package sncr.xdf.esloader.esloadercommon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

/**
 * Created by skbm0001 on 30/1/2018.
 */
public class ESHttpClient {

  private static final Logger LOGGER = Logger.getLogger(ESHttpClient.class);
  private List<String> hosts = new ArrayList<>();

  private ESConfig esConfig;
  private static final String HTTP = "http://";
  private static final String HTTPS = "https://";
  private static final String MAPPING = "/_mapping/";
  private ChallengeResponse authentication = null;

  public ESHttpClient(String host, String user, String password) {
    if (!host.toLowerCase().startsWith(HTTP) && !host.toLowerCase().startsWith(HTTPS)) {
      this.hosts.add(HTTP + host);
    } else {
      this.hosts.add(host);
    }

    ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    if (user != null && password != null)
      authentication = new ChallengeResponse(scheme, user, password);
  }

  public ESHttpClient(ESConfig config) {
    List<String> esNodes = config.getEsHosts();
    int port = config.getEsPort() == 0 ? 9200 : config.getEsPort();
    String user = config.getEsUser();
    String pwd = config.getEsPassword();
    this.esConfig = config;

    this.hosts =
        esNodes
            .parallelStream()
            .map(
                host -> {
                  String tempHost;
                  if (!host.toLowerCase().startsWith(HTTPS) && !host.toLowerCase().startsWith(HTTP)) {
                    if (config.isEsSslEnabled()) {
                      tempHost = HTTPS + host + ":" + port;
                    } else {
                      tempHost = HTTP + host + ":" + port;
                    }
                  } else {
                    tempHost = host + ":" + port;
                  }
                  return tempHost;
                })
            .collect(Collectors.toList());
    LOGGER.debug("Hosts : " + this.hosts);
    ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
    if (user != null && pwd != null) authentication = new ChallengeResponse(scheme, user, pwd);
  }

  private String get(String url) {
    String retval = null;
    for (String host : this.hosts) {
      LOGGER.debug("Trying :" + host);
      String fullUrl = host + url;
      try {
        ClientResource cr = getClientResource(fullUrl);
        cr.get();
        if (cr.getStatus().isSuccess()) {
          retval = cr.getResponseEntity().getText();
        } else {
          LOGGER.error("Error occurred : " + cr.getStatus());
        }

        return retval;
      } catch (Exception exception) {
        LOGGER.warn("Unable to reach " + host);
        LOGGER.warn("Exception occurred  " + exception);
      }
    }

    return null;
  }

  private boolean head(String url, boolean isIndexExist) {
    for (String host : this.hosts) {
      LOGGER.debug("Trying : " + host);
      try {
        String fullUrl = host + url;
        LOGGER.debug("URL :" + fullUrl);
        ClientResource cr = getClientResource(fullUrl);
        cr.head();
        return cr.getStatus().isSuccess();
      } catch (ResourceException ex) {
        String message = isIndexExist ? "Index does not exist in the ES nodes cluster." : "Unable to reach " + host;
        LOGGER.warn(ex.getMessage());
        LOGGER.warn(message);
      }
    }
    return false;
  }

  private boolean delete(String url) {
    for (String host : hosts) {
      LOGGER.debug("Trying " + host);
      String fullUrl = host + url;
      LOGGER.debug("URL = " + fullUrl);
      try {
        ClientResource cr = getClientResource(fullUrl);
        cr.delete();
        return cr.getStatus().isSuccess();

      } catch (ResourceException exception) {
        LOGGER.warn("Unable to reach: " + host);
        LOGGER.warn("Warning occurred while deleting : " + exception);
      }
    }

    return false;
  }

  private boolean put(String url, String body) {
    for (String host : hosts) {
      String fullUrl = host + url;
      LOGGER.debug("URL = " + fullUrl);

      try {
        ClientResource cr = getClientResource(fullUrl);
        StringRepresentation jsonData = new StringRepresentation(body);
        jsonData.setMediaType(MediaType.APPLICATION_JSON);
        cr.put(jsonData);
        if (!cr.getStatus().isSuccess()) {
          LOGGER.error(cr.getStatus().getDescription());
          return false;
        } else {
          return true;
        }

      } catch (ResourceException exception) {
        LOGGER.warn("Unable to reach: " + host);
        LOGGER.warn("Warning", exception);
      }
    }

    return false;
  }

  private boolean post(String url, String body) {
    for (String host : this.hosts) {
      String fullUrl = host + url;
      try {
        ClientResource cr = getClientResource(fullUrl);
        LOGGER.debug("Full URL =" + fullUrl + ". Data = " + body);
        StringRepresentation jsonData = new StringRepresentation(body);
        jsonData.setMediaType(MediaType.APPLICATION_JSON);
        cr.post(jsonData);
        return cr.getStatus().isSuccess();
      } catch (ResourceException exception) {
        LOGGER.warn("Unable to reach " + host);
        LOGGER.warn("Warning occurred while creating : {}", exception);
      }
    }
    return false;
  }

  /**
   * Get ClientResource based on SSL configured for Elasticsearch.
   *
   * @param fullUrl
   * @return ClientResource
   */
  private ClientResource getClientResource(String fullUrl) {
    ClientResource cr = null;
    LOGGER.debug("Inside getClientResource starts here.");
    if (esConfig != null && esConfig.isEsSslEnabled()) {
      LOGGER.debug("esConfig.isEsSslEnabled(): " + esConfig.isEsSslEnabled());
      Client client = new Client(new Context(), Protocol.HTTPS);
      Series<Parameter> parameters = client.getContext().getParameters();
      URL url = null;
      try {
        url = new URL(esConfig.getKeyStorePath());
      } catch (MalformedURLException e) {
        LOGGER.error("Exception occurred while accesing the key store path: {}", e);
      }
      if (url != null) {
        parameters.add("truststorePath", url.getPath());
      }
      parameters.add("truststorePassword", esConfig.getStorePassword());
      parameters.add("truststoreType", "JKS");
      cr = new ClientResource(fullUrl);
      cr.setNext(client);
      if (authentication != null) cr.setChallengeResponse(authentication);
    } else {
      cr = new ClientResource(fullUrl);
      if (authentication != null) cr.setChallengeResponse(authentication);
    }
    LOGGER.debug("Inside getClientResource ends here.");
    return cr;
  }

  public String esClusterVersion() throws Exception {
    LOGGER.debug("Getting cluster version");
    String response = get("");
    if (response == null) {
      throw new Exception("Cant obtain version.");
    } else {
      String clusterVersion;
      try {
        clusterVersion =
            new JsonParser()
                .parse(response)
                .getAsJsonObject()
                .getAsJsonObject("version")
                .getAsJsonPrimitive("number")
                .getAsString();
      } catch (NullPointerException e) {
        clusterVersion = null;
      }
      return clusterVersion;
    }
  }

  public int esIndexStructure(String idx, String type, Map<String, String> mapping)
      throws Exception {
    LOGGER.debug("Getting ES index structure");
    String mappingString;
    if (type != null) {
      mappingString = get("/" + idx + MAPPING + type + "?include_type_name=true");
    } else {
      mappingString = get("/" + idx + MAPPING);
    }
    JsonObject mappingJson;
    try {
      // Try to parse and access mapping section of ES JSON
      mappingJson =
          new JsonParser()
              .parse(mappingString)
              .getAsJsonObject()
              .getAsJsonObject(idx)
              .getAsJsonObject("mappings")
              .getAsJsonObject(type)
              .getAsJsonObject("properties");
    } catch (NullPointerException e) {
      // We can't parse json - on of the elements is missing
      mappingJson = null;
    }
    if (mappingJson != null) {
      for (Map.Entry<String, JsonElement> e : mappingJson.entrySet()) {
        String fieldName = e.getKey();
        JsonElement descr = e.getValue();
        String fieldType = descr.getAsJsonObject().get("type").getAsString();
        if ("date".equals(fieldType) && descr.getAsJsonObject().has("format")) {
          String fieldFmt = descr.getAsJsonObject().get("format").getAsString();
          // It is possible to specify multiple formats for ES DATE/TIMESTAMP
          // It is not recommended for XDF
          // For loading data we will use the first one - this may lead to potential issues
          // if data is stored with multiple formats in source data set
          fieldFmt = fieldFmt.split("\\|\\|")[0];
          fieldType += "^" + fieldFmt;
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
   * @return Total number of records in the index/alias
   */
  public long getRecordCount(String index) throws Exception {
    LOGGER.debug("Getting record count");
    long count = 0;
    String countURL = "/" + index + "/_count";
    String response = get(countURL);
    LOGGER.debug("Response = " + response);
    if (response != null && response.length() != 0) {
      JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
      count = responseObject.get("count").getAsLong();
    }
    return count;
  }

  public boolean esIndexExists(String idx) {
    return head("/" + idx, true);
  }

  // Check if index type exists
  // Only supported in ES 7.x
  public boolean esTypeExists(String idx, String type) throws Exception {
    String clusterVersion = esClusterVersion();
    if (clusterVersion.startsWith("7.")) {
      return head("/" + idx + MAPPING + type,false);
    } else {
      throw new Exception(
          "TypeExists operation is not supported for Elastic Search cluster version "
              + clusterVersion);
    }
  }

  // Create Index
  public boolean esIndexCreate(String idx, String mapping ,boolean esTypeExists) {
      boolean flag = false;
      if (esTypeExists) {
          flag = put("/" + idx+"?include_type_name=true", mapping);
      } else {
          flag = put("/" + idx, mapping);
      }
      return flag;
  }

  /*
    mapping must contain only mapping properties,
    not whole index definition
  */
  public boolean esMappingCreate(String idx, String mappingName, String mapping) {
    return put("/" + idx + MAPPING + mappingName, mapping);
  }

  // Delete index
  public boolean esIndexDelete(String idx) {
    return delete("/" + idx);
  }

  // Safely delete indices
  public void esIndexSafeDelete(String... idx) {
    for (String s : idx) {
      // Check if index participates in any alias
      int aliasParticipation = esIndexAliasParticipation(s);
      LOGGER.debug("Alias Participation = " + aliasParticipation);
      if (aliasParticipation == 0) {
        // This index is not attached to any alias - delete it
        LOGGER.debug("Safe deleting index " + s);
        esIndexDelete(s);
      }
    }
  }

  // Returns number of aliases for given index
  public int esIndexAliasParticipation(String idx) {
    LOGGER.debug("Getting ES alias count");
    String aliases = get("/" + idx + "/_alias");
    if (aliases == null) {
      return -1;
    }

    JsonObject aliasesJson;
    try {
      // Try to parse and access mapping section of ES JSON
      aliasesJson =
          new JsonParser()
              .parse(aliases)
              .getAsJsonObject()
              .getAsJsonObject(idx)
              .getAsJsonObject("aliases");

    } catch (NullPointerException e) {
      // We can't parse json - on of the elements is missing
      aliasesJson = null;
    }

    if (aliasesJson != null) {
      Set<Map.Entry<String, JsonElement>> aliasSet = aliasesJson.entrySet();
      List<String> aliasList = aliasSet.stream().map(Map.Entry::getKey).collect(Collectors.toList());
      return aliasList.size();
    }
    return 0;
  }

  // Add index to alias
  public boolean esIndexAddAlias(String alias, String... idx) {
    String addAlias = "{ \"actions\" : [";
    String sequence = null;
    for (String s : idx) {
      if (sequence != null)
        sequence += ", {\"add\" : {\"index\" : \"" + s + "\" , \"alias\" : \"" + alias + "\"}}";
      else sequence = "{\"add\" : {\"index\" : \"" + s + "\" , \"alias\" : \"" + alias + "\"}}";
    }
    addAlias = addAlias + sequence + "]}";
    return post("/_aliases", addAlias);
  }

  public boolean esAliasExists(String alias) {
    return head("/_alias/" + alias, false);
  }

  // Return list of indexes with the given alias
  public List<String> esAliasListIndices(String alias) {
    LOGGER.debug("Getting list of indexes for the alias : " + alias);
    List<String> retval = new ArrayList<>();
    String aliasStr = get("/_alias/" + alias);
    if (aliasStr == null) return retval;
    JsonObject indexJson;
    try {
      // Try to parse and access mapping section of ES JSON
      indexJson = new JsonParser().parse(aliasStr).getAsJsonObject();
      for (Map.Entry<String, JsonElement> e : indexJson.entrySet()) {
        String indexName = e.getKey();
        retval.add(indexName);
      }
    } catch (NullPointerException e) {
      // We can't parse json - on of the elements is missing
      retval = null;
    }
    return retval;
  }

  public boolean esIndexRemoveAlias(String alias, String... indices) {
    String actions = generateActionObject(alias, indices);
    LOGGER.debug("Actions = " + actions);
    return post("/_aliases", actions);
  }

  public String generateActionObject(String alias, String... indices) {
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
}
