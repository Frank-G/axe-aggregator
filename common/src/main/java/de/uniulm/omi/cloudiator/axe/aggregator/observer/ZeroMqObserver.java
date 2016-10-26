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
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * Created by Frank on 26.08.2015.
 */
public class ZeroMqObserver extends NetworkThresholdObserver {
    private Context context;
    private Socket socket;
    private int threadNum = 1;

    public ZeroMqObserver(String externalId, double threshold, FormulaOperator operator,
        String servername, Integer port) {
        super(externalId, threshold, operator, servername, port);

        LOGGER.debug("Init ZeroMQ observer.");
        initZeroMq();
    }

    private void initZeroMq() {
        context = ZMQ.context(threadNum);
        socket = context.socket(ZMQ.PUSH);
        socket.connect("tcp://" + getServername() + ":" + getPort());
    }

    @Override public synchronized void update(Measurement obj) {
        this.update(obj, true);
    }

    public void update(Measurement obj, boolean firstTry) {
        LOGGER.debug(
            "Start sending pushing following scaling event to ZeroMQ: " + this.getExternalId());

        if (firstTry) {
            try {
                sendEvent();
            } catch (Exception ex) {
                LOGGER.error("Error occurred while pushing to ZeroMQ: " + ex.getMessage());

                initZeroMq();
                this.update(obj, false);
            }
        } else {
            LOGGER.debug("Second try for ZeroMQ push.");
            sendEvent();
        }
    }

    public void sendEvent() {
        socket.sendMore(this.getExternalId());
        socket.send(new String(true + ""));
    }


    public synchronized void terminate() {
        socket.close();
        context.term();
    }
}
