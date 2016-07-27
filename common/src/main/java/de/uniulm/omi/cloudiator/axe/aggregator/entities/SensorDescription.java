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

import de.uniulm.omi.cloudiator.axe.aggregator.entities.placeholder.Id;

/**
 * Created by Frank on 20.08.2015.
 */
public final class SensorDescription extends Id {

    public static final SensorDescription VM_CPU_SENSOR =
        new SensorDescription(0, "eu.paasage.sensors.cpu.VmCpuSensor", "VM_CPU", true, false);
    public static final SensorDescription COMP_CPU_SENSOR =
        new SensorDescription(0, "eu.paasage.sensors.cpu.CompCpuSensor", "Comp_CPU", false, false);

    private final String className;
    private final String metricName;
    private final boolean isVmSensor;
    private final boolean isPush;


    public boolean isVmSensor() {
        return isVmSensor;
    }

    public boolean isPush() {
        return isPush;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getClassName() {
        return className;
    }

    public SensorDescription(long id, String _className, String _metricName, boolean _isVmSensor, boolean _isPush) {
        super(id);
        this.className = _className;
        this.metricName = _metricName;
        this.isVmSensor = _isVmSensor;
        this.isPush = _isPush;
    }

    public String getSensorMetricName() {
        return metricName;
    }
}
