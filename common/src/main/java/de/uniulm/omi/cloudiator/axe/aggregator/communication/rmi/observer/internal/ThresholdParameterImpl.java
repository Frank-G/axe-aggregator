package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 19.10.2016.
 */
public class ThresholdParameterImpl implements ThresholdParameter {
    private final Double threshold;
    private final FormulaOperator operator;

    public ThresholdParameterImpl(Double threshold, FormulaOperator operator) {

        this.threshold = threshold;
        this.operator = operator;
    }

    @Override
    public Double getThreshold() {
        return threshold;
    }

    @Override
    public FormulaOperator getOperator() {
        return operator;
    }
}
