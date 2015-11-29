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

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Iterator;
import java.util.List;

/**
 * TODO add functions for weighted values
 * <p>
 * Created by Frank on 06.08.2015.
 */
public class Calc {
    private static double ACC = 0.00001;

    private static DescriptiveStatistics transform(List<Double> values) {
        double[] arr = toArray(values);
        return new DescriptiveStatistics(arr);
    }

    private static double[] toArray(List<Double> values) {
        double[] arr = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }
        return arr;
    }

    public static Double calculate(FormulaOperator operator, List<Double> values) {

        switch (operator) {
            case AVG: {
                return Calc.AVG(values);
            }
            case SUM: {
                return Calc.SUM(values);
            }
            case MINUS: {
                return Calc.MINUS(values);
            }
            case MULTIPLY: {
                return Calc.MULTIPLY(values);
            }
            case DIV: {
                return Calc.DIV(values);
            }
            case MODULO: {
                return Calc.MODULO(values);
            }
            case AND: {
                return Calc.AND(values);
            }
            case OR: {
                return Calc.OR(values);
            }
            case XOR: {
                return Calc.XOR(values);
            }
            case GT: {
                return Calc.GT(values);
            }
            case GTE: {
                return Calc.GTE(values);
            }
            case LT: {
                return Calc.LT(values);
            }
            case LTE: {
                return Calc.LTE(values);
            }
            case EQ: {
                return Calc.EQ(values);
            }
            case NEQ: {
                return Calc.NEQ(values);
            }
            case STD: {
                return Calc.STD(values);
            }
            case COUNT: {
                return Calc.COUNT(values);
            }
            case MIN: {
                return Calc.MIN(values);
            }
            case PERCENTILE: {
                return Calc.PERCENTILE(values);
            }
            case DERIVATIVE: {
                return Calc.DERIVATIVE(values);
            }
            case MODE: {
                return Calc.MODE(values);
            }
            case MEDIAN: {
                return Calc.MEDIAN(values);
            }
            case LAST: {
                return Calc.LAST(values);
            }
            case IDENTITY:
            case FIRST: {
                return Calc.FIRST(values);
            }
            default:
                throw new RuntimeException("FormulaOperator not yet implemented!");

        }
    }

    public static Double calculate(FormulaOperator operator, List<Double> values,
        int minimumApplies, Double mappedMonitorValue) {

        switch (operator) {
            case AVG:
            case SUM:
            case MINUS:
            case MULTIPLY:
            case DIV:
            case MODULO:
            case AND:
            case OR:
            case XOR:
            case STD:
            case COUNT:
            case MIN:
            case PERCENTILE:
            case DERIVATIVE:
            case MODE:
            case MEDIAN:
            case LAST:
            case FIRST:
            case EQ: {
                return Calc.EQ(values, minimumApplies, mappedMonitorValue);
            }
            case NEQ: {
                return Calc.NEQ(values, minimumApplies, mappedMonitorValue);
            }
            case GT: {
                return Calc.GT(values, minimumApplies, mappedMonitorValue);
            }
            case GTE: {
                return Calc.GTE(values, minimumApplies, mappedMonitorValue);
            }
            case LT: {
                return Calc.LT(values, minimumApplies, mappedMonitorValue);
            }
            case LTE: {
                return Calc.LTE(values, minimumApplies, mappedMonitorValue);
            }
            default:
                throw new RuntimeException("FormulaOperator not yet implemented!");

        }
    }

    public static Double AVG(List<Double> values) {
        Double sum = 0d;

        for (Double number : values) {
            sum += number;
        }

        return sum / values.size();
    }

    public static Double SUM(List<Double> values) {
        Double sum = 0d;

        for (Double number : values) {
            sum += number;
        }

        return sum;
    }

    public static Double MINUS(List<Double> values) {
        Double minus = null;

        for (Double number : values) {
            if (minus == null) {
                minus = number;
            } else {
                minus += number;
            }
        }

        return minus;
    }

    public static Double MULTIPLY(List<Double> values) {
        Double multiply = null;

        for (Double number : values) {
            if (multiply == null) {
                multiply = number;
            } else {
                multiply *= number;
            }
        }

        return multiply;
    }

    public static Double DIV(List<Double> values) {
        Double div = null;

        for (Double number : values) {
            if (div == null) {
                div = number;
            } else {
                div /= number;
            }
        }

        return div;
    }

    public static Double MODULO(List<Double> values) {
        Double modulo = null;

        for (Double number : values) {
            if (modulo == null) {
                modulo = number;
            } else {
                modulo %= number;
            }
        }

        return modulo;
    }

    public static Double STD(List<Double> values) {
        DescriptiveStatistics ds = transform(values);

        return ds.getStandardDeviation();
    }

    public static Double COUNT(List<Double> values) {
        return (double) values.size();
    }

    public static Double MIN(List<Double> values) {
        DescriptiveStatistics ds = transform(values);

        return ds.getMin();
    }

    public static Double MAX(List<Double> values) {
        DescriptiveStatistics ds = transform(values);

        return ds.getMax();
    }

    public static Double PERCENTILE(List<Double> values) {
        //TODO this semantics has to be defined better
        Double p = values.get(0);
        values.remove(0);
        DescriptiveStatistics ds = transform(values);

        return ds.getPercentile(p);
    }

    public static Double PERCENTILE(List<Double> values, Double p) {
        DescriptiveStatistics ds = transform(values);

        return ds.getPercentile(p);
    }

    public static Double DERIVATIVE(List<Double> values) {
        //TODO not sure about the meaning / difference to STD
        return values.get(0) / values.get(values.size() - 1);
    }

    public static Double MODE(List<Double> values) {
        DescriptiveStatistics ds = transform(values);

        // TODO check if first value of mode is enough
        return Double.valueOf(StatUtils.mode(toArray(values))[0]);
    }

    public static Double MEDIAN(List<Double> values) {
        DescriptiveStatistics ds = transform(values);

        return ds.getPercentile(50.0);
    }

    public static Double GT(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            if (iterator.next() > mappedMonitorValue) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double GT(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                result &= (iterator.next() > last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double GTE(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            /* TODO equals dangerous with double arithmetic */
            if (iterator.next() >= mappedMonitorValue) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double GTE(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                result &= (iterator.next() >= last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double LT(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            if (iterator.next() < mappedMonitorValue) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double LT(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                result &= (iterator.next() < last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double LTE(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            /* TODO equals dangerous with double arithmetic */
            if (iterator.next() <= mappedMonitorValue) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double LTE(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                /* TODO equals dangerous with double arithmetic */
                result &= (iterator.next() <= last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double EQ(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            /* TODO equals dangerous with double arithmetic */
            Double next = iterator.next();
            if ((next + ACC) > mappedMonitorValue && (next - ACC) < mappedMonitorValue) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double EQ(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                double next = iterator.next();
                /* TODO equals dangerous with double arithmetic */
                result &= ((next + ACC) > last && (next - ACC) < last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double NEQ(List<Double> values, int minimumApplies, Double mappedMonitorValue) {
        int amountApply = 0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && amountApply < minimumApplies) {
            /* TODO equals dangerous with double arithmetic */
            Double next = iterator.next();
            if (!((next + ACC) > mappedMonitorValue && (next - ACC) < mappedMonitorValue)) {
                amountApply += 1;
            }
        }

        if (amountApply < minimumApplies) {
            return 0d;
        } else {
            return 1d;
        }
    }

    public static Double NEQ(List<Double> values) {
        boolean result = true;
        int amountApply = 0;
        boolean first = true;
        double last = 0.0;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (first) {
                first = false;
                last = iterator.next();
            } else {
                double next = iterator.next();
                /* TODO equals dangerous with double arithmetic */
                result &= !((next + ACC) > last && (next - ACC) < last);
            }
        }

        if (result) {
            return 0d;
        } else {
            return 1d;
        }
    }

    /*

    Semantic: if one value is true, the expression is true.

     */
    public static Double OR(List<Double> values) {
        boolean result = false;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && result == false) {
            if (iterator.next() > 0.1 /* ungenauigkeit */) {
                result = true;
            }
        }

        if (result) {
            return 1d;
        } else {
            return 0d;
        }
    }

    public static Double XOR(List<Double> values) {
        boolean result = false;
        boolean first = true;
        Iterator<Double> iterator = values.iterator();
        while (iterator.hasNext() && result == false) {
            double next = iterator.next();
            if (first) {
                if (next > 0.1 /* ungenauigkeit */) {
                    result = true;
                    first = false;
                }
            } else {
                if ((!result && next > 0.1) || (result && next < 0.99)) {
                    result = true;
                }
            }
        }

        if (result) {
            return 1d;
        } else {
            return 0d;
        }
    }

    public static Double AND(List<Double> values) {
        boolean result = true;

        if (values.size() > 0) {
            Iterator<Double> iterator = values.iterator();
            while (iterator.hasNext() && result == true) {
                if (iterator.next() < 0.99 /* ungenauigkeit */) {
                    result = false;
                }
            }
        } else {
            result = false;
        }

        if (result) {
            return 1d;
        } else {
            return 0d;
        }
    }

    public static Double FIRST(List<Double> values) {
        return values.get(0);
    }

    public static Double LAST(List<Double> values) {
        return values.get(values.size() - 1);
    }

}
