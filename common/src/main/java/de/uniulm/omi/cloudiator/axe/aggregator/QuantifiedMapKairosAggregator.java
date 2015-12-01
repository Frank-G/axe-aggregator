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
import de.uniulm.omi.cloudiator.axe.aggregator.utils.Calc;
import de.uniulm.omi.cloudiator.colosseum.client.entities.MonitorInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frank on 25.03.2015.
 */
public class QuantifiedMapKairosAggregator extends ComposedKairosAggregator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentSchedule;
    // only one output in reduce:
    private final List<Long> idMonitorInstance;
    private final ConstantMonitor mappedMonitor;

    public QuantifiedMapKairosAggregator(KairosDbService kairos, ComposedMonitor monitor,
        List<Long> idMonitorInstance, Map<String, List<MonitorInstance>> monitorListMap,
        ConstantMonitor mappedMonitor) {
        super(kairos, monitorListMap, monitor);
        this.idMonitorInstance = idMonitorInstance;
        this.mappedMonitor = mappedMonitor;
    }

    public List<Long> getIdMonitorInstance() {
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

        List<Double> values = new ArrayList<Double>();
        Double newValue = 0d;
        int index = 0;

        for (Map.Entry<String, List<MonitorInstance>> entry : getMonitorListMap().entrySet()) {
            //special case:

            String metricName = entry.getKey();

            if (getComposedMonitor().getQuantifier().isRelative()
                && getComposedMonitor().getQuantifier().getValue() > 0.99 /*ungenauigkeit*/) {
                if (this.mappedMonitor == null) {
                    // just use function on each metricinstance
                    for (MonitorInstance instance : entry.getValue()) {
                        List<String> tagValues = new ArrayList<String>();
                        tagValues.add(String.valueOf(instance.getId()));

                        List<Double> kairosAggregatedValues = getKairos()
                            .getKairos(getIpCache().getIp(instance.getIpAddress()), getKairos().getDefaultPort() /* TODO dynamic storing per VM */)
                            .getAggregatedValue(metricName, tagValues,
                                getComposedMonitor().getFunction(),
                                getComposedMonitor().getWindow(), (long) Utils.timeToMilliseconds(
                                    getComposedMonitor().getSchedule().getTimeUnit(),
                                    getComposedMonitor().getSchedule().getInterval()) /* * 2 todo define offset*/,
                                getComposedMonitor().getSchedule());

                        if (kairosAggregatedValues.isEmpty()) {
                            LOGGER.error("No Values aggregated for: " + metricName + " with tags "
                                + tagValues.toString());
                        } else {
                            //TODO use all or just the last?
                            //values.addAll(kairosAggregatedValues);
                            //values.add(kairosAggregatedValues.get(0));
                            //values.add(kairosAggregatedValues.get(kairosAggregatedValues.size()-1));
                            //Maybe if Kairos aggregated the values - use last, if not use all?
                            if (getKairos().getLocalKairos()
                                .isAggregationMappable(getComposedMonitor().getFunction())) {
                                values.add(
                                    kairosAggregatedValues.get(kairosAggregatedValues.size() - 1));
                            } else {
                                values.addAll(kairosAggregatedValues);
                            }


                            newValue = Calc.calculate(getComposedMonitor().getFunction(), values);

                            updateValue(newValue, getIdMonitorInstance().get(index));
                        }


                        index++;
                        values = new ArrayList<Double>();
                    }
                } else if (this.mappedMonitor != null) {
                    // use second value on each metricinstance
                    for (MonitorInstance instance : entry.getValue()) {
                        List<String> tagValues = new ArrayList<String>();
                        tagValues.add(String.valueOf(instance.getId()));

                        List<Double> kairosAggregatedValues = getKairos()
                            .getKairos(getIpCache().getIp(instance.getIpAddress()), getKairos().getDefaultPort() /* TODO dynamic storing per VM */)
                            .getAggregatedValue(metricName, tagValues,
                                getComposedMonitor().getFunction(),
                                getComposedMonitor().getWindow(), (long) Utils.timeToMilliseconds(
                                    getComposedMonitor().getSchedule().getTimeUnit(),
                                    getComposedMonitor().getSchedule().getInterval()) /* * 2 todo define offset*/,
                                getComposedMonitor().getSchedule());

                        if (kairosAggregatedValues.isEmpty()) {
                            LOGGER.error("No Values aggregated for: " + metricName + " with tags "
                                + tagValues.toString());
                        } else {
                            //TODO use all or just the last?
                            //values.addAll(kairosAggregatedValues);
                            //values.add(kairosAggregatedValues.get(0));
                            //values.add(kairosAggregatedValues.get(kairosAggregatedValues.size()-1));
                            //Maybe if Kairos aggregated the values - use last, if not use all?
                            if (getKairos().getLocalKairos()
                                .isAggregationMappable(getComposedMonitor().getFunction())) {
                                values.add(
                                    kairosAggregatedValues.get(kairosAggregatedValues.size() - 1));
                            } else {
                                values.addAll(kairosAggregatedValues);
                            }


                            newValue = Calc.calculate(getComposedMonitor().getFunction(), values,
                                values.size(), mappedMonitor.getValue());

                            updateValue(newValue, getIdMonitorInstance().get(index));
                        }


                        index++;
                        values = new ArrayList<Double>();
                    }
                }
            } else if (getComposedMonitor().getQuantifier().isRelative()) {
                //relative
                //TODO
                LOGGER.error("Other relative quantifiers than 1 is not allowed atm.");

                if (this.mappedMonitor == null) {

                } else {

                }
            } else {
                //non-relative
                //TODO
                LOGGER.error("Non-relative quantifiers are not allowed atm.");

                if (this.mappedMonitor == null) {

                } else {

                }
            }
        }
    }

    @Override public void run() {
        try {
            this.aggregate();

        } catch (Exception e) {
            System.out.println(
                "MetricInstance: " + getComposedMonitor().getId() + " ERROR setting values!");
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
