package sncr.bda.cli;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.avro.data.Json;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sncr.bda.datasets.conf.DataSetProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by skbm0001 on 29/3/2018.
 */
public class RequestTest {
    private Request req;
    private JsonParser parser;
    private JsonElement metadata;

    @Before
    public void setUp() {
        String metadataStr = null;
        parser = new JsonParser();

        StringWriter writer = new StringWriter();

        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("testmeta.jmeta");
            IOUtils.copy(stream, writer, "UTF-8");
            metadataStr = writer.toString();
            metadata = parser.parse(metadataStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        req = new Request(metadataStr);

    }

    @Test
    public void analyzeAndValidateCRUD() {
        try {
            Method analyzeMethod = Request.class.getDeclaredMethod("analyzeAndValidateCRUD", JsonObject.class);
            analyzeMethod.setAccessible(true);

            Field actionField = Request.class.
                    getDeclaredField("action");

            actionField.setAccessible(true);
            actionField.set(req, Request.Actions.create);


            analyzeMethod.invoke(req, metadata.getAsJsonObject());

            Field srcField = Request.class.getDeclaredField("src");
            srcField.setAccessible(true);

            JsonObject object = (JsonObject)srcField.get(req);

            assertEquals(true, object.has(DataSetProperties.CreatedTime.toString()));
            assertEquals(true, object.has(DataSetProperties.ModifiedTime.toString()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}