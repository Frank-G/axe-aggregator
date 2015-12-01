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

import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.FrontendCommunicator;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.Monitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.MonitorInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/**
 * Created by Frank on 25.03.2015.
 */
public class AggregatorService {
    private static AggregatorService instance = null;
    private final static int MAX_RETRY_ADD_OBSERVER = 5;
    private final List<Aggregator> aggregators = new ArrayList<Aggregator>();
    private final FrontendCommunicator fc;
    private final KairosDbService kairosDb;
    private final IpCache ipCache;
    private final static String COMPOSED_METRIC_NAME = "aggregation";
    public static final Logger LOGGER = LogManager.getLogger(AggregatorService.class);
    private final String homeDomainIP;

    synchronized public void addAggregator(ComposedMonitor monitor) {

        ConstantMonitor singleConstantMonitor =
            this.getSingleConstantMonitor(monitor.getMonitors());
        Aggregator aggregator = null;
        Map<String, List<MonitorInstance>> monitorMap =
            new HashMap<String, List<MonitorInstance>>();

        for (Monitor obj : monitor.getMonitors()) {
            if (obj instanceof ConstantMonitor) {
                /**TODO*/
            } else if (obj instanceof RawMonitor) {
                List<MonitorInstance> instances = fc.getMonitorInstances(obj.getId());
                String metricName = ((RawMonitor) obj).getConfig().getMetricName();

                if (monitorMap.get(metricName) == null) {
                    monitorMap.put(metricName, instances);
                } else {
                    monitorMap.get(metricName).addAll(instances);
                }

            } else if (obj instanceof ComposedMonitor) {
                List<MonitorInstance> instances = fc.getMonitorInstances(obj.getId());

                if (monitorMap.get(COMPOSED_METRIC_NAME) == null) {
                    monitorMap.put(COMPOSED_METRIC_NAME, instances);
                } else {
                    monitorMap.get(COMPOSED_METRIC_NAME).addAll(instances);
                }

            } else {
                throw new RuntimeException("Monitor type is not implemented!");
            }
        }

        List<Long> instanceIDs = fc.getMonitorInstanceIDs(monitor.getId());
        int rep = 0;
        while (instanceIDs.size() == 0 && rep < 5) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.error("Thread was interrupted on sleep.");
            }
            rep++;
            instanceIDs = fc.getMonitorInstanceIDs(monitor.getId());
            LOGGER.debug("retry receiving instances");
        }

        LOGGER.info("For Monitor " + monitor.getId() + " are " + instanceIDs.size()
            + " instances available: " + instanceIDs.toString());

        switch (monitor.getFlowOperator()) {
            case REDUCE: {
                if (instanceIDs.size() != 1) {
                    throw new RuntimeException(
                        "REDUCE should just have one output monitorinstance!");
                }
                aggregator =
                    new QuantifiedReduceKairosAggregator(kairosDb, monitor, instanceIDs.get(0),
                        monitorMap, singleConstantMonitor);
            }
            break;
            case MAP: {
                if (instanceIDs.size() <= 0) {
                    throw new RuntimeException(
                        "MAP should have at least one output monitorinstance!");
                }
                aggregator =
                    new QuantifiedMapKairosAggregator(kairosDb, monitor, instanceIDs, monitorMap,
                        singleConstantMonitor);
            }
            break;
            default:
                throw new RuntimeException("Flow Operator is not implemented!");
        }

        aggregator.setIpCache(this.ipCache);
        aggregator.schedule();
        aggregators.add(aggregator);

    }

    private ConstantMonitor getSingleConstantMonitor(List<Monitor> monitors) {
        int found = 0;
        ConstantMonitor result = null;

        Iterator<Monitor> iterator = monitors.iterator();
        while (found < 2 && iterator.hasNext()) {
            Monitor obj = iterator.next();

            if (obj instanceof ConstantMonitor) {
                result = (ConstantMonitor) obj;
                found++;
            }
        }

        if (found > 1) {
            result = null;
        }

        return result;
    }

    //    synchronized
    //    public boolean updateAggregator(ComposedMonitor monitor) throws Exception {
    //        System.out.println("Start Updating Monitor: " + monitor.getIdMonitor());
    //
    //        boolean found = false;
    //        int index = 0;
    //        while(!found && index < aggregators.size()){
    //            if(aggregators.get(index).getMonitorId() == monitor.getIdMonitor()){
    //                System.out.println("Done Updating Monitor: " + monitor.getIdMonitor());
    //
    //                Aggregator agg = aggregators.get(index);
    //
    //                // if(compareComposedMonitor(agg.getComposedMonitor(), monitor)){
    //                aggregators.get(index).unschedule();
    //                aggregators.remove(index);
    //
    //                this.addAggregator(monitor);
    //
    //                found = true;
    //            } else {
    //                System.out.println(monitor.getIdMonitor() + " is not " + aggregators.get(index).getMonitorId());
    //            }
    //            index++;
    //        }
    //
    //        return true; // nothing changed
    //    }

    synchronized public void removeAggregator(ComposedMonitor monitor) {
        System.out.println("Start Deleting Monitor: " + monitor.getId());

        boolean found = false;
        int index = 0;
        while (!found && index < aggregators.size()) {
            if (aggregators.get(index).getMonitorId() == monitor.getId()) {
                System.out.println("Done Deleting Monitor: " + monitor.getId());

                aggregators.get(index).unschedule();
                aggregators.remove(index);
                found = true;
            } else {
                System.out
                    .println(monitor.getId() + " is not " + aggregators.get(index).getMonitorId());
            }
            index++;
        }
    }

    synchronized public void addObserverToMonitor(Long id,
        de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer observer) {
        addObserverToMonitor(id, observer, 0);
    }

    synchronized public void addObserverToMonitor(Long id,
        de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer observer, int retry) {
        boolean found = false;
        for (Aggregator agg : aggregators) {
            if (agg.getMonitorId() == id) {
                found = true;
                agg.addObservers(observer);
            }
        }

        if (found == false && retry < MAX_RETRY_ADD_OBSERVER) {

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                LOGGER.error("Could not add observer to monitor due to retry-interruption");
                e.printStackTrace();
            }

            addObserverToMonitor(id, observer, ++retry);
        }
    }

    synchronized public void removeObserverFromMonitor(String identifier) {
        for (Aggregator agg : aggregators) {
            List<de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer> toRemove =
                new ArrayList();
            for (de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer ob : agg
                .getObservers()) {
                if (ob.getExternalId().equals(identifier)) {
                    toRemove.add(ob);
                }
            }
            for (de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer ob : toRemove) {
                agg.getObservers().remove(ob);
            }
        }
    }

    synchronized public void removeObserver(Long id) {
        for (Aggregator agg : aggregators) {
            if (agg.getMonitorId() == id) {
                agg.getObservers().clear();
            }
        }
    }

    private AggregatorService(FrontendCommunicator fc, String homeDomainIP) {
        this.fc = fc;
        this.kairosDb = KairosDbService.getInstance();
        this.homeDomainIP = homeDomainIP;
        this.ipCache = IpCache.create(fc, homeDomainIP);
    }

    public static AggregatorService getService(FrontendCommunicator fc, String homeDomainIP) {
        if (instance == null) {
            instance = new AggregatorService(fc, homeDomainIP);
        }

        return instance;
    }

    public FrontendCommunicator getFc() {
        return fc;
    }
}
