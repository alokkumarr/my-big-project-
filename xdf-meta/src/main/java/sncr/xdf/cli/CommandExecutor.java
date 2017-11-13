package sncr.xdf.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.ojai.Document;
import sncr.xdf.core.file.HFileOperations;
import sncr.xdf.metastore.DSStore;

import java.io.FileNotFoundException;

/**
 * Created by srya0001 on 11/4/2017.
 */
public class CommandExecutor {

    public static void main(String args[]){
        try {
            String json = args[0];
            System.out.print("Request: " + json);
            String jStr = HFileOperations.readFile(json);
            Request r = new Request(jStr);
            r.process();
            System.out.print("Request processing completed");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
