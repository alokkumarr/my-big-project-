package sncr.metadata;

import cmd.CommandLineHandler;
import files.HFileOperations;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import sncr.metadata.engine.ihandlers.RequestHandler;

import java.io.OutputStream;


/**
 * Created by srya0001 on 2/23/2017.
 */
public class MetadataCLI {

    protected static final Logger logger = LoggerFactory.getLogger(MetadataCLI.class.getName());

    public static void main(String[] args) throws Exception {

        CommandLineHandler cli = new CommandLineHandler();
        try{
            CommandLine cl  = buildCMD(cli, args);
            String inData = HFileOperations.readFile(cl.getOptionValue('i'));
            OutputStream outStream = HFileOperations.writeToFile(cl.getOptionValue('o'));
            System.out.println("Start data processing:\n input file: " + cl.getOptionValue('i') + "\nOutput path: " + cl.getOptionValue('o'));
            RequestHandler requestHandler = new RequestHandler(inData, outStream);
            Tuple2<Integer, String> validation_result = requestHandler.validate();

            if (validation_result._1() != 0) {
                System.out.println("Result: " + validation_result._1() + " ==> " + validation_result._2());
                System.exit(1);
            }
            else
            {
                requestHandler.execute();
            }
        } catch(org.apache.commons.cli.ParseException e){
            System.err.println(e.getMessage());
            cli.printUsage("MetadataUtility");
        } catch(Exception e) {
            System.err.println("ERROR: Exception: " + e.getMessage());
            System.err.println("\r\nException stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static CommandLine buildCMD(CommandLineHandler cli, String args[]) throws org.apache.commons.cli.ParseException {

        cli.addOptionToHandler("inputFile", true,
                "Full path to text file in JSON format that contains all processed objects",
                "input-file",
                "i",
                true);

        cli.addOptionToHandler("outputFile", true,
                "Full path and file name for output file",
                "output-file",
                "o",
                true);

        return cli.parse(args);
    }

}
