package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer;

import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.EndpointParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ExternalReferenced;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.NetworkParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ObserverParameter;
import de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal.ThresholdParameter;

/**
 * Created by Frank on 11.01.2017.
 */
public interface ActivationObserverParameter extends
        ObserverParameter,
        ExternalReferenced,
        NetworkParameter,
        ThresholdParameter,
        EndpointParameter {
}
