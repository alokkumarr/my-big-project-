package com.synchronoss.saw.storage.proxy.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.synchronoss.saw.storage.proxy.model.response.Valid;
import org.ojai.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sncr.bda.metastore.ProductModuleMetaStore;

import java.util.Map;

@Component
public class ProductSpecificModuleServiceRESTImpl implements ProductSpecificModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ProductSpecificModuleServiceRESTImpl.class);
    private static String xdfRoot = "hdfs:///var/sip";
    Gson gson = new Gson();

    /**
     * Method adds a document to a MaprDB Table
     * @param tableName
     * @param id : unique id for a Doc
     * @param doc : a valid json doc which is schema free
     * @return
     */
    @Override
    public Valid addDocument(String tableName, String id, JsonElement doc) {
        logger.debug("Calling ProductModuleMetaStore obj to add a doc of id :",id);
        logger.debug("TableName :",tableName);
        Valid valid = new Valid();
        // TODO: Set XDF ROOT dir value
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            productModuleMetaStore.create(id,doc);
            valid.setValid(true);
            valid.setValidityMessage("Document added successfully!");
            logger.debug("Document added successfully!");
        } catch (Exception e) {
            valid.setError("Failed to add document to table,Exception occurred!");
            logger.error("Failed to add document to table,Exception occurred!");
            valid.setValid(false);
            e.printStackTrace();
        }
        return valid;
    }

    /**
     * Method updates a document in maprDB table, updating always replaces old doc with new doc.
     * @param tableName
     * @param id
     * @param doc
     * @return
     */
    @Override
    public Valid updateDocument(String tableName, String id, JsonElement doc) {
        logger.debug("Calling ProductModuleMetaStore obj to update a doc of id :",id);
        logger.debug("TableName :",tableName);
        Valid valid = new Valid();
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            productModuleMetaStore.update(id,doc);
            valid.setValid(true);
            valid.setValidityMessage("Document updated successfully!");
            logger.debug("Document updated successfully!");
        } catch (Exception e) {
            valid.setError("Failed updating document, Exception occurred!");
            logger.error("Failed updating document, Exception occurred!");
            valid.setValid(false);
            e.printStackTrace();
        }

        return valid;
    }

    /**
     * Delete a document from MaprDB table. This will be a hard delete.
     * @param tableName
     * @param id
     * @return
     */
    @Override
    public Valid deleteDocument(String tableName, String id) {
        logger.debug("Calling ProductModuleMetaStore obj to delete a doc of id :",id);
        logger.debug("TableName :",tableName);
        Valid valid = new Valid();
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            productModuleMetaStore.delete(id);
            valid.setValid(true);
            valid.setValidityMessage("Document deleted successfully!");
            logger.debug("Document deleted successfully!");
        } catch (Exception e) {
            valid.setError("Failed to delete document from table,Exception occurred!");
            logger.error("Failed to delete document from table,Exception occurred!");
            valid.setValid(false);
            e.printStackTrace();
        }

        return valid;
    }

    /**
     * Fetch a document from MaprDB table based on provided id.
     * @param tableName
     * @param id
     * @return
     */
    @Override
    public String getDocument(String tableName, String id) {
        logger.debug("Calling ProductModuleMetaStore obj to read document with id : "+id);
        logger.debug("TableName :",tableName);
        JsonElement doc = null;
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            doc = productModuleMetaStore.read(id);
            logger.debug("Retrieved Document = ",doc);
        } catch (Exception e) {
            logger.error("Failed to retrieve document,Exception occurred!");
            e.printStackTrace();
        }

        return gson.toJson(doc);
    }

    /**
     * Fetch all documents based on queryCondition
     * @param tableName
     * @param keyValues : Attribute - values
     * @return
     */
    @Override
    public String getAllDocs(String tableName, Map<String, String> keyValues) {
        Map<String, Document> docs = null;
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            docs = productModuleMetaStore.searchAll(keyValues);
        } catch (Exception e) {
            logger.error("Failed to retrieve document,Exception occurred!");
            e.printStackTrace();
        }
            return gson.toJson(docs);
    }

    /**
     * Retrieve all the documents of a given table
     * @param tableName
     * @return
     */
    @Override
    public String getAllDocs(String tableName) {
        ProductModuleMetaStore productModuleMetaStore;
        try {
            productModuleMetaStore = new ProductModuleMetaStore(tableName,xdfRoot);
            return gson.toJson(productModuleMetaStore.searchAll());
        } catch (Exception e) {
            logger.error("Failed to retrieve documents,Exception occurred!");
            e.printStackTrace();
            return null;
        }

    }

}
