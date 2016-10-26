package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

/**
 * Created by Frank on 19.10.2016.
 */
public class EndpointParameterImpl implements EndpointParameter {
    private final String endpoint;

    public EndpointParameterImpl(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }
}
