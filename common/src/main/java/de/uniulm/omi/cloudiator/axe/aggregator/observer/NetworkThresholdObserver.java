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

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 26.08.2015.
 */
public abstract class NetworkThresholdObserver extends ThresholdObserver {
    private final String servername;
    private final Integer port;

    public NetworkThresholdObserver(String externalId, double threshold, FormulaOperator operator,
        String servername, Integer port) {
        super(externalId, threshold, operator);
        this.servername = servername;
        this.port = port;
    }

    public String getServername() {
        return servername;
    }

    public Integer getPort() {
        return port;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NetworkThresholdObserver))
            return false;
        if (!super.equals(o))
            return false;

        NetworkThresholdObserver that = (NetworkThresholdObserver) o;

        if (!port.equals(that.port))
            return false;
        if (!servername.equals(that.servername))
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + servername.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }
}
