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
import de.uniulm.omi.cloudiator.axe.aggregator.entities.converter.Converter;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.converter.ConverterImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.utils.Check;
import de.uniulm.omi.cloudiator.colosseum.client.Client;
import de.uniulm.omi.cloudiator.colosseum.client.ClientBuilder;
import de.uniulm.omi.cloudiator.colosseum.client.ClientController;
import de.uniulm.omi.cloudiator.colosseum.client.SingletonFactory;
import de.uniulm.omi.cloudiator.colosseum.client.entities.*;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Monitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.ScalingAction;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Window;
import de.uniulm.omi.cloudiator.colosseum.client.entities.internal.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 23.07.2015.
 */
public class RemoteFrontendCommunicator implements FrontendCommunicator {

    private static List<FrontendCommunicator> agents = new ArrayList<FrontendCommunicator>();

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getProtocol() {
        return protocol;
    }

    private String protocol;
    private String ip;
    private int port;
    private String username;
    private String tenant;
    private String password;
    private Client cl;
    private ClientBuilder clientBuilder;
    private SingletonFactory csf;
    private Converter converter;

    public RemoteFrontendCommunicator() {
        // in this case init is required afterwards
    }

    public RemoteFrontendCommunicator(String protocol, String ip, int port, String username,
        String tenant, String password) {
        this.init(protocol, ip, port, username, tenant, password);
    }

    public void init(String protocol, String ip, int port, String username, String tenant,
        String password) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.username = username;
        this.tenant = tenant;
        this.password = password;

        this.clientBuilder = ClientBuilder.getNew()
            // the base url
            .url(protocol + "://" + ip + ":" + port + "/api")
                // the login credentials
            .credentials(username, tenant, password);

        cl = clientBuilder.build();

        csf = new SingletonFactory(cl);

        converter = new ConverterImpl(this);
    }

    @Override public void updateCredentials(String protocol, String ip, int port, String username,
        String tenant, String password) {
        this.init(protocol, ip, port, username, tenant, password);
    }

    @Override public void updateCredentials(ColosseumDetails colosseumDetails) {
        this.init(colosseumDetails.getProtocol(), colosseumDetails.getIp(),
            colosseumDetails.getPort(), colosseumDetails.getUsername(),
            colosseumDetails.getTenant(), colosseumDetails.getPassword());
    }

    private <T extends Entity> ClientController<T> get(Class<T> type) {
        return cl.controller(type);
    }

/*    public static FrontendCommunicator getCommunicator(String protocol, String ip, int port, String username, String password){
        for(FrontendCommunicator agent : agents){
            if (agent.getProtocol().equals(protocol) && agent.getIp().equals(ip) && agent.getPort() == port){
                return agent;
            }
        }
        FrontendCommunicator agent = new RemoteFrontendCommunicator(protocol, ip, port, username, password);
        agents.add(agent);
        return agent;
    }*/

    @Override public List<VirtualMachine> getVirtualMachines(long applicationId, long componentId,
        long instanceId, long cloudId) {
        List<VirtualMachine> vms;
        List<VirtualMachine> result = new ArrayList<VirtualMachine>();

        ClientController<VirtualMachine> vmController = this.get(VirtualMachine.class);
        vms = vmController.getList();



        for (VirtualMachine vm : vms) {
            boolean suitable = true;
            List<Instance> instances = null;
            List<ApplicationComponent> appComps = null;


            // Filter for application id
            if (Check.idNotNull(applicationId)) {
                instances = this.getInstances(getIdFromLink(vm.getSelfLink()));
                appComps = new ArrayList<ApplicationComponent>();
                for (Instance instance : instances) {
                    if (instance.getVirtualMachine() == getIdFromLink(vm.getSelfLink())) {
                        System.out.println(
                            "Instance " + getIdFromLink(instance.getSelfLink()) + " belongs to VM "
                                + getIdFromLink(vm.getSelfLink()));
                        appComps.add(
                            getApplicationComponentForInstance(instance.getApplicationComponent()));
                    }
                }

                boolean oneInstanceFit = false;

                for (ApplicationComponent ac : appComps) {
                    if (ac.getApplication() == applicationId) {
                        oneInstanceFit = true;
                    }
                }

                suitable = oneInstanceFit;
            }

            // Filter for component id
            if (suitable && Check.idNotNull(componentId)) {
                if (instances == null) {
                    instances = this.getInstances(getIdFromLink(vm.getSelfLink()));
                    appComps = new ArrayList<ApplicationComponent>();
                    for (Instance instance : instances) {
                        appComps.add(
                            getApplicationComponentForInstance(instance.getApplicationComponent()));
                    }
                }

                boolean oneInstanceFit = false;

                for (ApplicationComponent ac : appComps) {
                    if (ac.getComponent() == componentId) {
                        oneInstanceFit = true;
                    }
                }

                suitable = oneInstanceFit;
            }

            // Filter for instance id
            if (suitable && Check.idNotNull(instanceId)) {
                if (instances == null) {
                    instances = this.getInstances(getIdFromLink(vm.getSelfLink()));
                }

                boolean oneInstanceFit = false;
                for (Instance instance : instances) {
                    if (getIdFromLink(instance.getSelfLink()) == instanceId) {
                        oneInstanceFit = true;
                    }
                }

                suitable = oneInstanceFit;
            }

            // Filter for cloud id
            if (suitable && Check.idNotNull(cloudId)) {
                if (vm.getCloud() != cloudId) {
                    suitable = false;
                }
            }

            // Add to result
            if (suitable) {
                result.add(vm);
            }
        }

        return result;
    }

    @Override public List<Instance> getInstances(long vm) {
        List<Instance> instances;
        List<Instance> result = new ArrayList<Instance>();

        ClientController<Instance> controller = this.get(Instance.class);
        instances = controller.getList();


        for (Instance instance : instances) {
            boolean suitable = true;


            // Filter for application id
            if (vm > 0 && vm != instance.getVirtualMachine()) {
                suitable = false;
            }

            if (suitable) {
                result.add(instance);
            }
        }

        return result;
    }

    @Override public ApplicationComponent getApplicationComponentForInstance(long appCompId) {
        ClientController<ApplicationComponent> controller = this.get(ApplicationComponent.class);
        return controller.get(appCompId);
    }

    @Override public long getIdFromLink(String link) {
        return Long.parseLong(link.substring(link.lastIndexOf('/') + 1));
    }

    @Override public String getPublicAddressOfVM(VirtualMachine vm) {
        long vmId = getIdFromLink(vm.getSelfLink());


        ClientController<IpAddress> controller = this.get(IpAddress.class);
        List<IpAddress> addresses = controller.getList();

        for (IpAddress ip : addresses) {
            /*TODO Not only return ONE, but EACH address */
            if (ip.getVirtualMachine() == vmId && ip.getIpType().equals("PUBLIC")) {
                return ip.getIp();
            }
        }

        return null;
    }

    @Override public List<LifecycleComponent> getComponents(long applicationId, long componentId,
        long instanceId, long cloudId) {
        List<LifecycleComponent> result = new ArrayList<LifecycleComponent>();
        List<LifecycleComponent> components = get(LifecycleComponent.class).getList();
        List<Instance> instances = null;
        List<VirtualMachine> vms = null;

        List<ApplicationComponent> appComps = get(ApplicationComponent.class).getList();


        for (LifecycleComponent component : components) {
            boolean suitable = false;

            if (Check.idNotNull(applicationId)) {
                for (ApplicationComponent ac : appComps) {
                    if (ac.getComponent() == componentId && ac.getApplication() == applicationId) {
                        suitable = true;
                    }
                }
            }



            if (Check.idNotNull(componentId)) {
                if (componentId == getIdFromLink(component.getSelfLink())) {
                    suitable = suitable && true;
                }
            }


            if (Check.idNotNull(instanceId)) {
                if (instances == null)
                    instances = get(Instance.class).getList();
                boolean oneFits = false;

                for (Instance instance : instances) {
                    if (isInstanceOf(instance, appComps, component)) {
                        oneFits = true;
                    }
                }

                if (oneFits) {
                    suitable = suitable && true;
                } else {
                    suitable = false;
                }
            }


            if (Check.idNotNull(cloudId)) {
                if (instances == null)
                    instances = get(Instance.class).getList();
                if (vms == null)
                    vms = get(VirtualMachine.class).getList();
                boolean oneFits = false;

                for (Instance instance : instances) {
                    if (isInstanceOf(instance, vms, cloudId)) {
                        if (isInstanceOf(instance, appComps, component)) {
                            oneFits = true;
                        }
                    }
                }

                if (oneFits) {
                    suitable = suitable && true;
                } else {
                    suitable = false;
                }
            }


            if (suitable) {
                result.add(component);
            }
        }



        return result;
    }

    @Override public boolean isInstanceOf(Instance instance, List<ApplicationComponent> appComps,
        LifecycleComponent component) {
        boolean result = false;

        long componentId = getIdFromLink(component.getSelfLink());

        for (ApplicationComponent ac : appComps) {
            long acId = getIdFromLink(ac.getSelfLink());

            if (instance.getApplicationComponent() == acId && ac.getComponent() == componentId) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean isInstanceOf(Instance instance, List<VirtualMachine> vms, long cloudId) {
        boolean result = false;

        for (VirtualMachine vm : vms) {
            long vmId = getIdFromLink(vm.getSelfLink());

            if (vm.getCloud() == cloudId && instance.getVirtualMachine() == vmId) {
                result = true;
            }
        }

        return result;
    }

    @Override public String getIpAddress(long idIpAddress) {
        return get(IpAddress.class).get(idIpAddress).getIp();
    }

    @Override public long getIdPublicAddressOfVM(VirtualMachine vm) {
        long vmId = getIdFromLink(vm.getSelfLink());


        ClientController<IpAddress> controller = this.get(IpAddress.class);
        List<IpAddress> addresses = controller.getList();

        for (IpAddress ip : addresses) {
            /*TODO Not only return ONE, but EACH address */
            if (ip.getVirtualMachine() == vmId && ip.getIpType().equals("PUBLIC")) {
                return getIdFromLink(ip.getSelfLink());
            }
        }

        return 0;
    }

    @Override public Long getVirtualMachineToIP(String ipAddress) {
        Long result = null;

        for (IpAddress ip : get(IpAddress.class).getList()) {
            if (ip.getIp().equals(ipAddress)) {
                result = ip.getVirtualMachine();
            }
        }

        return result;
    }

    @Override public Long getApplicationIdByName(String name) {
        Long result = null;

        for (de.uniulm.omi.cloudiator.colosseum.client.entities.Application app : get(
            de.uniulm.omi.cloudiator.colosseum.client.entities.Application.class).getList()) {
            if (app.getName().equals(name)) {
                result = getIdFromLink(app.getSelfLink());
                break;
            }
        }

        return result;
    }

    @Override public Long getComponentIdByName(String name) {
        Long result = null;

        /* TODO NOT ONLY LC COMPONENT */
        for (de.uniulm.omi.cloudiator.colosseum.client.entities.LifecycleComponent component : get(
            de.uniulm.omi.cloudiator.colosseum.client.entities.LifecycleComponent.class)
            .getList()) {
            if (component.getName().equals(name)) {
                result = getIdFromLink(component.getSelfLink());
                break;
            }
        }

        return result;
    }


    @Override public List<MonitorInstance> getMonitorInstances(long idMonitor) {
        List<MonitorInstance> result = new ArrayList<MonitorInstance>();


        ClientController<MonitorInstance> controller = this.get(MonitorInstance.class);
        List<MonitorInstance> instances = controller.getList();

        for (MonitorInstance instance : instances) {
            /*TODO Not only return ONE, but EACH address */
            if (instance.getMonitor() == idMonitor) {
                result.add(instance);
            }
        }

        return result;
    }

    @Override public List<Long> getMonitorInstanceIDs(long idMonitor) {
        List<MonitorInstance> monitorInstances = getMonitorInstances(idMonitor);
        List<Long> result = new ArrayList<Long>();

        for (MonitorInstance monitorInstance : monitorInstances) {
            result.add(monitorInstance.getId());
        }

        return result;
    }

    @Override public String getPublicAddressByMetricInstance(MonitorInstance instance) {

        ClientController<IpAddress> controller = this.get(IpAddress.class);
        List<IpAddress> instances = controller.getList();

        for (IpAddress ip : instances) {
            if (ip.getId().equals(instance.getIpAddress())) {
                return ip.getIp();
            }
        }

        return null;
    }

    @Override public MonitorInstance getMonitorInstance(Long idMonitorInstance) {

        ClientController<MonitorInstance> controller = this.get(MonitorInstance.class);
        return controller.get(idMonitorInstance);
    }

    @Override public Schedule getSchedule(Long id) {

        ClientController<Schedule> controller = this.get(Schedule.class);
        return controller.get(id);
    }

    @Override public FormulaQuantifier getQuantifier(Long id) {

        ClientController<FormulaQuantifier> controller = this.get(FormulaQuantifier.class);
        return controller.get(id);
    }

    @Override public Window getWindow(Long id) {
        List<TimeWindow> tws = this.get(TimeWindow.class).getList();
        List<MeasurementWindow> mws = this.get(MeasurementWindow.class).getList();

        for (TimeWindow w : tws) {
            if (w.getId().equals(id)) {
                return w;
            }
        }
        for (MeasurementWindow w : mws) {
            if (w.getId().equals(id)) {
                return w;
            }
        }

        return null;
    }

    @Override public List<Monitor> getMonitors(List<Long> ids) {
        List<Monitor> result = new ArrayList<>();

        List<RawMonitor> rawMonitors = this.get(RawMonitor.class).getList();
        List<ComposedMonitor> composedMonitors = this.get(ComposedMonitor.class).getList();
        List<ConstantMonitor> constantMonitors = this.get(ConstantMonitor.class).getList();

        for (Long id : ids) {
            for (RawMonitor m : rawMonitors) {
                if (m.getId().equals(id)) {
                    result.add(m);
                }
            }
            for (ComposedMonitor m : composedMonitors) {
                if (m.getId().equals(id)) {
                    result.add(m);
                }

            }
            for (ConstantMonitor m : constantMonitors) {
                if (m.getId().equals(id)) {
                    result.add(m);
                }
            }
        }

        return result;
    }

    @Override public SensorDescription getSensorDescription(Long id) {

        ClientController<SensorDescription> controller = this.get(SensorDescription.class);
        return controller.get(id);
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor getComposedMonitor(
        Long id) {

        ClientController<ComposedMonitor> controller = this.get(ComposedMonitor.class);
        return converter.convert(controller.get(id));
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor getRawMonitor(Long id) {

        ClientController<RawMonitor> controller = this.get(RawMonitor.class);
        return converter.convert(controller.get(id));
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor getConstantMonitor(
        Long id) {

        ClientController<ConstantMonitor> controller = this.get(ConstantMonitor.class);
        return converter.convert(controller.get(id));
    }

    @Override
    public int getAmountOfComponentInstances(Long appComponent) {
        int amount = 0;

        for(Instance i : this.get(Instance.class).getList()){
            if(this.get(ApplicationComponent.class).get(i.getApplicationComponent()).getId().equals(appComponent)){
                amount++;
            }
        }

        return amount;
    }

    @Override
    public void removeLatestComponentInstance(Long appComponent) {
        List<Instance> allInstances = this.get(Instance.class).getList();
        Instance toRemove = allInstances.get(allInstances.size() - 1);
        this.get(Instance.class).delete(toRemove);
    }

    @Override
    public void addAnotherComponentInstance(Long appComponent) {
        List<Instance> allInstances = this.get(Instance.class).getList();

        Instance anyInstance = null;

        for(Instance i : allInstances){
            ApplicationComponent ac = this.get(ApplicationComponent.class).get(i.getApplicationComponent());
            if (ac.getId().equals(appComponent)) {
                anyInstance = i;
                break;
            }
        }

        // add the same vm
        VirtualMachine vm = null;

        for (VirtualMachine x : this.get(VirtualMachine.class).getList()) {
            if (anyInstance.getVirtualMachine().equals(x.getId())) {
                //VirtualMachine vmOfAnyInstance = this.get(VirtualMachine.class).get(anyInstance.getVirtualMachine());

                vm = new VirtualMachine(
                        null,
                        null,
                        null,
                        null,
                        x.getCloud(),
                        x.getCloudCredentials(),
                        x.getOwner(),
                        x.getLocation(),
                        x.getName() + System.currentTimeMillis(),
                        x.getImage(),
                        x.getHardware(),
                        x.getTemplateOptions()
                );

                vm = this.get(VirtualMachine.class).create(vm);
            }
        }

        // add the instance to the vm
        Instance newInstance = new Instance(
                null,
                null,
                null,
                null,
                anyInstance.getApplicationComponent(),
                anyInstance.getApplicationInstance(),
                vm.getId()
        );

        this.get(Instance.class).create(newInstance);
    }

    @Override
    public List<ScalingAction> getScalingActions(List<Long> ids) {
        List<ScalingAction> result = new ArrayList<>();

        List<ComponentHorizontalInScalingAction> ins = this.get(ComponentHorizontalInScalingAction.class).getList();
        List<ComponentHorizontalOutScalingAction> outs = this.get(ComponentHorizontalOutScalingAction.class).getList();

        for (Long id : ids) {
            for (ComponentHorizontalInScalingAction m : ins) {
                if (m.getId().equals(id)) {
                    result.add(m);
                }
            }
            for (ComponentHorizontalOutScalingAction m : outs) {
                if (m.getId().equals(id)) {
                    result.add(m);
                }

            }
        }

        return result;
    }
}
