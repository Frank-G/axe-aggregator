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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Frank on 20.08.2015.
 */
public abstract class External extends Id {
    private final List<String> externalIds = new ArrayList<>();

    public External(long id) {
        super(id);
    }

    public List<String> getExternalIds() {
        return externalIds;
    }

    public void addExternalId(String externalId) {
        this.externalIds.add(externalId);
    }

    public void removeExternalId(String externalId) {
        Predicate<String> filter = Predicate.isEqual(externalId);
        this.externalIds.removeIf(filter);
    }
}
