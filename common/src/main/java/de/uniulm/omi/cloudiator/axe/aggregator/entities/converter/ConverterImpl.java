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

import de.uniulm.omi.cloudiator.axe.aggregator.communication.frontend.FrontendCommunicator;
import de.uniulm.omi.cloudiator.colosseum.client.entities.*;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.HorizontalScalingAction;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Monitor;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.ScalingAction;
import de.uniulm.omi.cloudiator.colosseum.client.entities.abstracts.Window;
import de.uniulm.omi.cloudiator.colosseum.client.entities.enums.FlowOperator;
import de.uniulm.omi.cloudiator.colosseum.client.entities.enums.FormulaOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 20.08.2015.
 */
public class ConverterImpl implements Converter {
    private final FrontendCommunicator fc;

    public ConverterImpl(FrontendCommunicator fc) {
        this.fc = fc;
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor convert(
        ComposedMonitor cm) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.ComposedMonitor(cm.getId(),
            convert(cm.getFlowOperator()), convert(fc.getSchedule(cm.getSchedule())),
            convert(cm.getFunction()), convert(fc.getQuantifier(cm.getQuantifier())),
            convert(fc.getWindow(cm.getWindow())), convert(fc.getMonitors(cm.getMonitors())),
                convertScalingAction(fc.getScalingActions(cm.getScalingActions())));
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor convert(RawMonitor mon) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.RawMonitor(mon.getId(),
            convert(fc.getSchedule(mon.getSchedule())),
            (mon.getApplication() == null ? -1 : mon.getApplication()),
            (mon.getComponent() == null ? -1 : mon.getComponent()),
            (mon.getComponentInstance() == null ? -1 : mon.getComponentInstance()),
            (mon.getCloud() == null ? -1 : mon.getCloud()),
            convert(fc.getSensorDescription(mon.getSensorDescription())));
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor convert(
        ConstantMonitor mon) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor(mon.getId(),
            mon.getValue());
    }

    @Override public List<de.uniulm.omi.cloudiator.axe.aggregator.entities.Monitor> convert(
        List<Monitor> obj) {
        List<de.uniulm.omi.cloudiator.axe.aggregator.entities.Monitor> result = new ArrayList();

        for (Monitor mon : obj) {
            result.add(convert(mon));
        }

        return result;
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.Monitor convert(Monitor mon) {
        if (mon instanceof RawMonitor) {
            return (convert((RawMonitor) mon));
        } else if (mon instanceof ComposedMonitor) {
            return (convert((ComposedMonitor) mon));
        } else { //ConstantMonitor TODO fix this
            return (new de.uniulm.omi.cloudiator.axe.aggregator.entities.ConstantMonitor(
                mon.getId(), ((ConstantMonitor) mon).getValue()));
        }
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.SensorDescription convert(
        SensorDescription obj) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.SensorDescription(obj.getId(),
            obj.getClassName(), obj.getMetricName(), obj.getIsVmSensor());
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.FlowOperator convert(FlowOperator fo) {
        switch (fo) {
            case MAP:
                return de.uniulm.omi.cloudiator.axe.aggregator.entities.FlowOperator.MAP;
            case REDUCE:
                return de.uniulm.omi.cloudiator.axe.aggregator.entities.FlowOperator.REDUCE;
            default:
                return null; //TODO exception
        }
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator convert(
        FormulaOperator fo) {
        return de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator
            .valueOf(fo.toString());
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.Schedule convert(Schedule obj) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.Schedule(obj.getId(),
            obj.getInterval().intValue(), obj.getTimeUnit());
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaQuantifier convert(
        FormulaQuantifier obj) {
        return new de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaQuantifier(
            obj.getRelative(), obj.getValue());
    }

    @Override public de.uniulm.omi.cloudiator.axe.aggregator.entities.Window convert(Window obj) {
        if (obj instanceof TimeWindow) {
            return new de.uniulm.omi.cloudiator.axe.aggregator.entities.TimeWindow(obj.getId(),
                ((TimeWindow) obj).getInterval().intValue(), //TODO
                ((TimeWindow) obj).getTimeUnit());
        } else { //MeasurementWindow TODO fix this
            return new de.uniulm.omi.cloudiator.axe.aggregator.entities.MeasurementWindow(
                obj.getId(), ((MeasurementWindow) obj).getMeasurements().intValue() //TODO
            );
        }
    }

    @Override
    public List<de.uniulm.omi.cloudiator.axe.aggregator.entities.ScalingAction> convertScalingAction(List<ScalingAction> obj) {
        List<de.uniulm.omi.cloudiator.axe.aggregator.entities.ScalingAction> result = new ArrayList();

        for (ScalingAction sa : obj) {
            result.add(convert(sa));
        }

        return result;
    }

    @Override
    public de.uniulm.omi.cloudiator.axe.aggregator.entities.ScalingAction convert(ScalingAction obj) {
        if (obj instanceof ComponentHorizontalInScalingAction) {
            return new de.uniulm.omi.cloudiator.axe.aggregator.entities.ComponentHorizontalInScalingAction(
                    obj.getId(),
                    ((ComponentHorizontalInScalingAction) obj).getAmount(),
                    ((ComponentHorizontalInScalingAction) obj).getMin(),
                    ((ComponentHorizontalInScalingAction) obj).getMax(),
                    ((ComponentHorizontalInScalingAction) obj).getCount(),
                    ((ComponentHorizontalInScalingAction) obj).getApplicationComponent()
            );
        } else if(obj instanceof ComponentHorizontalOutScalingAction) { //MeasurementWindow TODO fix this
            return new de.uniulm.omi.cloudiator.axe.aggregator.entities.ComponentHorizontalOutScalingAction(
                    obj.getId(),
                    ((ComponentHorizontalOutScalingAction) obj).getAmount(),
                    ((ComponentHorizontalOutScalingAction) obj).getMin(),
                    ((ComponentHorizontalOutScalingAction) obj).getMax(),
                    ((ComponentHorizontalOutScalingAction) obj).getCount(),
                    ((ComponentHorizontalOutScalingAction) obj).getApplicationComponent()
            );
        } else {
            throw new RuntimeException("ScalingActionType not implemented!");
        }
    }
}
