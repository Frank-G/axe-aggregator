/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.uniulm.omi.cloudiator.axe.aggregator;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.MeasurementWindow;
import de.uniulm.omi.cloudiator.axe.aggregator.utils.Calc;
import de.uniulm.omi.cloudiator.colosseum.client.entities.MonitorInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frank on 25.03.2015.
 */
public class QuantifiedReduceKairosAggregator extends ComposedKairosAggregator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentSchedule;
    // only one output in reduce:
    private final long idMonitorInstance;
    private final ConstantMonitor mappedMonitor;
    private final int minimumApplies;

    public QuantifiedReduceKairosAggregator(KairosDbService kairos, ComposedMonitor monitor,
        long idMonitorInstance, Map<String, List<MonitorInstance>> monitorListMap,
        ConstantMonitor mappedMonitor) {
        super(kairos, monitorListMap, monitor);
        this.idMonitorInstance = idMonitorInstance;
        this.mappedMonitor = mappedMonitor;
        int amountInstances = 0;
        for (Map.Entry<String, List<MonitorInstance>> entry : monitorListMap.entrySet()) {
            amountInstances += entry.getValue().size();
        }
        if (monitor.getQuantifier().isRelative()) {
            minimumApplies = (int) Math.ceil(monitor.getQuantifier().getValue() * amountInstances);
        } else {
            minimumApplies = (int) monitor.getQuantifier().getValue();
        }
    }

    public long getIdMonitorInstance() {
        return idMonitorInstance;
    }

    @Override public void aggregate() {
        /*********
         *
         * Algorithm:
         *
         * 1) Collect all values of the monitor instances
         *  - if the aggregation function is kairos-compatible
         *      - sort by metric name
         *      - get the aggregated value per metric name
         * 2) Calculate amount of output values based on quantifier
         * 2) Calculate the function of all (aggregagted) values
         *      for each group (quantifier based, 1 = 1 output values,
         *      0.5 groups of half of the size of the instances)
         * 3) Save the new value(s) to database
         *
         *
         */


        if (getComposedMonitor().getQuantifier().isRelative()
            && getComposedMonitor().getQuantifier().getValue() > 0.99 /*ungenauigkeit*/) {
            //special case for relative with 1:
            //relative
            if (this.mappedMonitor == null) {
                this.calculateAllValues();
            } else {
                calculateQuantifiedValuesWithMappedMonitor(mappedMonitor.getValue());
            }

        } else if (getComposedMonitor().getQuantifier().isRelative()) {
            //relative

            if (this.mappedMonitor == null) {
                //TODO
                LOGGER
                    .error("Relative quantifiers without mapped monitor are not implemented atm.");
            } else {
                calculateQuantifiedValuesWithMappedMonitor(mappedMonitor.getValue());
            }
        } else {
            //non-relative

            if (this.mappedMonitor == null) {
                //TODO
                LOGGER.error(
                    "Non-relative quantifiers without mapped monitor are not implemented atm.");
            } else {
                calculateQuantifiedValuesWithMappedMonitor(mappedMonitor.getValue());
            }
        }
    }

    protected void calculateAllValues() {

        Double newValue = 0d;

        List<Double> values = this.getValuesFromKairosDB();

        newValue = Calc.calculate(getComposedMonitor().getFunction(), values);

        this.updateValue(newValue, getIdMonitorInstance());
    }

    protected void calculateQuantifiedValuesWithMappedMonitor(double mappedMonitorValue) {

        Double newValue = 0d;

        List<Double> values = this.getValuesFromKairosDB();

        newValue = Calc.calculate(getComposedMonitor().getFunction(), values, minimumApplies,
            mappedMonitorValue);

        this.updateValue(newValue, getIdMonitorInstance());
    }

    protected List<Double> getValuesFromKairosDB() {
        List<Double> values = new ArrayList<Double>();

        for (Map.Entry<String, List<MonitorInstance>> entry : getMonitorListMap().entrySet()) {
            String metricName = entry.getKey();

            //mapped to ip and list of tag values
            Map<String, List<String>> tagValues = new HashMap<String, List<String>>();
            List<Double> kairosAggregatedValues = new ArrayList<Double>();


            if (getKairos().getLocalKairos()
                .isAggregationMappable(getComposedMonitor().getFunction())) {

                for (MonitorInstance instance : entry.getValue()) {
                    String ipAddress = getIpCache().getIp(instance.getIpAddress());
                    if (!tagValues.containsKey(ipAddress)) {
                        tagValues.put(ipAddress, new ArrayList<String>());
                    }
                    tagValues.get(ipAddress).add(String.valueOf(instance.getId()));
                }

                for (String key : tagValues.keySet()) {
                    kairosAggregatedValues = getKairos().
                        getKairos(key, getKairos().getDefaultPort() /* TODO dynamic storing per VM */)
                        .getAggregatedValue(metricName, tagValues.get(key),
                            getComposedMonitor().getFunction(), getComposedMonitor().getWindow(),
                            (long) Utils.timeToMilliseconds(
                                getComposedMonitor().getSchedule().getTimeUnit(),
                                getComposedMonitor().getSchedule().getInterval()) /* * 2 todo define offset*/,
                            getComposedMonitor().getSchedule());

                    //Use as many measurements as in the window if its a MeasurementWindow
                    //since kairos cant handle the aggregation here:
                    if (getComposedMonitor().getWindow() instanceof MeasurementWindow) {
                        MeasurementWindow mw = (MeasurementWindow) getComposedMonitor().getWindow();
                        int startingIndex;
                        if (mw.getMeasurements() >= kairosAggregatedValues.size()){
                            startingIndex = 0;
                        } else {
                            startingIndex = kairosAggregatedValues.size() - mw.getMeasurements();
                        }

                        values.addAll(kairosAggregatedValues.subList(startingIndex, kairosAggregatedValues.size()));
                    } else {
                        values.add(kairosAggregatedValues.get(kairosAggregatedValues.size() - 1));
                    }
                }

            } else {
                for (MonitorInstance instance : entry.getValue()) {
                    String onlyEntry = "single";
                    List<String> singleId = new ArrayList<>();
                    singleId.add(String.valueOf(instance.getId()));
                    tagValues.put(onlyEntry, singleId);

                    kairosAggregatedValues = getKairos()
                        .getKairos(getIpCache().getIp(instance.getIpAddress()), getKairos().getDefaultPort() /* TODO dynamic storing per VM */)
                        .getAggregatedValue(metricName, tagValues.get(onlyEntry),
                            getComposedMonitor().getFunction(), getComposedMonitor().getWindow(),
                            (long) Utils.timeToMilliseconds(
                                getComposedMonitor().getSchedule().getTimeUnit(),
                                getComposedMonitor().getSchedule().getInterval()) /* * 2 todo define offset*/,
                            getComposedMonitor().getSchedule());

                    if (!kairosAggregatedValues.isEmpty()) {
                        // first value: oldest; latest value: newest.
                        // last: kairosAggregatedValues.get(kairosAggregatedValues.size()-1)
                        // first: kairosAggregatedValues.get(0)
                        //values.add(kairosAggregatedValues.get(kairosAggregatedValues.size()-1));
                        values.addAll(kairosAggregatedValues);
                    } else {
                        LOGGER.error(
                            "No values aggregated for: " + metricName + " with tags " + tagValues
                                .toString());
                    }

                    tagValues = new HashMap<String, List<String>>();
                }
            }
            // TODO weight the aggregated value by the amount of metricinstances

            //    for(int i = 0; i <= tagValues.size(); i++){
            //        values.add(kairosAggregatedValues.get(kairosAggregatedValues.size()-1-i));
            //    }
            //    //values.addAll(kairosAggregatedValues);
            //}
            //values.add(kairosAggregatedValues.get(0));
        }

        return values;
    }

    @Override public void run() {
        try {
            this.aggregate();

        } catch (Exception e) {
            LOGGER.error("Monitor: " + getComposedMonitor().getId() + " failed setting values! (3)");
            e.printStackTrace();
        }
    }

    @Override public void schedule() {
        unschedule();


        long timeInMilliSeconds = 0;
        long delayInMilliSeconds = 0;


        timeInMilliSeconds = Utils
            .timeToMilliseconds(getComposedMonitor().getSchedule().getTimeUnit(),
                getComposedMonitor().getSchedule().getInterval());


        currentSchedule = scheduler
            .scheduleAtFixedRate(this, delayInMilliSeconds, timeInMilliSeconds,
                TimeUnit.MILLISECONDS);

        /* TODO: Implement to kill thread after certain time. */
        /* TODO: Implement to count repetitions if applicable. */
    }

    @Override public void unschedule() {
        if (currentSchedule != null) {
            if (!currentSchedule.cancel(false)) {
                LOGGER.error("Aggregator could not be canceled: " + this.getMonitorId());
            }
        }
    }
}
