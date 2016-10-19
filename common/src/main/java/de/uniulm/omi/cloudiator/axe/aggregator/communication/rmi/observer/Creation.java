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


import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ObserverParameter;

import de.uniulm.omi.cloudiator.axe.aggregator.observer.JsonCsObserver;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.Observer;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.ScalingObserver;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.TelnetEventObserver;
import de.uniulm.omi.cloudiator.axe.aggregator.observer.TelnetMetricObserver;

/**
 * Created by Frank on 24.08.2015.
 */
public class Creation {
    public static Observer createObserver(ObserverParameter params) {
        if (params instanceof TelnetEventObserverParameter) {
            TelnetEventObserverParameter p = (TelnetEventObserverParameter) params;
            return new TelnetEventObserver(p.getExternalId(), p.getThreshold(), p.getOperator(),
                p.getServername(), p.getPort());
        } else if (params instanceof TelnetMetricObserverParameter) {
            TelnetMetricObserverParameter p = (TelnetMetricObserverParameter) params;
            return new TelnetMetricObserver(p.getExternalId(), p.getThreshold(), p.getOperator(),
                    p.getServername(), p.getPort());
        } else if (params instanceof JsonHttpThresholdObserverParameter) {
            JsonHttpThresholdObserverParameter p = (JsonHttpThresholdObserverParameter) params;
            return new JsonCsObserver(p.getExternalId(), p.getThreshold(), p.getOperator(),
                    p.getEndpoint());
        } else if (params instanceof ScalingObserverParameter) {
            ScalingObserverParameter p = (ScalingObserverParameter) params;
            return new ScalingObserver(p.getExternalId(), p.getThreshold(), p.getOperator(),
                    p.getColosseumDetails());
        } else {
            throw new RuntimeException("Observer type not implemented!");
        }
    }

    private Creation() {
        // no instance of this class
    }
}
