package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 25.05.2016.
 */
public class JsonHttpThresholdObserverParameter extends HttpThresholdObserverParameter {
    public JsonHttpThresholdObserverParameter(Double threshold, FormulaOperator operator, String servername, Integer port, String externalId, String endpoint) {
        super(threshold, operator, servername, port, externalId, endpoint);
    }
}
