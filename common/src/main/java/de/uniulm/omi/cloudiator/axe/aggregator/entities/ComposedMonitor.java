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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 20.08.2015.
 */
public class ComposedMonitor extends MetricMonitor {
    private final FlowOperator flowOperator;
    private final FormulaOperator function;
    private final FormulaQuantifier quantifier;
    private final Schedule schedule;
    private final Window window;
    private final List<Monitor> monitors;
    private final List<ScalingAction> scalingActions = new ArrayList<ScalingAction>();

    public ComposedMonitor(long idMonitor, FlowOperator flowOperator, Schedule schedule,
        FormulaOperator function, FormulaQuantifier quantifier, Window window,
        List<Monitor> monitors) {
        super(idMonitor, schedule);
        this.flowOperator = flowOperator;
        this.function = function;
        this.quantifier = quantifier;
        this.schedule = schedule;
        this.window = window;
        this.monitors = monitors;
    }

    public FlowOperator getFlowOperator() {
        return flowOperator;
    }

    public FormulaQuantifier getQuantifier() {
        return quantifier;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Window getWindow() {
        return window;
    }

    public List<Monitor> getMonitors() {
        return monitors;
    }

    public FormulaOperator getFunction() {
        return function;
    }

    public List<ScalingAction> getScalingActions() {
        return scalingActions;
    }

    public void addScalingAction(ScalingAction action) {
        scalingActions.add(action);
    }
}
