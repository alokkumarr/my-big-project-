package cmd;

/**
 * Created by srya0001 on 2/23/2017.
 */

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Created by Sergey Ryabov on 2017/02/23
 * Definition of XDF components command line interface
 */
public class CommandLineHandler {

    private org.apache.commons.cli.Options options = null;

    public CommandLineHandler(){
        options = new Options();
    }

    public void addOptionToHandler(String argName, boolean hasArg, String description, String longOpt, String shortOpt, boolean isRequired){
        options.addOption(
                OptionBuilder.hasArg(hasArg)
                .withArgName(argName)
                .withDescription(description)
                .withLongOpt(longOpt)
                .isRequired(isRequired)
                .create(shortOpt)
        );

    }

    public org.apache.commons.cli.CommandLine parse(String[] s) throws org.apache.commons.cli.ParseException{
        BasicParser parser = new BasicParser();
        return parser.parse( options, s);
    }

    public void printUsage(String name){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( name, options );
    }

}
