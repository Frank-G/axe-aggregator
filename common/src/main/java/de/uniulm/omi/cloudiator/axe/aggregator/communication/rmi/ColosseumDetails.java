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

package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi;

import java.io.Serializable;

/**
 * Created by Frank on 20.08.2015.
 */
public class ColosseumDetails implements Serializable {
    private final String protocol;
    private final String ip;
    private final int port;
    private final String username;
    private final String tenant;
    private final String password;

    public ColosseumDetails(String protocol, String ip, int port, String username, String tenant,
        String password) {
        this.protocol = protocol;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.tenant = tenant;
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getTenant() {
        return tenant;
    }

    public String getPassword() {
        return password;
    }
}
