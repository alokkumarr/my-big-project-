package sncr.bda.admin;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.ojai.Document;
import org.ojai.store.QueryCondition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mapr.db.MapRDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.metastore.DataSetStore;
import sncr.bda.metastore.ProjectStore;
import sncr.bda.metastore.TransformationStore;
import sncr.bda.services.DLMetadata;
import java.util.Optional;

/**
 * Created by srya0001 on 12/1/2017.
 */
public class ProjectAdmin extends ProjectStore{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectAdmin.class);
    private final DLMetadata dlMd;
    public static final String PLP = "projectLevelParameters";
    public static final String TAGS = "allowableTags";

    public ProjectAdmin(String altRoot) throws Exception {
        super(altRoot);
        dlMd = new DLMetadata(altRoot);

    }

    public void createProject(String name, String src) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        JsonElement je0 = jsonParser.parse(src);
        create(name, je0);
        dlMd.createProject(name);
    }

    public void createProject(String name, String desc, Map<String, String> p) throws Exception {
        this.createProject(name, Optional.ofNullable(desc), Optional.empty(),  Optional.ofNullable(p));
    }

    public void createProject(String name, Optional<String> optDesc, Optional<String[]> optTags, Optional<Map<String, String>> optParameters) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        JsonObject jo = new JsonObject();
        if(optDesc.isPresent()){
            String desc = optDesc.get().trim();
            jo.add("description", new JsonPrimitive(desc.isEmpty()?"no description":desc));
        }
        if(optTags.isPresent()){
            String[] tags = optTags.get();
            JsonArray tagArray = new JsonArray();
            for ( String tag : tags){
                if(tag != null && !tag.trim().isEmpty()){
                    tagArray.add(new JsonPrimitive(tag.trim()));
                }
            }
            jo.add(TAGS, tagArray);
        }
        if(optParameters.isPresent()){
            Map<String, String> parameters = optParameters.get();
            JsonArray plp = new JsonArray();
            for ( String propKey: parameters.keySet()){
                JsonObject plpjo = new JsonObject();
                jo.add(propKey.trim(), new JsonPrimitive(parameters.get(propKey)));
                plp.add( plpjo );
            }
            jo.add(PLP, plp);
        }
        create(name, jo);
        dlMd.createProject(name);
    }

    public void createProject(String name, JsonElement el) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        create(name, el);
        dlMd.createProject(name);
    }

    @Override
    public JsonElement readProjectData(String name) throws Exception {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Project name cannot be null or empty");
        Document prj = table.findById(name);
        if (prj == null)
            throw new Exception("Project with name: " + name + " not found");
        return toJsonElement(prj);
    }

    public void cleanupProject(String name) throws Exception {

        Document prj = table.findById(name);
        if (prj == null)
            throw new Exception("ProjectService with name: " + name + " not found");

        QueryCondition qc = MapRDB.newCondition();
        qc.is("system.project", QueryCondition.Op.EQUAL, name).build();
        String dsTablename = getRoot() + Path.SEPARATOR + METASTORE + Path.SEPARATOR + DataSetStore.TABLE_NAME;
        DataSetStore dss = new DataSetStore(getRoot());
        List<Document> datasets = searchAsList(dsTablename, qc);
        LOGGER.debug("Found # datasets: " + datasets.size());
        datasets.forEach( d -> {
            String id = d.getIdString();
            try {
                JsonElement dset = dss.read(id);
                JsonObject jo = dset.getAsJsonObject();
                JsonPrimitive pl = jo.getAsJsonObject("system").getAsJsonPrimitive("physicalLocation");
                LOGGER.trace("Process dataset: " + id + " DS descriptor: " + jo.toString() + " physical location = " + ((pl != null)? pl.toString():"n/a"));
                if (pl != null) {
                    String normPL = pl.toString().replace("\"", "");
                    normPL = normPL.substring(0, normPL.length() - PREDEF_DATA_DIR.length());
                    HFileOperations.deleteEnt(normPL);
                }
                dss.delete(id);
            } catch (Exception e) {
                LOGGER.error("Could not remove datasets from Metadata Store: " + id, e);
            }}
        );

        qc = MapRDB.newCondition();
        qc.is("project", QueryCondition.Op.EQUAL, name).build();
        String trTablename = getRoot() + Path.SEPARATOR + METASTORE + Path.SEPARATOR + TransformationStore.TABLE_NAME;
        TransformationStore ts = new TransformationStore(getRoot());
        List<Document> transformations = searchAsList(trTablename, qc);
        transformations.forEach( d -> {
            String id = String.valueOf(d.getId());
            try {
                ts.delete(id);
            } catch (Exception e) {
                LOGGER.error("Could not remove transformation from Metadata Store: " + id, e);
            }}
        );
        prj.delete(PLP);
        table.flush();
    }

    public void updateProject(String name, JsonElement newDesc) throws Exception {
        update(name, newDesc);
    }

    public void updateProject(String name, JsonArray plpJA) throws Exception {
        HashMap<String, String> nParams = new HashMap<>();
        plpJA.forEach( el -> {
            JsonObject jo = el.getAsJsonObject();
            if (!jo.has("name") || !jo.has("value")) {
                LOGGER.error("Invalid parameter entry in parameter set. Skip it");
            }
            else{
                nParams.put(jo.get("name").getAsString(), jo.get("value").getAsString());
            }
        });
        this.updateProject(name, Optional.empty(), Optional.empty(),  Optional.ofNullable(nParams));
    }

    public void updateProject(String name, Map<String, String> newParameters) throws Exception {
        this.updateProject(name, Optional.empty(), Optional.empty(),  Optional.ofNullable(newParameters));
    }

    public void updateProject(String name, Optional<String> optNewDesc, Optional<String[]> optNewTags, Optional<Map<String, String>> optNewParameters) throws Exception {
        Document prj = table.findById(name);
        if (prj == null)
            throw new Exception("ProjectService with name: " + name + " not found");

        JsonObject fromDoc = this.toJsonElement(prj).getAsJsonObject();

        if(optNewDesc.isPresent()) {
            String desc = optNewDesc.get().trim();
            _updatePath(name, null, "description", new JsonPrimitive(desc.isEmpty()?"no description":desc));
        }

        if(optNewTags.isPresent()){
            String[] tags = optNewTags.get();
            JsonArray newTags = new JsonArray();
            for ( String tag : tags){
                if(tag != null && !tag.trim().isEmpty()){
                    newTags.add(new JsonPrimitive(tag.trim()));
                }
            }
            _updatePath(name, null, TAGS, newTags);
        }

        if(optNewParameters.isPresent()){
            JsonArray plp = fromDoc.getAsJsonArray(PLP);
            if (plp == null ) {
                JsonArray finalPlp = new JsonArray();
                optNewParameters.get().forEach((k, v) ->{
                    JsonObject o = new JsonObject();
                    o.add("name", new JsonPrimitive(k));
                    o.add("value", new JsonPrimitive(v));
                    finalPlp.add(o);
                });
                _updatePath(name, null, PLP, finalPlp);
            }
            else {
                JsonArray newPLP = new JsonArray();
                optNewParameters.get().forEach((k, v) -> {
                        JsonObject newEntry;
                        newEntry = new JsonObject();
                        newEntry.add("name", new JsonPrimitive(k));
                        newEntry.add("value", new JsonPrimitive(v));
                        newPLP.add(newEntry);
                    }
                );
                plp.forEach(plpel ->
                {
                    JsonObject plpJo = plpel.getAsJsonObject();
                    if (!plpJo.has("name")) {
                        LOGGER.error("Incorrect parameter entry, ignore it");
                    } else {
                        boolean found = false;
                        for (JsonElement el : newPLP) {
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
                _updatePath(name, null, PLP, newPLP);
            }
        }
        table.flush();
    }

    public void deleteProject(String name) throws Exception {
        cleanupProject(name);
        table.delete(name);
        table.flush();
        HFileOperations.deleteEnt(getRoot() + Path.SEPARATOR + name);
    }
    
    public Map<String, Document> search(QueryCondition qc) throws Exception {
      LOGGER.trace("Search query on search " + qc.toString());
      return searchAsMap(table, qc);
  }

    public static void main(String args[]){
        try {
            if (args.length < 3 ){
                LOGGER.error("prg <project name> <project desc> <prop.file>");
                System.exit(1);
            }
            String root = args[0];
            String prjName = args[1];
            String prjDesc = args[2];

            LOGGER.info("Create project with name: {} and description: {}", prjName, prjDesc);
            ProjectAdmin ps = new ProjectAdmin(root);

            JsonElement readDoc = ps.readProjectData(prjName);
            LOGGER.info("Converted to document: \n\n" + readDoc.toString() + "\n");

            ps.cleanupProject(prjName);

        } catch (FileNotFoundException e) {
            LOGGER.error("No file found error : {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while processing : {}", e.getMessage());
        }
    }


}

