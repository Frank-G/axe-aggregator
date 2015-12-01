package de.uniulm.omi.cloudiator.axe.aggregator.config;

/**
 * Created by Frank on 01.12.2015.
 */
public class CommandLineProperty {
    public static CommandLineProperty[] commandLineProperties = {
            new CommandLineProperty("localDomainKairosIP", "localDomainKairosIP", "TODO desc", "127.0.0.1"),
            new CommandLineProperty("localDomainKairosPort", "localDomainKairosPort", "TODO desc", "8080"),
            new CommandLineProperty("defaultKairosPort", "defaultKairosPort", "TODO desc", "8080")
    };

    private final String name;
    private final String longOpt;
    private final String desc;
    private final String defaultValue;

    private CommandLineProperty(String name, String longOpt, String desc, String defaultValue) {
        this.name = name;
        this.longOpt = longOpt;
        this.desc = desc;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public String getDesc() {
        return desc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
