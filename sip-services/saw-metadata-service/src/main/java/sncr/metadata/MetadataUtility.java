package sncr.metadata;

import cmd.CommandLineHandler;
import files.HFileOperations;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.metadata.analysis.AnalysisProvHelper;
import sncr.metadata.ui_components.UIMDRequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * Created by srya0001 on 2/23/2017.
 */
public class MetadataUtility {

    protected static final Logger logger = LoggerFactory.getLogger(MetadataUtility.class.getName());

    public static void main(String[] args) throws Exception {

        cmd.CommandLineHandler cli = new cmd.CommandLineHandler();
        try{
            org.apache.commons.cli.CommandLine cl  = buildCMD(cli, args);
            String inData = HFileOperations.readFile(cl.getOptionValue('i'));
            OutputStream outStream = HFileOperations.writeToFile(cl.getOptionValue('o'));
            String mdType = cl.getOptionValue('t');

            System.out.println("Start data processing:\n input file: " + cl.getOptionValue('i') + "\nOutput path: " + cl.getOptionValue('o'));
            switch (mdType) {
                case "ui":
                    List<UIMDRequestProcessor> semaReqHandlers = UIMDRequestProcessor.getHandlerForRequest4Java(inData, false);
                    if (semaReqHandlers.isEmpty())
                    {
                        logger.error("The document is not parsable. Exit");
                        break;
                    }
                    semaReqHandlers.forEach (  h ->
                    {
                        String result = h.validateAndExecute();
                        try{
                            outStream.write(result.getBytes());
                        } catch (IOException e) {
                            logger.error("I/O exception at attempt to write data: ", e);
                        }
                    });
                    break;
                case "scan_ui":
                    String result = UIMDRequestProcessor.scanSemanticTable(true);
                    try{
                        outStream.write(result.getBytes());
                    } catch (IOException e) {
                        logger.error("I/O exception at attempt to write data: ", e);
                    }
                    break;
                case "analysis":
                    AnalysisProvHelper provision = AnalysisProvHelper.apply(inData);
                    if ( !provision.requestsParsed()){
                        logger.error("The document is not parsable. Exit");
                        break;
                    }
                    result = provision.handleRequests(true);
                    try {
                        outStream.write(result.getBytes());
                    } catch (IOException e) {
                        logger.error("I/O exception at attempt to write data: ", e);
                    }
                    break;
                default:
                    logger.error("Unsupported metadata type");
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
        cli.addOptionToHandler("metadataType", true,
               "Type of metadata object: ui, ui_scan, analysis, sncr.datalake, report",
               "md-type",
               "t",
                true);

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
