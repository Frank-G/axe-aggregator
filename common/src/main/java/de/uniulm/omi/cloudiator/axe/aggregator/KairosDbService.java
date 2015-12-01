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

import de.uniulm.omi.cloudiator.axe.aggregator.utils.Address;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Frank on 27.07.2015.
 */
public class KairosDbService {
    private static /* final */ KairosDbService instance;
    private final ConcurrentMap<Address, KairosDbConnection> connections =
        new ConcurrentHashMap<Address, KairosDbConnection>();
    private final String homeDomainIp;
    private final Integer homeDomainPort;
    private final Integer defaultKairosPort;

    private KairosDbService(String homeDomainIp, Integer homeDomainPort, Integer defaultKairosPort) {
        this.homeDomainIp = homeDomainIp;
        this.homeDomainPort = homeDomainPort;
        this.defaultKairosPort = defaultKairosPort;
    }

    public synchronized static KairosDbService getInstance() {
        if(instance == null){
            //throw new RuntimeException("This is not allowed to be called before set instance!");
        }

        return instance;
    }


    public synchronized static void setInstance(String homeDomainIp, Integer homeDomainPort, Integer defaultKairosPort) {
        if(instance != null){
            //throw new RuntimeException("This is not allowed to be called a second time!");
        } else {
            instance = new KairosDbService(homeDomainIp, homeDomainPort, defaultKairosPort);
        }
    }

    public KairosDbConnection getKairos(String ip, int port) {
        return getKairos(new Address(ip, port));
    }

    public KairosDbConnection getKairos(Address address) {
        for (Map.Entry<Address, KairosDbConnection> e : connections.entrySet()) {

            if (e.getKey().equals(address)) {
                return e.getValue();
            }
        }

        KairosDbConnection conn = new KairosDbConnection(address.getIp(), address.getPort());
        connections.put(address, conn);
        return conn;
    }

    public KairosDbConnection getLocalKairos() {
        return getKairos(new Address(homeDomainIp, homeDomainPort));
    }

    public Integer getDefaultPort(){
        return defaultKairosPort;
    }
}