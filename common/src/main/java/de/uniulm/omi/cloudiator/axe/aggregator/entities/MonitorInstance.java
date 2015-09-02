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
public class MonitorInstance extends External {
    private final long idMonitor;
    private final String apiEndpoint;
    private final long idIpAddress;
    private final long vmId;
    private final long componentId;

    public MonitorInstance(long id, long idMonitor, String apiEndpoint, long idIpAddress, long vmId,
        long componentId) {
        super(id);
        this.idMonitor = idMonitor;
        this.apiEndpoint = apiEndpoint;
        this.idIpAddress = idIpAddress;
        this.vmId = vmId;
        this.componentId = componentId;
    }

    public long getIdMonitor() {
        return idMonitor;
    }

    public long getIdIpAddress() {
        return idIpAddress;
    }

    public long getVmId() {
        return vmId;
    }

    public long getComponentId() {
        return componentId;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }
}
