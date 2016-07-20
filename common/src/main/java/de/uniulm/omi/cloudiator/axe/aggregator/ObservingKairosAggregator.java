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

import com.google.common.base.Throwables;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.TimeWindow;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.Measurement;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer;
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
 * Created by Frank on 06.08.2015.
 */
public class ObservingKairosAggregator extends RawKairosAggregator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentSchedule;
    // only one output in reduce:
    private final List<Long> idMonitorInstance;

    public ObservingKairosAggregator(KairosDbService kairos, RawMonitor monitor,
        List<Long> idMonitorInstance, Map<String, List<MonitorInstance>> monitorListMap) {
        super(kairos, monitorListMap, monitor);
        this.idMonitorInstance = idMonitorInstance;
    }

    public List<Long> getIdMonitorInstance() {
        return idMonitorInstance;
    }

    @Override public void aggregate() {
        /*********
         *
         * Algorithm:
         *
         * 1) TODO
         *
         *
         */

        List<Double> values = new ArrayList<Double>();
        Double newValue = 0d;
        int index = 0;

        for (Map.Entry<String, List<MonitorInstance>> entry : getMonitorListMap().entrySet()) {
            String metricName = entry.getKey();
            for (MonitorInstance instance : entry.getValue()) {
                List<String> tagValues = new ArrayList<String>();
                tagValues.add(String.valueOf(instance.getId()));

                List<Double> kairosAggregatedValues = getKairos()
                    .getKairos(getIpCache().getIp(instance.getIpAddress()), getKairos().getDefaultPort() /* TODO dynamic storing per VM */)
                    .getAggregatedValue(metricName, tagValues, FormulaOperator.LAST,
                        new TimeWindow(0, getMonitor().getSchedule().getInterval() * 2,
                            getMonitor().getSchedule().getTimeUnit()), (long) Utils
                            .timeToMilliseconds(getMonitor().getSchedule().getTimeUnit(),
                                getMonitor().getSchedule().getInterval()) /* * 2 todo define offset*/,
                        getMonitor().getSchedule());

                if (kairosAggregatedValues.isEmpty()) {
                    LOGGER.error(
                        "No Values aggregated for: " + metricName + " with tags " + tagValues
                            .toString());
                } else {
                    values.addAll(kairosAggregatedValues);

                    newValue = Calc.calculate(FormulaOperator.LAST, values);

                    // TODO here the trick is not write - is this correct?
                    //List<Tag> tags = new ArrayList<>();
                    //tags.add(new Tag("monitorinstance", String.valueOf(getIdMonitorInstance().get(index))));
                    //
                    //getKairos().getLocalKairos().write("aggregation", tags, newValue);

                    for (Observer observer : this.getObservers()) {
                        if (observer.isViolated(newValue)) {
                            observer.update(new Measurement(getMonitor().getId(),
                                getIdMonitorInstance().get(index), newValue,
                                System.currentTimeMillis()));
                        }
                    }
                }


                index++;
                values = new ArrayList<Double>();
            }
        }
    }

    @Override public void run() {
        try {
            this.aggregate();

        } catch (Exception e) {
            LOGGER.error("Monitor: " + getMonitor().getId() + " failed setting values! (1), exception: "+ Throwables.getStackTraceAsString(e));
        }
    }

    @Override public void schedule() {

        long timeInMilliSeconds = 0;
        long delayInMilliSeconds = 0;


        timeInMilliSeconds = Utils.timeToMilliseconds(getMonitor().getSchedule().getTimeUnit(),
            getMonitor().getSchedule().getInterval());


        currentSchedule = scheduler
            .scheduleAtFixedRate(this, delayInMilliSeconds, timeInMilliSeconds,
                TimeUnit.MILLISECONDS);

        /* TODO: Implement to kill thread after certain time. */
        /* TODO: Implement to count repetitions if applicable. */
    }

    @Override public void unschedule() {
        if (currentSchedule != null) {
            currentSchedule.cancel(false);
        }
    }

    @Override public ComposedMonitor getComposedMonitor() {
        return null; /*TODO instead of implementing this, i have to restructure the parent class / inheritance
         best to not use inheritance but composition, instead if one class per TSDB, make class TsdbProvider
         that is used in each aggregator. */
    }
}
