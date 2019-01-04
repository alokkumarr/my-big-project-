package sncr.xdf.esloader.esloadercommon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by skbm0001 on 30/1/2018.
 */
public class ESHttpClient {

    private static final Logger logger = Logger.getLogger(ESHttpClient.class);
    private String host;
    private ChallengeResponse authentication = null;

    public ESHttpClient(String host, String user, String password){

        this.host = host;
        if(!host.toLowerCase().startsWith("http://") && !host.toLowerCase().startsWith("https://")){
            this.host = "http://" + host;
        }

        ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        if(user != null && password != null)
            authentication = new ChallengeResponse(scheme, user, password);
    }

    public ESHttpClient(ESConfig config) throws Exception {
        //TODO: Should be fixed as part of high-availability story
        List<String> esNodes = config.getEsHosts();
        String esHost = esNodes.get(0);

        this.host = esHost;

        String user = config.getEsUser();
        String pwd = config.getEsPassword();

        int port = config.getEsPort() == 0 ? 9200 : config.getEsPort();

        this.host = this.host + ":" + port;

        if(!host.toLowerCase().startsWith("http://") && !host.toLowerCase().startsWith("https://")){
            this.host = "http://" + host;
        }

        ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        if(user != null && pwd != null)
            authentication = new ChallengeResponse(scheme, user, pwd);

    }

    private String get(String url) {
        String retval = null;
        String fullUrl = host + url;
        try {
            ClientResource cr = new ClientResource(fullUrl);
            if(authentication != null) cr.setChallengeResponse(authentication);
            cr.get();

            if(cr.getStatus().isSuccess()){
                retval = cr.getResponseEntity().getText();
            } else {
                logger.error(cr.getStatus());
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return retval;
    }

    private boolean head(String url) {
        String fullUrl = host + url;
        try {
            ClientResource cr = new ClientResource(fullUrl);
            if(authentication != null) cr.setChallengeResponse(authentication);
            cr.head();
            return cr.getStatus().isSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    private boolean delete(String url) {
        String fullUrl = host + url;
        try {
            ClientResource cr = new ClientResource(fullUrl);
            if(authentication != null) cr.setChallengeResponse(authentication);
            cr.delete();
            return cr.getStatus().isSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    private boolean put(String url, String body) {
        String retval = null;
        String fullUrl = host + url;
        try {
            ClientResource cr = new ClientResource(fullUrl);

            if(authentication != null) cr.setChallengeResponse(authentication);

            // Specify Content-Type is application/json
            StringRepresentation jsonData = new StringRepresentation(body);
            jsonData.setMediaType(MediaType.APPLICATION_JSON);

            cr.put(jsonData);
            if(!cr.getStatus().isSuccess()){
                logger.error(cr.getStatus().getDescription());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    private boolean post(String url, String body) {
        String retval = null;
        String fullUrl = host + url;
        try {
            ClientResource cr = new ClientResource(fullUrl);
            logger.debug("Full URL = " + fullUrl + ". Data = " + body);

            if(authentication != null) cr.setChallengeResponse(authentication);

            // Specify Content-Type is application/json
            StringRepresentation jsonData = new StringRepresentation(body);
            jsonData.setMediaType(MediaType.APPLICATION_JSON);
            cr.post(jsonData);
            return cr.getStatus().isSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public String esClusterVersion() throws Exception {
        String response = get("");
        if(response == null){
            throw new Exception("Cant obtain version.");
        } else {
            String clusterVersion;
            try {
                clusterVersion = new JsonParser().parse(response)
                        .getAsJsonObject()
                        .getAsJsonObject("version")
                        .getAsJsonPrimitive("number")
                        .getAsString();
            }  catch(NullPointerException e){
                clusterVersion = null;
            }
            return clusterVersion;
        }
    }

    public int esIndexStructure (String idx, String type, Map<String, String> mapping) throws Exception {
        String mappingString = get("/" + idx + "/_mapping/" + type);
        JsonObject mappingJson;
        try {
            // Try to parse and access mapping section of ES JSON
            mappingJson = new JsonParser().parse(mappingString)
                    .getAsJsonObject()
                    .getAsJsonObject(idx)
                    .getAsJsonObject("mappings")
                    .getAsJsonObject(type)
                    .getAsJsonObject("properties");
        } catch(NullPointerException e){
            // We can't parse json - on of the elements is missing
            mappingJson = null;
        }
        if(mappingJson != null) {
            for(Map.Entry<String, JsonElement> e : mappingJson.entrySet()) {
                String fieldName = e.getKey();
                JsonElement descr = e.getValue();
                String fieldType = descr.getAsJsonObject().get("type").getAsString();
                if(fieldType.equals("date")) {
                    if (descr.getAsJsonObject().has("format")) {
                        String fieldFmt = descr.getAsJsonObject().get("format").getAsString();
                        // It is possible to specify multiple formats for ES DATE/TIMESTAMP
                        // It is not recommended for XDF
                        // For loading data we will use the first one - this may lead to potential issues
                        // if data is stored with multiple formats in source data set
                        fieldFmt = fieldFmt.split("\\|\\|")[0];
                        fieldType += "^" + fieldFmt;
                    }
                }
                mapping.put(fieldName, fieldType);
            }
            return mapping.size();
        } else {
            return -1;
        }
    }

    /**
     * Returns the total number of records for a given index/alias
     *
     * @param index Name of the index or alias
     *
     * @return Total number of records in the index/alias
     */
    public long getRecordCount (String index) {
        long count = 0;

        String countURL = "/" + index + "/_count";

        String response = get(countURL);

        logger.debug("Response = " + response);

        if (response != null && response.length() != 0) {
            // Extract count
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();

            count = responseObject.get("count").getAsLong();
        }

        return count;
    }

    public boolean esIndexExists(String idx) throws Exception  {
        return head("/" + idx);
    }

    // Check if index type exists
    // Only supported in ES 6.x
    public  boolean esTypeExists(String idx, String type) throws Exception  {
        String clusterVersion = esClusterVersion();
        if(clusterVersion.startsWith("6.")) {
            return head("/" + idx + "/_mapping/" + type);
        } else {
            throw new Exception("TypeExists operation is not supported for Elastic Search cluster version " + clusterVersion);
        }
    }

    // Create Index
    public  boolean esIndexCreate(String idx, String mapping) throws Exception {
        return put( "/" + idx, mapping);
    }

    public  boolean esMappingCreate(String idx, String mappingName, String mapping) throws Exception {
        /*
            mapping must contain only mapping properties,
            not whole index definition
            PUT twitter/_mapping/user
            {
                "properties": {
                    "name": { "type": "text" }
                }
             }
        */

        return put( "/" + idx + "/_mapping/" + mappingName, mapping);
    }

    // Delete index
    public  boolean esIndexDelete(String idx) throws Exception {
        return delete("/" + idx);
    }

    // Safely delete indices
    public void  esIndexSafeDelete(String ... idx) throws Exception{
        for(String s : idx){
            // Check if index participates in any alias
            int aliasParticipation = esIndexAliasParticipation(s);
            logger.debug("Alias Participation = " + aliasParticipation);
            if(aliasParticipation == 0){
                // This index is not attached to any alias - delete it
                logger.debug("Safe deleting index " + s);
                esIndexDelete(s);
            }
        }
    }

    // Returns number of aliases for given index
    public  int esIndexAliasParticipation(String idx) throws Exception{
        String aliases = get("/" + idx + "/_alias");
        if(aliases == null) {
            return -1;
        }

        JsonObject aliasesJson;
        try {
            // Try to parse and access mapping section of ES JSON
            aliasesJson = new JsonParser().parse(aliases)
                    .getAsJsonObject()
                    .getAsJsonObject(idx)
                    .getAsJsonObject("aliases");

        }  catch(NullPointerException e){
            // We can't parse json - on of the elements is missing
            aliasesJson = null;
        }

        List<String> aliasList = new ArrayList<>();
        if(aliasesJson != null){
            for(Map.Entry<String, JsonElement> e : aliasesJson.entrySet()) {
                String aliasName = e.getKey();

                aliasList.add(aliasName);
            }
            return aliasList.size();
        }
        return 0;
    }

    // Add index to alias
    public boolean esIndexAddAlias(String alias, String ... idx) throws Exception {
        String addAlias = "{ \"actions\" : [";
        String sequence = null;
        for(String s : idx) {
            if(sequence != null)
                sequence  += ", {\"add\" : {\"index\" : \"" + s + "\" , \"alias\" : \"" + alias + "\"}}";
            else
                sequence  = "{\"add\" : {\"index\" : \"" + s + "\" , \"alias\" : \"" + alias + "\"}}";
        }
        addAlias = addAlias + sequence + "]}";
        return post("/_aliases" , addAlias);
    }

    public  boolean esAliasExists(String alias) throws Exception {
        return head("/_alias/" + alias);
    }

    // Return list of indexes with the given alias
    public List<String> esAliasListIndices(String alias) throws Exception {
        List<String> retval = new ArrayList<>();
        String aliasStr = get("/_alias/" + alias);
        if(aliasStr == null)  return null;
        JsonObject inndexJson;
        try {
            // Try to parse and access mapping section of ES JSON
            inndexJson = new JsonParser().parse(aliasStr).getAsJsonObject();
            for(Map.Entry<String, JsonElement> e : inndexJson.entrySet()) {
                String indexName = e.getKey();
                retval.add(indexName);
            }
        }  catch(NullPointerException e){
            // We can't parse json - on of the elements is missing
            retval = null;
        }
        return retval;
    }

    public  boolean esIndexRemoveAlias(String alias, String ... indices)throws Exception {
        String actions = generateActionObject(alias, indices);

        logger.debug("Actions = " + actions);
        return post("/_aliases" , actions );
    }

    public String generateActionObject(String alias, String ...indices) {
        JsonObject actionsObject = new JsonObject();

        JsonArray actionsArray = new JsonArray();

        for (String index : indices) {
            JsonObject actionItem = new JsonObject();

            JsonObject aliasObject = new JsonObject();
            aliasObject.addProperty("index", index);
            aliasObject.addProperty("alias", alias);

            actionItem.add("remove", aliasObject);

            actionsArray.add(actionItem);
        }

        actionsObject.add("actions", actionsArray);

        return actionsObject.toString();
    }

//    public static void main(String[] a) throws Exception {
//        ESHttpClient c = new ESHttpClient("es5.sncrbda.dev.cloud.synchronoss.net", "datauser", "datauser");
//        System.out.println(c.esClusterVersion());
//        Map<String, String> mapping = new HashMap<>();
//        if (c.esIndexStructure("idx_xdf_test", "test_type", mapping) > 0){
//            System.out.println(mapping);
//        }
//
//        String idx_def =
//                "{'settings':{'number_of_shards':1},'mappings':{'test_type':{"
//                        + "'properties':{ 'field1':{ 'type':'string'}, 'field2':{ 'type':'date'} "
//                        + "}}}}";
//        c.esIndexCreate("idx_xdf_test_new", idx_def.replace("'", "\""));
//    }
}
