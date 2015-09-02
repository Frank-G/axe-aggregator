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

/**
 * Created by Frank on 22.05.2015.
 */
public abstract class ThresholdObserver extends MetricObserver {
    private final double threshold;
    private final FormulaOperator operator; /*TODO check for correct use*/
    private final double accuracy = 0.00001;

    public ThresholdObserver(String externalId, double threshold, FormulaOperator operator) {
        super(externalId);
        this.threshold = threshold;
        this.operator = operator;
    }

    public double getThreshold() {
        return threshold;
    }

    @Override public boolean isViolated(double latestValue) {
        switch (operator) {
            case LT:
                return latestValue < threshold;
            case LTE:
                return latestValue <= threshold;
            case GT:
                return latestValue > threshold;
            case GTE:
                return latestValue >= threshold;
            case EQ:
                return /* latestValue == threshold */ (latestValue + accuracy < threshold
                    && latestValue - accuracy > threshold);
            default:
                return true; /*TODO actually exception */
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ThresholdObserver))
            return false;

        ThresholdObserver that = (ThresholdObserver) o;

        if (Double.compare(that.accuracy, accuracy) != 0)
            return false;
        if (Double.compare(that.threshold, threshold) != 0)
            return false;
        if (operator != that.operator)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(threshold);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        temp = Double.doubleToLongBits(accuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
