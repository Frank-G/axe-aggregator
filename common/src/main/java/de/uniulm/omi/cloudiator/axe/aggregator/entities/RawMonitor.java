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

package de.uniulm.omi.cloudiator.axe.aggregator.entities;

/**
 * Created by Frank on 20.08.2015.
 */
public class RawMonitor extends MetricMonitor {

    /* filter */
    private final long applicationId;
    private final long componentId;
    private final long compInstanceId;
    private final long cloudId;
    private final SensorDescription sensorDescription;

    public RawMonitor(long idMonitor, Schedule schedule, long applicationId, long componentId,
        long compInstanceId, long cloudId, SensorDescription sensorDescription) {
        super(idMonitor, schedule);
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.compInstanceId = compInstanceId;
        this.cloudId = cloudId;
        this.sensorDescription = sensorDescription;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getComponentId() {
        return componentId;
    }

    public long getCompInstanceId() {
        return compInstanceId;
    }

    public long getCloudId() {
        return cloudId;
    }

    public SensorDescription getConfig() {
        return sensorDescription;
    }
}
