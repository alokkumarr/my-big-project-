package com.synchronoss.saw.storage.proxy.service.productSpecificModuleService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synchronoss.saw.storage.proxy.model.response.ProductModuleDocs;
import com.synchronoss.saw.storage.proxy.model.response.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
@Component
public class ProductSpecificModuleServiceRESTImpl implements ProductSpecificModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ProductSpecificModuleServiceRESTImpl.class);

    @Value("${metastore.base}")
    @NotNull
    private String root;

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
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            productModuleMetaStore.create(id,doc);
            valid.setValid(true);
            valid.setValidityMessage("Document added successfully!");
            logger.debug("Document added successfully!");
        } catch (Exception e) {
            valid.setError("Failed to add document to table,Exception occurred!");
            logger.error("Failed to add document to table,Exception occurred!");
            valid.setValid(false);
            logger.error(e.getStackTrace().toString());
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
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            productModuleMetaStore.update(id,doc);
            valid.setValid(true);
            valid.setValidityMessage("Document updated successfully!");
            logger.debug("Document updated successfully!");
        } catch (Exception e) {
            valid.setError("Failed updating document, Exception occurred!");
            logger.error("Failed updating document, Exception occurred!");
            valid.setValid(false);
            logger.error(e.getStackTrace().toString());
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
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            productModuleMetaStore.delete(id);
            valid.setValid(true);
            valid.setValidityMessage("Document deleted successfully!");
            logger.debug("Document deleted successfully!");
        } catch (Exception e) {
            valid.setError("Failed to delete document from table,Exception occurred!");
            logger.error("Failed to delete document from table,Exception occurred!");
            valid.setValid(false);
            logger.error(e.getStackTrace().toString());
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
    public ProductModuleDocs getDocument(String tableName, String id) {
        logger.debug("Calling ProductModuleMetaStore obj to read document with id : "+id);
        logger.debug("TableName :",tableName);
        JsonElement doc = null;
        ProductModuleDocs productModuleDocs = new ProductModuleDocs();
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            doc = productModuleMetaStore.read(id);
            System.out.println("Retrieved doc from MetaStore :"+doc);
            System.out.println("doc as String : "+doc.toString());
            if (doc == null)    {
                productModuleDocs.setValid(false);
                productModuleDocs.setMessage("id given is not present");
                logger.error(id," : not present in MaprDB");
                return productModuleDocs;
            }
            System.out.println("doc as gson.toJson : "+gson.toJson(doc));
            productModuleDocs.setDoc((List<JsonObject>) doc);
            System.out.println("doc strored in bean : "+productModuleDocs.getDoc());
            productModuleDocs.setValid(true);
            productModuleDocs.setMessage("Document Retrieved successfully");
            logger.debug("Retrieved Document = ",doc);
            return productModuleDocs;
        } catch (Exception e) {
            logger.error("Failed to retrieve document,Exception occurred!");
            logger.error(e.getStackTrace().toString());
            productModuleDocs.setValid(false);
            productModuleDocs.setMessage("Failed to retrieve document!!");
            return productModuleDocs;
        }
    }

    /**O
     * Fetch all documents based on queryCondition
     * @param tableName
     * @param keyValues : Attribute - values
     * @return
     */
    @Override
    public ProductModuleDocs getAllDocs(String tableName, Map<String, String> keyValues) {
        List<JsonObject> docs = null;
        ProductModuleDocs productModuleDocs = new ProductModuleDocs();
        try {
            ProductModuleMetaStore productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            String res = gson.toJson(productModuleMetaStore.searchAll(keyValues));
            JsonParser parser =  new JsonParser();
            JsonObject o = parser.parse(res).getAsJsonObject();
            docs.add(o);
            System.out.println("Retrieved docs from MetaStore :"+docs);
            System.out.println("docs as String : "+docs.toString());
            if (res.isEmpty()) {
                productModuleDocs.setValid(true);
                productModuleDocs.setMessage("No documents present for given filters!!");
                productModuleDocs.setDoc(docs);
                return productModuleDocs;
            }
            System.out.println("doc as gson.toJson : "+gson.toJson(docs));
            productModuleDocs.setDoc(docs);
            System.out.println("doc strored in bean : "+productModuleDocs.getDoc());
            productModuleDocs.setValid(true);
            productModuleDocs.setMessage("Document Retrieved successfully!!");
            return productModuleDocs;
        } catch (Exception e) {
            logger.error("Failed to retrieve document,Exception occurred!");
            logger.error(e.getStackTrace().toString());
            productModuleDocs.setValid(false);
            productModuleDocs.setMessage("Failed to retrieve documents!!");
            return productModuleDocs;
        }

    }

    /**
     * Retrieve all the documents of a given table
     * @param tableName
     * @return
     */
    @Override
    public ProductModuleDocs getAllDocs(String tableName) {
        ProductModuleMetaStore productModuleMetaStore;
        List<JsonObject> docs = null;
        ProductModuleDocs productModuleDocs = new ProductModuleDocs();
        try {
            productModuleMetaStore = new ProductModuleMetaStore(tableName,root);
            String res = gson.toJson(productModuleMetaStore.searchAll());
            JsonParser parser =  new JsonParser();
            JsonObject o = parser.parse(res).getAsJsonObject();
            docs.add(o);
            if (res.isEmpty()) {
                productModuleDocs.setValid(true);
                productModuleDocs.setMessage("No document present for given table!!");
                productModuleDocs.setDoc(docs);
                return productModuleDocs;
            }
            productModuleDocs.setDoc(docs);
            productModuleDocs.setValid(true);
            productModuleDocs.setMessage("Document retrieved successfully!! ");
            return productModuleDocs;
        } catch (Exception e) {
            logger.error("Failed to retrieve documents,Exception occurred!");
            logger.error(e.getStackTrace().toString());
            productModuleDocs.setValid(false);
            productModuleDocs.setMessage("Failed to retrieve documents!!");
            return productModuleDocs;
        }

    }

}
