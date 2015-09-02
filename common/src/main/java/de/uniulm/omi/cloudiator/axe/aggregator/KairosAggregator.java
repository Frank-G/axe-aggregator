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

import de.uniulm.omi.cloudiator.axe.aggregator.observer.Measurement;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer;
import de.uniulm.omi.cloudiator.colosseum.client.entities.MonitorInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Frank on 29.03.2015.
 */
public abstract class KairosAggregator implements Aggregator {
    private final KairosDbService kairos;
    private final List<Observer> observers = new ArrayList<Observer>();
    private IpCache ipCache;
    public static final Logger LOGGER = LogManager.getLogger(KairosAggregator.class);
    private final Map<String, List<MonitorInstance>> monitorListMap;

    public KairosAggregator(KairosDbService kairos,
        Map<String, List<MonitorInstance>> monitorListMap) {
        this.kairos = kairos;
        this.monitorListMap = monitorListMap;
    }

    public KairosDbService getKairos() {
        return kairos;
    }

    public Map<String, List<MonitorInstance>> getMonitorListMap() {
        return monitorListMap;
    }

    @Override public List<Observer> getObservers() {
        return this.observers;
    }

    @Override public void addObservers(Observer obs) {
        this.getObservers().add(obs);
    }

    @Override public void notifyObservers(Measurement o) {
        for (Observer obs : this.getObservers()) {
            if (obs.isViolated(o.getMeasurement())) {
                obs.update(o);
            }
        }
    }

    @Override public void setIpCache(IpCache ipCache) {
        this.ipCache = ipCache;
    }

    @Override public IpCache getIpCache() {
        return this.ipCache;
    }

    protected void updateValue(Double newValue, Long idMonitorInstance) {

        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("monitorinstance", String.valueOf(idMonitorInstance)));


        // TODO not just write in local kairos
        if (newValue == Double.NaN) {
            LOGGER.error("No values could be aggregated: newValue is NaN");
        } else {
            getKairos().getLocalKairos().write("aggregation", tags, newValue);

            notifyObservers(new Measurement(idMonitorInstance, idMonitorInstance, newValue,
                System.currentTimeMillis()));
        }
    }
}
