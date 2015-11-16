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

package de.uniulm.omi.cloudiator.axe.aggregator.entities.converter;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.Monitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.*;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.ScalingAction;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Window;
import de.uniulm.omi.cloudiator.colosseum.client.entities.enums.FlowOperator;
import de.uniulm.omi.cloudiator.colosseum.client.entities.enums.FormulaOperator;

import java.util.List;

/**
 * Created by Frank on 20.08.2015.
 */
public interface Converter {
    de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor convert(ComposedMonitor cm);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor convert(RawMonitor mon);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor convert(ConstantMonitor mon);

    List<Monitor> convert(
        List<de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Monitor> obj);

    Monitor convert(de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Monitor mon);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.SensorDescription convert(
        SensorDescription obj);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.FlowOperator convert(FlowOperator fo);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator convert(FormulaOperator fo);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.Schedule convert(Schedule obj);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaQuantifier convert(
        FormulaQuantifier obj);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.Window convert(Window obj);

    de.uniulm.omi.cloudiator.axe.aggregator.entities.ScalingAction convert(ScalingAction obj);

    List<de.uniulm.omi.cloudiator.axe.aggregator.entities.ScalingAction> convertScalingAction(
            List<ScalingAction> obj);
}
