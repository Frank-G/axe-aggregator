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

package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi;

import de.uniulm.omi.cloudiator.axe.aggregator.AggregatorService;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.FrontendCommunicator;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.Creation;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.ObserverParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Frank on 20.08.2015.
 */
public class AggregatorServiceAccessImpl implements AggregatorServiceAccess {

    private final FrontendCommunicator fc;
    private final AggregatorService as;
    public static final Logger LOGGER = LogManager.getLogger(AggregatorServiceAccessImpl.class);

    public AggregatorServiceAccessImpl(FrontendCommunicator fc) {
        this.fc = fc;
        this.as = AggregatorService.getService(this.fc);
    }

    @Override public void doAggregation(Long idMonitor) throws RemoteException {
        try {
            as.addAggregator(fc.getComposedMonitor(idMonitor));
        } catch (Exception e) {
            LOGGER.error("Could not aggregate: " + idMonitor);
            e.printStackTrace();
            //TODO add exception handling
        }
    }

    @Override public void doAggregation(Long idMonitor, List<Long> monitorInstances)
        throws RemoteException {
        throw new RuntimeException("Not yet implemented!"); //TODO filter for monitorInstances
    }

    @Override public void stopAggregation(Long idMonitor) throws RemoteException {
        try {
            as.removeAggregator(fc.getComposedMonitor(idMonitor));
        } catch (Exception e) {
            LOGGER.error("Could not stop aggregation: " + idMonitor);
            e.printStackTrace();
            //TODO add exception handling
        }
    }

    @Override public void stopAggregation(Long idMonitor, List<Long> monitorInstances)
        throws RemoteException {
        throw new RuntimeException("Not yet implemented!"); //TODO filter for monitorInstances
    }

    @Override public void updateCache() throws RemoteException {
        //throw new RuntimeException("Not yet implemented!");
        //TODO update ipCache
        //TODO update EVERY aggregator since the monitor informtion is cached - add interface
    }

    @Override public void stopAll() throws RemoteException {
        throw new RuntimeException("Not yet implemented!");
        //TODO add removeAll to aggregatorservice and call here
    }

    @Override public void setColosseum(ColosseumDetails details) throws RemoteException {
        this.fc.updateCredentials(details.getProtocol(), details.getIp(), details.getPort(),
            details.getUsername(), details.getTenant(), details.getPassword());
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override public void addObserver(Long id, ObserverParameter params) {
        as.addObserverToMonitor(id, Creation.createObserver(params));
    }

    @Override public void removeObserver(String externalReference) {
        as.removeObserverFromMonitor(externalReference);
    }

    @Override public void removeObserver(Long id) {
        as.removeObserver(id);
    }
}
