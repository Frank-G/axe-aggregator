package de.uniulm.omi.cloudiator.axe.aggregator.communication.rmi.observer.internal;

/**
 * Created by Frank on 19.10.2016.
 */
public class ExternalReferencedImpl implements ExternalReferenced {

    private final String externalId;

    public ExternalReferencedImpl(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String getExternalId() {
        return externalId;
    }
}
