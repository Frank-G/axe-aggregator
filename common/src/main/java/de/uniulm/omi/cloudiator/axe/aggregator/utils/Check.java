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

import de.uniulm.omi.cloudiator.axe.aggregator.entities.Schedule;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.TimeWindow;

/**
 * Created by Frank on 20.08.2015.
 */
public class Check {
    public static boolean idNotNull(long id) {
        return id > 0;
    }

    public static boolean isShorter(TimeWindow w1, TimeWindow w2) throws Exception {
        long value1 = Convert.timeToMilliseconds(w1.getTimeUnit(), w1.getInterval());
        long value2 = Convert.timeToMilliseconds(w2.getTimeUnit(), w2.getInterval());
        return value1 < value2;
    }

    public static boolean isShorter(Schedule s, TimeWindow w) throws Exception {
        long value1 = Convert.timeToMilliseconds(s.getTimeUnit(), s.getInterval());
        long value2 = Convert.timeToMilliseconds(w.getTimeUnit(), w.getInterval());
        return value1 < value2;
    }

    public static boolean isShorter(Schedule s1, Schedule s2) throws Exception {
        long value1 = Convert.timeToMilliseconds(s1.getTimeUnit(), s1.getInterval());
        long value2 = Convert.timeToMilliseconds(s2.getTimeUnit(), s2.getInterval());
        return value1 < value2;
    }

    public static boolean isEqual(Schedule s1, Schedule s2) throws Exception {
        long value1 = Convert.timeToMilliseconds(s1.getTimeUnit(), s1.getInterval());
        long value2 = Convert.timeToMilliseconds(s2.getTimeUnit(), s2.getInterval());
        return value1 == value2;
    }
}
