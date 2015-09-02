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

import de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.MonitorInstance;

import java.util.List;
import java.util.Map;

/**
 * Created by Frank on 06.08.2015.
 */
public abstract class ComposedKairosAggregator extends KairosAggregator {

    private final ComposedMonitor monitor;

    public ComposedKairosAggregator(KairosDbService kairos,
        Map<String, List<MonitorInstance>> monitorListMap, ComposedMonitor monitor) {
        super(kairos, monitorListMap);
        this.monitor = monitor;
    }

    @Override public ComposedMonitor getComposedMonitor() {
        return monitor;
    }

    @Override public long getMonitorId() {
        return getComposedMonitor().getId();
    }
}
