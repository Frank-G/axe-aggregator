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

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.ObserverParameter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Frank on 20.08.2015.
 */
public interface AggregatorServiceAccess extends Remote {

    void doAggregation(Long idMonitor) throws RemoteException;

    void doAggregation(Long idMonitor, List<Long> monitorInstances) throws RemoteException;

    void stopAggregation(Long idMonitor) throws RemoteException;

    void stopAggregation(Long idMonitor, List<Long> monitorInstances) throws RemoteException;

    void addObserver(Long id, ObserverParameter params) throws RemoteException;

    void removeObserver(String externalReference) throws RemoteException;

    void removeObserver(Long id) throws RemoteException;

    void updateCache() throws RemoteException;

    void stopAll() throws RemoteException;

    void setColosseum(ColosseumDetails details) throws RemoteException;
}
