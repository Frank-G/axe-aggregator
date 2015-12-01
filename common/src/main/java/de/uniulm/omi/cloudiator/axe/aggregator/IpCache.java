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

import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.FrontendCommunicator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Frank on 27.07.2015.
 */
public class IpCache {
    private static IpCache singleton;

    private Map<Long, String> IPs = new ConcurrentHashMap<Long, String>(); // <id_ip, value_ip>
    private FrontendCommunicator fc;
    private String homeDomainIp;

    private IpCache(FrontendCommunicator fc, String homeDomainIp) {
        this.fc = fc;
        this.homeDomainIp = homeDomainIp;
    }

    synchronized public static IpCache create(FrontendCommunicator fc, String homeDomainIp) {
        if (singleton == null) {
            singleton = new IpCache(fc, homeDomainIp);
        } else {
            if (!singleton.checkFrontendCommunicator(fc)) {
                singleton.setFrontendCommunicator(fc);
            }
        }
        return singleton;
    }

    public boolean checkFrontendCommunicator(FrontendCommunicator fc) {
        return this.fc.equals(fc);
    }

    public void setFrontendCommunicator(FrontendCommunicator fc) {
        this.fc = fc;
    }

    public String getIp(Long ip) {//TODO better add ip address per mi
        if (ip == null) {
            return homeDomainIp; /* TODO only for the time being, that we have aggr. only in home domain */
        }

        for (Map.Entry<Long, String> s : IPs.entrySet()) {
            if (s.getKey().equals(ip)) {
                return s.getValue();
            }
        }

        // if not returned, ip not found in set
        String address = fc.getIpAddress(ip);
        IPs.put(ip, address);
        return address;
    }

    public void refresh() {
        for (Map.Entry<Long, String> s : IPs.entrySet()) {
            String address = fc.getIpAddress(s.getKey());

            s.setValue(address);
        }
    }
}
