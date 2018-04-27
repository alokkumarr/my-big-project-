package sncr.bda.metastore;

import com.google.gson.*;
import com.mapr.db.MapRDB;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import sncr.bda.base.MetadataStore;
import sncr.bda.base.WithSearchInMetastore;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.services.DLMetadata;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srya0001 on 12/1/2017.
 */
public class ProjectStore extends MetadataStore implements WithSearchInMetastore{

    private static final Logger logger = Logger.getLogger(ProjectStore.class);
    private static String TABLE_NAME = "projects";
    public static final String PLP = "projectLevelParameters";
    protected JsonParser jsonParser;

    public ProjectStore(String altRoot) throws Exception {
        super(TABLE_NAME, altRoot);
        jsonParser = new JsonParser();
    }

    public void createProjectRecord(String name, String src) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
         JsonElement je0 = jsonParser.parse(src);
         create(name, je0);
    }


    public void createProjectRecord(String name, String desc, Map<String, String> p) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        JsonObject jo = new JsonObject();
        jo.add("description", new JsonPrimitive((desc == null || desc.isEmpty())?"no description":desc));
        JsonArray plp = new JsonArray();
        for ( String propKey: p.keySet()){
            JsonObject plpjo = new JsonObject();
            jo.add(propKey, new JsonPrimitive(p.get(propKey)));
            plp.add( plpjo );
        }
        jo.add(PLP, plp);
        create(name, jo);
    }

    public void createProjectRecord(String name, String desc, JsonArray p) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        JsonObject jo = new JsonObject();
        jo.add("description", new JsonPrimitive((desc == null || desc.isEmpty())?"no description":desc));
        jo.add(PLP, p);
        create(name, jo);
    }

    public JsonElement readProjectData(String name) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        Document prj = table.findById(name);
        if (prj == null)
            throw new Exception("Project with name: " + name + " not found");
        return toJsonElement(prj);
    }


    public void updateProjectRecord(String name, String src) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        Document prj = table.findById(name);
        if (prj == null)
            throw new Exception("Project with name: " + name + " not found");
        JsonElement je = jsonParser.parse(src);
        update(name, je);
    }

    /*
    public void updateProject(String name, JsonArray plpJA) throws Exception {
        HashMap<String, String> nParams = new HashMap<>();
        plpJA.forEach( el -> {
            JsonObject jo = el.getAsJsonObject();
            if (!jo.has("name") || !jo.has("value")) {
                logger.error("Invalid parameter entry in parameter set. Skip it");
            }
            else{
                nParams.put(jo.get("name").getAsString(), jo.get("value").getAsString());
            }
        });
        updateProject(name, nParams);
    }
*/

    public void updateProjectRecord(String name, Map<String, String> newParameters) throws Exception {
        Document prj = table.findById(name);
        if (prj == null)
             throw new Exception("ProjectService with name: " + name + " not found");

        JsonArray je = new JsonArray();
        JsonObject fromDoc = this.toJsonElement(prj).getAsJsonObject();
        JsonArray plp = fromDoc.getAsJsonArray(PLP);
        if (plp == null ) {
            JsonArray finalPlp = new JsonArray();
            newParameters.forEach((k, v) ->{
                JsonObject o = new JsonObject();
                o.add("name", new JsonPrimitive(k));
                o.add("value", new JsonPrimitive(v));
                finalPlp.add(o);
            });
            _updatePath(name, null, PLP, finalPlp);
        }
        else {
            JsonArray newPLP = new JsonArray();
            newParameters.forEach((k, v) -> {
                        JsonObject newEntry;
                        newEntry = new JsonObject();
                        newEntry.add("name", new JsonPrimitive(k));
                        newEntry.add("value", new JsonPrimitive(v));
                        newPLP.add(newEntry);
                    }
            );
            plp.forEach( plpel ->
            {
                JsonObject plpJo = plpel.getAsJsonObject();
                if (!plpJo.has("name")){
                  logger.error("Incorrect parameter entry, ignore it");
                }
                else{
                    boolean found = false;
                    for (JsonElement el: newPLP) {
                        if (el.getAsJsonObject()
                            .get("name")
                            .getAsString()
                            .equalsIgnoreCase(plpJo.get("name").getAsString())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) newPLP.add(plpJo);
                }
            });
            _updatePath(name, null, PLP, plp);
        }
        table.flush();
    }


    public static void main(String args[]){
        try {
            if (args.length < 3 ){
                System.err.println("prg <project name> <project desc> <prop.file>");
                System.exit(1);
            }
            String root = args[0];
            String prjName = args[1];
            String prjDesc = args[2];

            String propFile = null;
            if (args.length == 4) propFile = args[3];

            System.out.println(String.format("Create project with name: %s and description: %s", prjName, prjDesc));
            ProjectStore ps = new ProjectStore(root);
/*
            if (propFile == null)
                //ps.createProject(prjName, prjDesc);
            else{
                String jStr = HFileOperations.readFile(propFile);
                JsonParser jsonParser = new JsonParser();
                JsonElement je = jsonParser.parse(jStr);
                System.out.print("Parsed parameter file: \n\n" + je.toString() + "\n");
                JsonObject jo = je.getAsJsonObject();
                HashMap<String, String> hm = new HashMap<String, String>();
                jo.entrySet().forEach( e -> hm.put(e.getKey(), e.getValue().getAsString()) );
                ps.createProject(prjName, prjDesc, hm);
            }
*/
            JsonElement readDoc = ps.readProjectData(prjName);
            System.out.println("Converted to document: \n\n" + readDoc.toString() + "\n");



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

