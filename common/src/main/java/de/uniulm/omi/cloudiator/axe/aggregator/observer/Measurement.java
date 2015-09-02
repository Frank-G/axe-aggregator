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

/**
 * Created by Frank on 03.08.2015.
 */
public class Measurement {
    final private Long idMonitor;
    final private Long idMonitorInstance;
    final private Double measurement;
    final private long timeStamp;

    public Measurement(Long idMonitor, Long idMonitorInstance, Double measurement, long timeStamp) {
        this.idMonitor = idMonitor;
        this.idMonitorInstance = idMonitorInstance;
        this.measurement = measurement;
        this.timeStamp = timeStamp;
    }

    public Long getIdMonitor() {
        return idMonitor;
    }

    public Double getMeasurement() {
        return measurement;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Long getIdMonitorInstance() {
        return idMonitorInstance;
    }
}
