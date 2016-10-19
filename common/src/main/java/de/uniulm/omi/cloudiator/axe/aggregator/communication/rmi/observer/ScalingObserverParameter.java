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

package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.ColosseumDetails;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ExternalReferenced;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.NetworkParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ThresholdParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Created by Frank on 25.08.2015.
 */
public interface ScalingObserverParameter extends
        Remote,
        Serializable,
        ExternalReferenced,
        NetworkParameter,
        ThresholdParameter {

    ColosseumDetails getColosseumDetails();
}