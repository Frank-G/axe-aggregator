package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 25.05.2016.
 */
public class HttpThresholdObserverParameter extends ExternalObserverParameter {
    private final String endpoint;

    public HttpThresholdObserverParameter(Double threshold, FormulaOperator operator, String servername, Integer port, String externalId, String endpoint) {
        super(threshold, operator, servername, port, externalId);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
