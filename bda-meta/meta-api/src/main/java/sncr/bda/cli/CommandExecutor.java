package sncr.bda.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.core.file.HFileOperations;

import java.io.FileNotFoundException;

/**
 * Created by srya0001 on 11/4/2017.
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    public static void main(String args[]){
        try {
            String json = args[0];
            LOGGER.info("Request: {}", json);
            String jStr = HFileOperations.readFile(json);
            Request r = new Request(jStr);
            r.process();
            LOGGER.info("Request processing completed");
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while processing file  {}", e.getMessage());
        }
    }

}
