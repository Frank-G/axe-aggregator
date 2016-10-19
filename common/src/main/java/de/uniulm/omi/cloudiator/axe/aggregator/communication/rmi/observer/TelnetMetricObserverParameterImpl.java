package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

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
public class TelnetMetricObserverParameterImpl implements ZeroMqObserverParameter{
    private final ExternalReferenced externalReferenced;
    private final ThresholdParameter thresholdParameter;
    private final NetworkParameter networkParameter;

    public TelnetMetricObserverParameterImpl(Double threshold, FormulaOperator operator, String servername, Integer port, String externalId) {
        externalReferenced = new ExternalReferencedImpl(externalId);
        thresholdParameter = new ThresholdParameterImpl(threshold, operator);
        networkParameter = new NetworkParameterImpl(servername, port);
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
}
