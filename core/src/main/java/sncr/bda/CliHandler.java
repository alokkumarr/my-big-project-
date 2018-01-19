package sncr.bda;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import sncr.bda.exceptions.BDAException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexey Sorokin on 9/19/2016.
 * Definition of XDF components command line interface
 */
public class CliHandler {

    public static final String NONE = "none";
    private static final Logger logger = Logger.getLogger(CliHandler.class);

    private Options options = null;


    public enum OPTIONS {
        BATCH_ID("batch"),
        CONFIG("config"),
        //LOCATIONS("PhysicalLocation"),
        APP_ID("app");

        private final String text;
        OPTIONS(String s){
            text = s;
        }
    }

    public CliHandler(){
        options = new Options();

        Option opt = new Option("b", OPTIONS.BATCH_ID.name(), true, " : Batch ID");
        opt.isRequired();
        opt.setArgName("batch id");
        options.addOption(opt);

        opt = new Option("c", OPTIONS.CONFIG.name(), true, " : Full path to component configuration file");
        opt.isRequired();
        opt.setArgName("component configuration file");
        options.addOption(opt);

        opt = new Option("a", OPTIONS.APP_ID.name(), true, " : Unique application ID");
        opt.isRequired();
        opt.setArgName("application id");
        options.addOption(opt);

    }

    public Map<String, Object> parse(String[] s) throws ParseException{
        CommandLineParser parser = new BasicParser();
        CommandLine cl = parser.parse(options, s);
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < OPTIONS.values().length; i++) {
            if ( cl.hasOption(OPTIONS.values()[i].name())){
                logger.debug("Process option: "+ OPTIONS.values()[i].name() + " value: " + cl.getOptionValue(OPTIONS.values()[i].name()));
                parameters.put(OPTIONS.values()[i].name(), cl.getOptionValue(OPTIONS.values()[i].name()));
            }
            else{
                throw new BDAException(BDAException.ErrorCodes.IncorrectCall);
            }
        }
        return parameters;
    }

    public void printUsage(String name){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( name, options );
    }
}
