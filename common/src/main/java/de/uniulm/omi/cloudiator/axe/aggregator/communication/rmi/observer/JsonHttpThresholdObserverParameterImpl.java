package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.EndpointParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.EndpointParameterImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ExternalReferenced;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ExternalReferencedImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.NetworkParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.NetworkParameterImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ThresholdParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ThresholdParameterImpl;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

/**
 * Created by Frank on 19.10.2016.
 */
public class JsonHttpThresholdObserverParameterImpl implements JsonHttpThresholdObserverParameter {
    private final ExternalReferenced externalReferenced;
    private final ThresholdParameter thresholdParameter;
    private final NetworkParameter networkParameter;
    private final EndpointParameter endpointParameter;

    public JsonHttpThresholdObserverParameterImpl(Double threshold, FormulaOperator operator, String servername, Integer port, String externalId, String endpoint) {
        externalReferenced = new ExternalReferencedImpl(externalId);
        thresholdParameter = new ThresholdParameterImpl(threshold, operator);
        networkParameter = new NetworkParameterImpl(servername, port);
        endpointParameter = new EndpointParameterImpl(endpoint);
    }

    @Override
    public String getExternalId() {
        return externalReferenced.getExternalId();
    }

    @Override
    public String getServername() {
        return networkParameter.getServername();
    }

    @Override
    public Integer getPort() {
        return networkParameter.getPort();
    }

    @Override
    public Double getThreshold() {
        return thresholdParameter.getThreshold();
    }

    @Override
    public FormulaOperator getOperator() {
        return thresholdParameter.getOperator();
    }

    @Override
    public String getEndpoint() {
        return endpointParameter.getEndpoint();
    }
}
