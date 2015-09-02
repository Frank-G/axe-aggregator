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
public class HorizontalScalingAction extends ScalingAction {
    private final Long amount;
    private final Long min;
    private final Long max;
    private final Long count;

    public HorizontalScalingAction(long id, Long amount, Long min, Long max, Long count) {
        super(id);
        this.amount = amount;
        this.min = min;
        this.max = max;
        this.count = count;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getMin() {
        return min;
    }

    public Long getMax() {
        return max;
    }

    public Long getCount() {
        return count;
    }
}
