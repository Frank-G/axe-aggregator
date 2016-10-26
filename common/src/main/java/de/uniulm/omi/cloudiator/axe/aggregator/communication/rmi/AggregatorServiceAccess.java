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

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ObserverParameter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * AggregatorServiceAccess is the interface to the aggregator service.
 *
 * One object will be created by Axe and is accessed via RMI.
 *
 * Created by Frank on 20.08.2015.
 */
public interface AggregatorServiceAccess extends Remote {

    /**
     *
     * Starts the aggregation that is described by the referenced monitor.
     *
     * @param idMonitor The ID of the referenced composed monitor that describes the aggregation.
     * @throws RemoteException
     */
    void doAggregation(Long idMonitor) throws RemoteException;

    /**
     *
     * Starts the aggregation that is described by the referenced monitor for specific
     * monitor instances.
     *
     * Note: not implemented in the current version
     *
     * @param idMonitor The ID of the referenced composed monitor that describes the aggregation.
     * @param monitorInstances The IDs of the specific monitor instances
     * @throws RemoteException
     */
    void doAggregation(Long idMonitor, List<Long> monitorInstances) throws RemoteException;

    /**
     *
     * Stops the aggregation that is described by the referenced monitor.
     *
     * @param idMonitor The ID of the referenced composed monitor that describes the aggregation.
     * @throws RemoteException
     */
    void stopAggregation(Long idMonitor) throws RemoteException;

    /**
     *
     * Stops the aggregation that is described by the referenced monitor for specific
     * monitor instances.
     *
     * Note: not implemented in the current version
     *
     * @param idMonitor
     * @param monitorInstances
     * @throws RemoteException
     */
    void stopAggregation(Long idMonitor, List<Long> monitorInstances) throws RemoteException;

    /**
     *
     * Adds an observer to a specific composed monitor.
     *
     * @param id The ID of the composed monitor that needs observation.
     * @param params The parameters for a the observer.
     * @throws RemoteException
     */
    void addObserver(Long id, ObserverParameter params) throws RemoteException;

    /**
     *
     * Removes the observer that has a specific external reference.
     *
     * @param externalReference The external reference to look for in the observers.
     * @throws RemoteException
     */
    void removeObserver(String externalReference) throws RemoteException;

    /**
     *
     * Removes the observer from a monitor.
     *
     * @param id The ID of the composed monitor for which the observer should be removed.
     * @throws RemoteException
     */
    void removeObserver(Long id) throws RemoteException;

    /**
     *
     * Updates the local cache of the colosseum entities.
     *
     * @throws RemoteException
     */
    void updateCache() throws RemoteException;

    /**
     *
     * Stops all aggregations.
     *
     * @throws RemoteException
     */
    void stopAll() throws RemoteException;

    /**
     *
     * Sets the details of the colosseum to connect to.
     *
     * @param details The details of the linked colosseum.
     * @throws RemoteException
     */
    void setColosseum(ColosseumDetails details) throws RemoteException;

    /**
     *
     * Check if the aggregation service is ready and accessible.
     *
     * @return True if the aggregation service is ready and accessible.
     * @throws RemoteException
     */
    boolean ping() throws RemoteException;
}
