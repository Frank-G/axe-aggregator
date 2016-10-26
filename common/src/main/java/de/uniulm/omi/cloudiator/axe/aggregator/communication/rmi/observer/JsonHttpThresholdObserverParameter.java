package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.EndpointParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ExternalReferenced;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.NetworkParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ObserverParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ThresholdParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.entities.FormulaOperator;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Created by Frank on 25.05.2016.
 */
public interface JsonHttpThresholdObserverParameter extends
        ObserverParameter,
        ExternalReferenced,
        NetworkParameter,
        ThresholdParameter,
        EndpointParameter{
}
