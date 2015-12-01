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
import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.RemoteFrontendCommunicator;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.AggregatorServiceAccess;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.AggregatorServiceAccessImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.Constants;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.Registrator;
import de.uniulm.omi.cloudiator.axe.aggregator.config.CommandLinePropertiesAccessor;
import de.uniulm.omi.cloudiator.axe.aggregator.config.CommandLinePropertiesAccessorImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 */
public class App {
    public static final Logger LOGGER = LogManager.getLogger(App.class);
    private final static Registrator<AggregatorServiceAccess> reg =
        Registrator.create(AggregatorServiceAccess.class);


    public static void main(String[] args) {
        try {
            CommandLinePropertiesAccessor config = new CommandLinePropertiesAccessorImpl(args);
            KairosDbService.setInstance(config.getLocalDomainKairosIP(),
                    config.getLocalDomainKairosPort(),
                    config.getDefaultKairosPort());

            FrontendCommunicator fc = new RemoteFrontendCommunicator();

            AggregatorServiceAccess asa = new AggregatorServiceAccessImpl(fc, config.getLocalDomainKairosIP());

            AggregatorServiceAccess stub = reg.export(asa, Constants.RMI_PORT);

            // TODO it might be worth exploiting ways to get rid of this
            // TODO dependency to a registry. note that there does not seem to
            // TODO be an easy way to do it (i.e. relaying on standard interfaces)
            // TODO may want to allow different Ports?
            if (stub != null && reg.addToRegistry(stub, Constants.REGISTRY_KEY)) {
                // from here on RMI takes over //
                LOGGER.info("AggregatorServiceAccess: exported. waiting for requests.");
            } else {
                LOGGER.error("Cannot start AggregatorServiceAccess; exiting.");
                Runtime.getRuntime().exit(-541);
            }


            // TODO check this. Sometimes threads are killed.
            while (true) {
                Thread.sleep(1000000);
            }
        } catch (Exception ex) {
            LOGGER.error("Error while providing AggregatorService via RMI.", ex);
        }
    }
}
