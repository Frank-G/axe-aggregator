package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 19.10.2016.
 */
public interface ThresholdParameter {
    Double getThreshold();
    FormulaOperator getOperator();
}
