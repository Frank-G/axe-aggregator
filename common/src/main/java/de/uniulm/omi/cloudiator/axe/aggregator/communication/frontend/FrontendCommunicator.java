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

package de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.ColosseumDetails;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.*;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Monitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Window;

import java.util.List;

/**
 * Created by Frank on 23.07.2015.
 */
public interface FrontendCommunicator {

    void updateCredentials(String protocol, String ip, int port, String username, String tenant,
        String password);

    void updateCredentials(ColosseumDetails colosseumDetails);

    List<VirtualMachine> getVirtualMachines(long applicationId, long componentId, long instanceId,
        long cloudId);

    List<Instance> getInstances(long vm);

    ApplicationComponent getApplicationComponentForInstance(long appCompId);

    long getIdFromLink(String link);

    String getPublicAddressOfVM(VirtualMachine vm);

    List<LifecycleComponent> getComponents(long applicationId, long componentId, long instanceId,
        long cloudId);

    boolean isInstanceOf(Instance instance, List<ApplicationComponent> appComps,
        LifecycleComponent component);

    boolean isInstanceOf(Instance instance, List<VirtualMachine> vms, long cloudId);

    String getIpAddress(long idIpAddress);

    long getIdPublicAddressOfVM(VirtualMachine vm);

    Long getVirtualMachineToIP(String ipAddress);

    Long getApplicationIdByName(String name);

    Long getComponentIdByName(String name);

    List<MonitorInstance> getMonitorInstances(long idMonitor);

    List<Long> getMonitorInstanceIDs(long idMonitor);

    String getPublicAddressByMetricInstance(MonitorInstance instance);

    MonitorInstance getMonitorInstance(Long idMonitorInstance);

    Schedule getSchedule(Long id);

    FormulaQuantifier getQuantifier(Long id);

    Window getWindow(Long id);

    List<Monitor> getMonitors(List<Long> ids);

    SensorDescription getSensorDescription(Long id);

    ComposedMonitor getComposedMonitor(Long id);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor getRawMonitor(Long id);

    ConstantMonitor getConstantMonitor(Long id);
}
