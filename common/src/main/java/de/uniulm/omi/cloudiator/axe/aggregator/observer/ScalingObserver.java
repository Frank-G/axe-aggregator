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

package de.uniulm.omi.cloudiator.axe.aggregator.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.FrontendCommunicator;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.RemoteFrontendCommunicator;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.ColosseumDetails;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.*;

/**
 * Created by Frank on 25.08.2015.
 */
public class ScalingObserver extends ThresholdObserver {
    private final ColosseumDetails colosseumDetails;
    private final FrontendCommunicator fc;

    private Long lastCreatedInstance = null;

    public ScalingObserver(String externalId, double threshold, FormulaOperator operator,
        ColosseumDetails colosseumDetails) {
        super(externalId, threshold, operator);

        this.colosseumDetails = colosseumDetails;
        this.fc = new RemoteFrontendCommunicator();

        this.fc.updateCredentials(colosseumDetails);
    }

    public ColosseumDetails getColosseumDetails() {
        return colosseumDetails;
    }

    @Override public void update(Measurement obj) {
        // get the scaling action(s) via the monitor: obj.getIdMonitor();
        LOGGER.info(
            "Now this should actually engage a scaling action via the FrontendCommunicator!" +
                    "But its not implemented yet. So we start it here");


        // Get scaling actions that are referenced to this
        ComposedMonitor cm = fc.getComposedMonitor(fc.getMonitorInstance(obj.getIdMonitor()).getMonitor());
        for(ScalingAction sa : cm.getScalingActions()){
            if (sa instanceof ComponentHorizontalInScalingAction) {
                LOGGER.info("Initiate In-Scaling.");
                ComponentHorizontalInScalingAction in = (ComponentHorizontalInScalingAction)sa;

                // DELETE component
                int amountOfInstances = fc.getAmountOfComponentInstances(in.getComponent());
                if(amountOfInstances > in.getMin()){
                    fc.removeLatestComponentInstance(in.getComponent());
                } else {
                    LOGGER.info("Already MIN reached.");
                }
            } else if(sa instanceof ComponentHorizontalOutScalingAction) {
                // Currently only cooldown for scale out, since you
                // can not check state of deleted instances.
                if(lastCreatedInstance != null && !fc.isInstanceOk(lastCreatedInstance)){
                    LOGGER.info("Could not trigger new scaling action, since the last one is not yet finished.");
                    return;
                }

                LOGGER.info("Initiate Out-Scaling.");
                ComponentHorizontalOutScalingAction out = (ComponentHorizontalOutScalingAction)sa;

                // ADD component
                int amountOfInstances = fc.getAmountOfComponentInstances(out.getComponent());
                if (amountOfInstances < out.getMax()) {
                    lastCreatedInstance = fc.addAnotherComponentInstance(out.getComponent());
                } else {
                    LOGGER.info("Already MAX reached.");
                }
            } else {
                throw new RuntimeException("ScalingType was not implemented: " + sa.getClass().toString());
            }
        }
    }
}
