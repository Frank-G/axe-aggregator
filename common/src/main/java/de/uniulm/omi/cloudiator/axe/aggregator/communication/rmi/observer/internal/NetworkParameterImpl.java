package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

/**
 * Created by Frank on 19.10.2016.
 */
public class NetworkParameterImpl implements NetworkParameter{
    private final String servername;
    private final Integer port;

    public NetworkParameterImpl(String servername, Integer port) {
        this.servername = servername;
        this.port = port;
    }

    @Override
    public String getServername() {
        return servername;
    }

    @Override
    public Integer getPort() {
        return port;
    }
}
