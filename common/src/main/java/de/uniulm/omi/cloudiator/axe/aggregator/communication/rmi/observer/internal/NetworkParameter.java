package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

/**
 * Created by Frank on 19.10.2016.
 */
public interface NetworkParameter extends ObserverParameter {
    String getServername();
    Integer getPort();
}
