package sncr.xdf.esloader.esloadercommon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import sncr.bda.conf.Alias;
import sncr.bda.conf.ESLoader;
import sncr.bda.core.file.HFileOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by skbm0001 on 30/1/2018.
 */
public class ElasticSearchStructureManager {

    private static final Logger logger = Logger.getLogger(ElasticSearchStructureManager.class);

    private String esMappingFile;
    private String destinationIndexAndTypePattern;
    private DateTime now;
    private ESLoader esLoaderSection;
    private Map<String, Alias.Mode> aliases;

    //
    public final static String PARTITION = "$PARTITION";

    public ElasticSearchStructureManager(ESLoader configuration){
        //this.objectName = objectName;
        //this.configuration = configuration;
        this.now = new DateTime();
        this.aliases = new HashMap<>();

        this.esLoaderSection = configuration;

        // ES index mapping file location
        if(this.esLoaderSection != null){

            this.destinationIndexAndTypePattern = this.esLoaderSection.getDestinationIndexName();
            if(this.destinationIndexAndTypePattern.isEmpty())
                this.destinationIndexAndTypePattern = null;

            this.esMappingFile = configuration.getIndexMappingfile();

            // Store information about aliases if configured
            if (this.esLoaderSection.getAliases() != null && this.esLoaderSection.getAliases().size() != 0) {
                for(Alias a : this.esLoaderSection.getAliases()){
                    String aliasName = a.getAliasName();
                    if(!aliasName.isEmpty()){
                        this.aliases.put(aliasName, a.getMode());
                    }
                }
            }
        }
    }

    public boolean elasticSearchLoaderConfigured(){
        return (esLoaderSection != null) && (getDestinationIndexAndTypePattern() != null);
    }

    public String getDestinationIndexAndTypePattern() {
        return this.destinationIndexAndTypePattern;
    }

    public String getIndexMappingFile(){
        return this.esMappingFile;
    }

    public String getParsedIndexNameAndType(String partition) throws Exception {
        String retval = getDestinationIndexAndTypePattern();
        if(retval != null) {
            retval = parseIndexName(retval, partition, now);
        }
        return retval;
    }

    //
    // Create ElasticSearch index if not exists
    //

    public void CreateIfNotExists(ESHttpClient esClient, String ... arrayIndexAndType) throws Exception {

        String mappings = null;
        for(String indexAndType : arrayIndexAndType) {
            if(indexAndType != null) {
                String indexName = getIndex(indexAndType);
                String typeName = getType(indexAndType);
                logger.info("Trying to creating index :" + indexName);

                // First check if index exists
                // Second make sure appropriate mapping is also exists
                boolean indexExists = esClient.esIndexExists(indexName);
                boolean mappingExists = esClient.esTypeExists(indexName, typeName);

                if (!indexExists || !mappingExists) {
                    // Index doesn't exists - try to create it
                    // First, check if mapping file is configured

                    String mappingsFile = getIndexMappingFile();
                    if (mappingsFile != null) {
                        // Load file
                        if (mappings == null)
                            mappings = HFileOperations.readFile(mappingsFile);

                        logger.info("Loading " + mappingsFile);
                        if(!indexExists){
                            // Create Index AND Mapping
                            // Create Elastic Search index
                            if (!esClient.esIndexCreate( indexName, mappings)) {
                                throw new Exception("Failed to create elasticSearch index.");
                            }
                            logger.info("Index created: " + indexName + "/" + typeName);
                        } else {
                            if(!esClient.esMappingCreate(indexName, typeName, ExtractMapping(typeName, mappings))) {
                                throw new Exception("Failed to create elasticSearch mapping.");
                            }
                            logger.info("Mapping created: " + indexName + "/" + typeName);
                        }

                        // Index created
                    } else {
                        throw new Exception("Index " + indexName + "/" + typeName + " doesn't exist. "
                                + "Can't create new index because index mappings file is not configured.");
                    } //<-- if(mappingsFile != null)...
                } else {
                    // Index exists - do nothing
                    logger.info("Index " + indexName + "/" + typeName + " already exists");
                } //<-- if (!ElasticSearchUtil.esIndexExists(esClient, indexName))...
            }
        }
    }

    //
    // Create ElasticSearch index if not exists
    //
    public void DeleteIndex(ESHttpClient esClient, String ... arrayIndexAndType) throws Exception {
        for(String indexAndType : arrayIndexAndType) {
            if(indexAndType != null) {
                String indexName = getIndex(indexAndType);
                esClient.esIndexDelete(indexName);
                logger.info("Deleted Index: " + indexName);
            }
        }
    }

    //
    // Process aliases
    //
    public void ProcessAliases(ESHttpClient esClient, String ... arrayIndexAndType) throws Exception {

        // Extract index names
        List<String> arrayIndex = new ArrayList<>();
        for(String s : arrayIndexAndType){
            if(s != null){
                arrayIndex.add(getIndex(s));
            }
        }

        logger.debug("Array Index = " + arrayIndex);

        if(arrayIndex.size() > 0) {
            logger.debug("Aliases = " + aliases);
            for (Map.Entry<String, Alias.Mode> alias : aliases.entrySet()) {
                boolean b = false;
                String errorMessage = "";
                String aliasName = alias.getKey();
                logger.info("Processing command '" + alias.getValue().toString() + "' for '" + aliasName + "' alias.");
                logger.info("List of new indices : " + arrayIndex);
                switch (alias.getValue()) {
                    case APPEND: {
                        errorMessage = "Cant add alias " + aliasName + " to indices " + arrayIndex;
                        b = esClient.esIndexAddAlias( aliasName, arrayIndex.toArray(new String[arrayIndex.size()]));
                        break;
                    }
                    case REPLACE: {
                        if(!esClient.esAliasExists(alias.getKey())){
                            // Alias doesn't exists - just append and create new one
                            errorMessage = "Cant add alias " + aliasName + " to index (indices) " + arrayIndex;
                            b = esClient.esIndexAddAlias(
                                    alias.getKey(),
                                    arrayIndex.toArray(new String[arrayIndex.size()]));
                        } else {
                            // Get list of indices currently attached to alias
                            List<String> oldIndices =  esClient.esAliasListIndices( alias.getKey());

                            if(oldIndices.size() > 0) {
                                // Detach those indices from alias
                                errorMessage = "Can't remove alias "  + aliasName + " from the list of existing indices " + oldIndices;
                                b = esClient.esIndexRemoveAlias( alias.getKey(),
                                        oldIndices.toArray(new String[oldIndices.size()]));
                                if (b) {
                                    // Attach new indices to alias
                                    errorMessage = "Can't add alias "  + aliasName + " to the list of indices " + arrayIndex;
                                    b = esClient.esIndexAddAlias(alias.getKey(),
                                            arrayIndex.toArray(new String[arrayIndex.size()]));
                                    if (b) {
                                        // Drop old indices if they are not part of other aliases
                                        esClient.esIndexSafeDelete(oldIndices.toArray(new String[oldIndices.size()]));
                                    } // <-- Add alias successfull
                                } //<-- Disassociate index successfull
                            } else {
                                b = false;
                                errorMessage = "Cant retrieve list of indices for " + aliasName + " alias.";
                            }
                        } //<-- if(!ElasticSearchUtil.esAliasExists())...
                        break;
                    }
                } //<-- switch
                if(!b){
                    // Error - throw exception
                    throw new Exception(errorMessage);
                }
                logger.info("Processed alias '" + aliasName + "'");
            } //<-- for(...)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    // Parse Index/Type string value and extract Index
    public static String getIndex(String s) throws Exception {
        String[] indexTypeSlit = s.split("/");
        if(indexTypeSlit.length != 2) {
            throw new Exception("Invalid ES index given; expecting [index]/[type] - received " + s);
        }

        return indexTypeSlit[0];
    }

    // Parse Index/Type string value and extract Type
    public static String getType(String s) throws Exception {
        String[] indexTypeSlit = s.split("/");
        if(indexTypeSlit.length != 2) {
            throw new Exception("Invalid ES index given; expecting [index]/[type] - received " + s);
        }
        return indexTypeSlit[1];
    }

    // Create index name based on template
    public static String parseIndexName(String template, String partition, DateTime now) throws Exception{
        String[] tmp = template.split("/");
        String indexName = tmp[0];
        Pattern patternIndexName = Pattern.compile("\\{(.*?)\\}");

        Matcher matchPattern = patternIndexName.matcher(tmp[0]);

        boolean result = matchPattern.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String format = matchPattern.group(1);
                String replacement;
                if (format.equals(PARTITION)) {
                    replacement = partitionToName(partition);
                } else {
                    replacement = now.toString(format);
                }
                matchPattern.appendReplacement(sb, replacement);
                result = matchPattern.find();
            } while (result);
            matchPattern.appendTail(sb);
            indexName = sb.toString();
        }
        return indexName.toLowerCase() + "/" + tmp[1];
    }

    private static String partitionToName(String partition) throws Exception {
        throw new Exception("$PARTITION is not currently supported");
        //return partition;
    }

    private static String ExtractMapping(String typeName, String mappings) {
        String retval = "";
        JsonObject jo = new JsonParser().parse(mappings).getAsJsonObject();
        if(jo.get("mappings") != null && jo.get("mappings").getAsJsonObject().get(typeName) != null){
            retval = jo.get("mappings").getAsJsonObject().get(typeName).getAsJsonObject().toString();
        } else {
            logger.error("Can't find mapping definition for " + typeName + ". Please check mapping definition file.");
        }
        return retval;
    }
}