package de.uniulm.omi.cloudiator.axe.aggregator.config;

/**
 * Created by Frank on 01.12.2015.
 */
public interface CommandLinePropertiesAccessor {
    String getLocalDomainKairosIP();
    Integer getLocalDomainKairosPort();
    Integer getDefaultKairosPort();
    Integer getRmiPort();
}
