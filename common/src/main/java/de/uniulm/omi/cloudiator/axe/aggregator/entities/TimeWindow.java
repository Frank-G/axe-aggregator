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

import de.uniulm.omi.cloudiator.axe.aggregator.Utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frank on 20.08.2015.
 */
public class TimeWindow extends Window {
    private final int interval;
    private final TimeUnit timeUnit;

    public TimeWindow(long id, int interval, TimeUnit timeUnit) {
        super(id);
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getInterval() {
        return interval;
    }

    @Override public long aggregationInMilliseconds(Schedule schedule, long minimumInterval) {
        return (int) (Utils.timeToMilliseconds(this.getTimeUnit(), this.getInterval())
            + minimumInterval);
    }
}
