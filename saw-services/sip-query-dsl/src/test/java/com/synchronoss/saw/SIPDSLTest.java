package com.synchronoss.saw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.es.ESResponseParser;
import com.synchronoss.saw.es.ElasticSearchQueryBuilder;
import com.synchronoss.saw.es.SIPAggregationBuilder;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SIPDSL;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Unit test for simple App.
 */
public class SIPDSLTest {

    /**
     * Query Builder Tests with aggregation.
     */
    @Test
    public void testQueryWithAggregationBuilder() throws IOException, ProcessingException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample.json").getPath());
        ObjectMapper objectMapper = new ObjectMapper();
        SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
        ElasticSearchQueryBuilder elasticSearchQueryBuilder = new ElasticSearchQueryBuilder();
        String query = elasticSearchQueryBuilder.buildDataQuery(sipdsl);
        Assert.assertNotNull(query);
    }

    /**
     * Query Builder Tests for parsing the data :
     */
    @Test
    public void testResultParser() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample.json").getPath());
        ObjectMapper objectMapper = new ObjectMapper();
        SIPDSL sipdsl = objectMapper.readValue(file, SIPDSL.class);
        List<Field> dataFields =
            sipdsl.getSipQuery().getArtifacts().get(0).getFields();
        List<Field> aggregationFields = SIPAggregationBuilder.getAggregationField(dataFields);
        JsonNode jsonNode = objectMapper.readTree(new File(classLoader.getResource("response_sample.json").getPath()));
        ESResponseParser esResponseParser = new ESResponseParser(dataFields, aggregationFields);
        List<Object> result = esResponseParser.parseData(jsonNode);
        Assert.assertTrue(result.size()>0);
    }
}
