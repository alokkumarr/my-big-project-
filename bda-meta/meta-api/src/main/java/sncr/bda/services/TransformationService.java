package sncr.bda.services;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static sncr.bda.base.MetadataStore.delimiter;

import com.google.gson.JsonElement;
import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.context.ContextMetadata;
import sncr.bda.metastore.TransformationStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Optional;

import java.io.*;

/**
 * Created by srya0001 on 11/2/2017.
 */
public class TransformationService {

    private static final Logger logger = Logger.getLogger(TransformationService.class);
    private final TransformationStore ts;

    public TransformationService(String xdfDataRootSys) throws Exception {
        ts = new TransformationStore(xdfDataRootSys);
    }


    public String readOrCreateTransformation(ContextMetadata ctx, ComponentConfiguration componentConfiguration) throws Exception {


        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(ALLOW_COMMENTS, true)
                .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);

        OutputStream os = new ByteArrayOutputStream();
        mapper.writeValue(os, componentConfiguration);
        os.close();
        os.flush();
        String strCompConf = os.toString();
        int hc  = strCompConf.hashCode();
        String id = generateId(ctx.applicationID, ctx.componentName, hc);
        JsonElement transformation  = ts.read(id);
        if (transformation != null){
            logger.debug("Transformation with such configuration found and read from metastore");
            return id;
        }
        else{
            logger.debug("Create new transformation with configuration in metastore");
            ts.create(id, strCompConf);
            return id;
        }
    }

    private String generateId(String applicationID, String componentName, int hc) {
        return applicationID + delimiter + componentName + delimiter + hc;
    }

    public void updateStatus(String transformationID, String status, String startTs, String finishedTs, String ale_id, String batchID) throws Exception {
        updateStatus(transformationID, status, startTs, finishedTs, ale_id, batchID, Optional.ofNullable(null),Optional.ofNullable(null));
    }

    public void updateStatus(String transformationID, String status, String startTs, String finishedTs, String ale_id, String batchID, Optional<Integer> returnCode, Optional<String> errorDesc) throws Exception {
        ts.updateStatus(transformationID, status, startTs, finishedTs, ale_id, batchID, returnCode, errorDesc);
    }
}
