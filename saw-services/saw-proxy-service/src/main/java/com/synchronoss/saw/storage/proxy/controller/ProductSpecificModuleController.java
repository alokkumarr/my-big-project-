package com.synchronoss.saw.storage.proxy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.synchronoss.saw.storage.proxy.model.response.Valid;
import com.synchronoss.saw.storage.proxy.service.ProductSpecificModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@Api(value="The controller provides operations pertaining to interacting with MapperDB meta-store used for Product-specific-modules")
@RequestMapping(value = "/internal/proxy/storage/product-module")
public class ProductSpecificModuleController {

    private static final Logger logger = LoggerFactory.getLogger(ProductSpecificModuleController.class);
    // We are restricting the api's to perform crud operations only on ProductModules as of now, and since we don't want to take tableName as input from user to stop creating unnecessary tables. That's why its been hard coded here.
    private static String tableName = "productModules";

    private static JsonElement toJsonElement(String js){
        logger.debug("toJsonElement Called: String = ",js);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement;
        try {
            jsonElement = jsonParser.parse(js);
            logger.info("Parsed String = ",jsonElement);
            return jsonElement;
        }
        catch (JsonParseException jse)  {
            logger.error("Can't parse String to Json, JsonParseException occurred!\n");
            logger.error(jse.getStackTrace().toString());
            return null;
        }
    }

    @Autowired
    private ProductSpecificModuleService pms;

    @ApiOperation(value = "Provides ability to add document into ProductSpecificModules ", nickname = "insertDocToProductModule",
        notes = "", response = Valid.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    @RequestMapping(value = "/{id}",
        method = RequestMethod.POST,
        produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Valid addDocument(HttpServletRequest request,
                             HttpServletResponse response,
                             @PathVariable(name = "id", required = true) String id,
                             @RequestBody JsonNode jse) {
        logger.debug("Request Body String:{}", jse);

        /* Extract input parameters */
        final String js = jse.path("source").toString();
        JsonElement jsonElement = toJsonElement(js);
        Valid valid = new Valid();
        if (jsonElement!= null) {
            if (id == null){
                valid.setValid(false);
                valid.setError("ID can't be null or empty");
                response.setStatus(400);
                logger.error("Id can't be null or empty");
                return valid;
            }
            return pms.addDocument(tableName,id,jsonElement);
        }
        else {
            valid.setValid(false);
            valid.setError("Input String can't be parsed to JSonElement");
            return valid;
        }

    }

    @ApiOperation(value = "Provides ability to Update document in ProductSpecificModules ", nickname = "updateDocInProductModule",
        notes = "", response = Valid.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    @RequestMapping(value = "/{id}",
        method = RequestMethod.PUT,
        produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Valid updateDocument(HttpServletRequest request, HttpServletResponse response,
                                @PathVariable(name = "id", required = true) String id,
                                @RequestBody JsonNode jse) {
        logger.debug("Request Body String:{}", jse);

        /* Extract input parameters */
        final String js = jse.path("source").toString();
        JsonElement jsonElement = toJsonElement(js);
        Valid valid = new Valid();
        if(jsonElement != null) {
            if (id == null || id.isEmpty()){
                valid.setValid(false);
                valid.setError("ID can't be null or empty");
                logger.error("ID can't be null or empty");
                response.setStatus(400);
                return valid;
            }
            return pms.updateDocument(tableName,id,jsonElement);
        }
        else {
            valid.setValid(false);
            valid.setError("Input String can't be parsed to JSonElement");
            logger.error("Request Body Json is null !!");
            return valid;
        }

    }

    @ApiOperation(value = "Provides ability to delete document from ProductSpecificModules ", nickname = "deleteSpecificDoc",
        notes = "", response = Valid.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Valid deleteDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "id", required = true) String id) {
        if (id == null){
            Valid valid = new Valid();
            valid.setValid(false);
            valid.setError("ID can't be null or empty");
            logger.error("ID can't be null or empty");
            response.setStatus(400);
            return valid;
        }
        return pms.deleteDocument(tableName,id);
    }

    @ApiOperation(value = "Provides ability to read document from ProductSpecificModules ", nickname = "getSpecificProductModuleDoc",
        notes = "", response = JsonElement.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String readDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "id", required = true) String id) {
        if (id == null){
            logger.error("ID can't be null or empty");
            response.setStatus(400);
        }
        logger.debug("Json returned : ",pms.getDocument(tableName,id));
        return pms.getDocument(tableName,id).toString();
    }

    @ApiOperation(value = "Provides ability to fetch all documents from ProductSpecificModules ", nickname = "getAllProductModuleDocs",
        notes = "", response = JsonElement.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    @RequestMapping(value = "/docs", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String readAllDocuments(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Json returned : ",pms.getAllDocs(tableName));
        return pms.getAllDocs(tableName).toString();
    }

    @ApiOperation(value = "Provides ability to fetch filtered document/'s from ProductSpecificModules ", nickname = "getAllProductModuleDocs",
        notes = "", response = JsonElement.class)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Request has been accepted without any error"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
        @ApiResponse(code = 500, message = "Server is down. Contact System administrator")
    })
    //The request type is POST since we need request body to accept list of attribute-values to filter documents.
    @RequestMapping(value = "/docs", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String readDocumentsByCond(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,String> keyValues) {
        logger.debug("Json returned : ",pms.getAllDocs(tableName,keyValues));
        return pms.getAllDocs(tableName,keyValues).toString();
    }

}
