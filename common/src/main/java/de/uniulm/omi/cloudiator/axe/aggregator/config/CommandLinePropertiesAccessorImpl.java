package de.uniulm.omi.cloudiator.axe.aggregator.config;

import org.apache.commons.cli.*;

import javax.annotation.Nullable;

/**
 * Created by Frank on 01.12.2015.
 */
public class CommandLinePropertiesAccessorImpl
        implements CommandLinePropertiesAccessor {

    private final Options options;
    private CommandLine commandLine;
    private final static DefaultParser parser = new DefaultParser();
    private final static HelpFormatter helpFormatter = new HelpFormatter();

    public CommandLinePropertiesAccessorImpl(String[] args) {
        this.options = new Options();
        this.generateOptions(this.options);

        try {
            this.commandLine = this.parser.parse(options, args);
        } catch (ParseException e) {
            this.commandLine = null;
        }
    }

    private void generateOptions(Options options) {
        for(CommandLineProperty clp : CommandLineProperty.commandLineProperties){
            options.addOption(
                    Option.builder(clp.getName()).
                            longOpt(clp.getLongOpt()).
                            desc(clp.getDesc()).
                            hasArg()
                            .build());
        }
    }

    public void printHelp() {
        helpFormatter.printHelp("java -jar [args] camel-adapter.jar", options);
    }

    @Nullable
    protected String getCommandLineOption(String name) {
        if (commandLine != null && commandLine.hasOption(name)) {
            String result = commandLine.getOptionValue(name);
            if(result == null){
                return getDefaultValue(name);
            } else {
                return result;
            }
        } else {
            return getDefaultValue(name);
        }
    }

    private String getDefaultValue(String name) {
        for(CommandLineProperty clp : CommandLineProperty.commandLineProperties){
            if(clp.getName().equals(name)){
                return clp.getDefaultValue();
            }
        }

        return null;
    }

    @Override
    public String getLocalDomainKairosIP() {
        return this.getCommandLineOption("localDomainKairosIP");
    }

    @Override
    public Integer getLocalDomainKairosPort() {
        return Integer.parseInt(this.getCommandLineOption("localDomainKairosPort"));
    }

    @Override
    public Integer getDefaultKairosPort() {
        return Integer.parseInt(this.getCommandLineOption("defaultKairosPort"));
    }
}
