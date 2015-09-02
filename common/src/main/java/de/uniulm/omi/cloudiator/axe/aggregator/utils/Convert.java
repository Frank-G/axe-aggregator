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

package de.uniulm.omi.cloudiator.axe.aggregator.utils;

import java.util.concurrent.TimeUnit;

// CAMEL-specific:
//import eu.paasage.camel.unit.TimeIntervalUnit;
//import eu.paasage.camel.unit.Unit;
//import eu.paasage.camel.unit.UnitType;


/**
 * Created by Frank on 20.08.2015.
 */
public class Convert {
    public static double hertz(int interval, TimeUnit unit) {
        /*TODO*/
        return 0.0d;
    }

    public static long timeToMilliseconds(Object unit, long interval) throws Exception {
        if (unit instanceof TimeUnit) {
            switch ((TimeUnit) unit) {
                case HOURS:
                    return interval * 1000 * 60 * 60;
                case MINUTES:
                    return interval * 1000 * 60;
                case SECONDS:
                    return interval * 1000;
                case MILLISECONDS:
                    return interval;
                default:
                    throw new Exception("TimeUnit for Schedule not implemented!");
            }
            // CAMEL-specific:
            //        } else if(unit instanceof UnitType){
            //            switch ((UnitType)unit){
            //                case HOURS:
            //                    return interval * 1000 * 60 * 60;
            //                case MINUTES:
            //                    return interval * 1000 * 60;
            //                case SECONDS:
            //                    return interval * 1000;
            //                case MILLISECONDS:
            //                    return interval;
            //                default:
            //                    throw new Exception("TimeUnit for Schedule not implemented!");
            //            }
        } else {
            switch (unit.toString()) {
                case "HOURS":
                    return interval * 1000 * 60 * 60;
                case "MINUTES":
                    return interval * 1000 * 60;
                case "SECONDS":
                    return interval * 1000;
                default:
                    throw new Exception("TimeUnit for Schedule not implemented!");
            }
        }
    }

    public static long timeToSeconds(Object unit, long interval) throws Exception {
        return (long) (timeToMilliseconds(unit, interval) / 1000);
    }

    // CAMEL-specific:
    //    public static TimeUnit toJavaTimeUnit(TimeIntervalUnit unit) throws Exception {
    //        switch(unit.getUnit()){
    //            case MILLISECONDS: return TimeUnit.MILLISECONDS;
    //            case SECONDS: return TimeUnit.SECONDS;
    //            case MINUTES: return TimeUnit.MINUTES;
    //            case HOURS: return TimeUnit.HOURS;
    //            default:
    //                throw new Exception("TimeIntervalUnit not yet implemented!");
    //        }
    //    }
}
