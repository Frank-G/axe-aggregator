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

import de.uniulm.omi.cloudiator.axe.aggregator.AggregatorService;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.Constants;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Frank on 26.08.2015.
 */
public abstract class TelnetObserver extends NetworkThresholdObserver {

    private Socket pingSocket;
    private PrintWriter out;
    private final String typePrefix;

    public TelnetObserver(String externalId, double threshold, FormulaOperator operator,
        String servername, Integer port) {
        super(externalId, threshold, operator, servername, port);

        this.typePrefix = this.getTypePrefix();

        LOGGER.debug("Init telnet metric observer.");

        try {
            pingSocket = new Socket(this.getServername(), this.getPort());
            out = new PrintWriter(pingSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.debug("Could not connect to telnet server.");
        }
    }

    protected abstract String getTypePrefix();

    @Override public void update(Measurement obj) {
        this.update(obj, true);
    }

    public void update(Measurement obj, boolean firstTry) {
        LOGGER.debug("Start sending telnet message.");

        String metricTelnet = "";
        String split = " ";

        /*TODO not very clean */
        /* TODO better solution to get the CDO id? */
        String externalRef = AggregatorService.getService(null, Constants.LOCALHOST_IP).getFc()
            .getMonitorInstance(obj.getIdMonitorInstance()).getExternalReferences().get(0);

        metricTelnet += "n/a" + split;
        metricTelnet += typePrefix + "#" + externalRef + split;
        metricTelnet += obj.getMeasurement() + split;
        metricTelnet += obj.getTimeStamp();

        LOGGER.error("Send this: " + metricTelnet);

        if (firstTry) {
            Socket exSocket = pingSocket;
            PrintWriter exPrintWriter = out;
            try {
                out.println(metricTelnet);
                if (out.checkError())
                    throw new IOException();
            } catch (Exception e) {
                this.update(obj, false);
                LOGGER.error("second try.");
                exPrintWriter.close();
                try {
                    exSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LOGGER.error("Could not close socket server.");
                }
            } finally {

            }
        } else { // try again once:
            try {
                pingSocket = new Socket(this.getServername(), this.getPort());
                out = new PrintWriter(pingSocket.getOutputStream(), true);
                out.println(metricTelnet);
                //pingSocket.close();
            } catch (IOException /**/ e) {
                e.printStackTrace();
                LOGGER.error("Could not send to telnet server (2).");
            }
        }
    }
}
