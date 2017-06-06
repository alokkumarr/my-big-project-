package sncr.analysis.execution;

import cmd.CommandLineHandler;
import files.HFileOperations;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.metadata.analysis.AnalysisExecutionHandler;

import java.io.OutputStream;


/**
 * Created by srya0001 on 2/23/2017.
 */
public class AnalysisExecutionRunner {

    protected static final Logger logger = LoggerFactory.getLogger(AnalysisExecutionRunner.class.getName());

    public static void main(String[] args) throws Exception {

        CommandLineHandler cli = new CommandLineHandler();
        try{
            CommandLine cl  = buildCMD(cli, args);
            String id = cl.getOptionValue('i');
            OutputStream outStream = HFileOperations.writeToFile(cl.getOptionValue('o'));
            System.out.println("Start data processing:\n input analysis ID: " + cl.getOptionValue('i') + "\nOutput path: " + cl.getOptionValue('o'));
            ExecutionTaskHandler er = new ExecutionTaskHandler(1);
            AnalysisExecutionHandler aeh = new AnalysisExecutionHandler(id);
            try {
                er.startSQLExecutor(aeh);
                String analysisResultId = er.getPredefResultRowID(id);
                er.waitForCompletion(id, aeh.getWaitTime());
                aeh.handleResult(outStream);
                logger.debug("Execution: Analysis ID = " + id + ", Result Row ID: " + analysisResultId );
            } catch (Exception e) {
                logger.error("Executing exception: ", e);
            }
            if (outStream != null ) {
                outStream.flush();
                outStream.close();
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
        cli.addOptionToHandler("analysis", true,
               "Analysis ID",
               "analysis",
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
