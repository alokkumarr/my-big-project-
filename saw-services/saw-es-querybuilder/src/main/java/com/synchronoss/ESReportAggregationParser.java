package com.synchronoss;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.querybuilder.model.report.DataField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ESReportAggregationParser {

    private static String GROUP_BY_FIELD = "group_by_field";
    private final static String DATA_FIELDS = "data_fields";
    private final static String KEY = "key";
    private final static String KEY_AS_STRING ="key_as_string";
    private final static String BUCKETS = "buckets";
    private final static String VALUE = "value";

    private String[] groupByFields;
    private List<DataField> dataFields;
    private List<DataField> aggregationFields;


    private static final Logger logger = LoggerFactory.getLogger(ESReportAggregationParser.class);

    public ESReportAggregationParser(List<DataField> dataFields,List<DataField> aggregationFields)
    {
        this.dataFields=dataFields;
        this.aggregationFields=aggregationFields;
    }

    /**
     * JSON node parser based on the report fields
     * to convert flatStructure.
     * @param jsonNode
     * @param dataObj
     * @param flatStructure
     * @param level
     * @return
     */
    private List<Object> jsonNodeParser(JsonNode jsonNode, Map dataObj, List<Object> flatStructure
                                        , int level)
    {
        JsonNode childNode = jsonNode;
        if (childNode.get(KEY)!=null)
        {
            String columnName = getColumnNames(groupByFields,level);
            if (childNode.get(KEY_AS_STRING)!=null)
                dataObj.put(columnName,childNode.get(KEY_AS_STRING).textValue());
            else if (childNode.get(KEY).isNumber())
            {
               switch (childNode.get(KEY).numberType()) {
                   case LONG:
                       dataObj.put(columnName,childNode.get(KEY).longValue());
                       break;
                   case BIG_INTEGER:
                       dataObj.put(columnName,childNode.get(KEY).bigIntegerValue());
                       break;
                   case FLOAT:
                       dataObj.put(columnName,childNode.get(KEY).floatValue());
                       break;
                   case DOUBLE:
                       dataObj.put(columnName,childNode.get(KEY).doubleValue());
                       break;
                   case BIG_DECIMAL :
                       dataObj.put(columnName,childNode.get(KEY).doubleValue());
                       break;
                   case INT:
                       dataObj.put(columnName,childNode.get(KEY).intValue());
                       break;
                       default: dataObj.put(columnName,childNode.get(KEY).textValue());
               }
            }
            else
                dataObj.put(columnName,childNode.get(KEY).textValue());
        }
        String childNodeName =  childNodeName(childNode);

        if (childNodeName!=null
                && childNode.get(childNodeName)!=null)
        {
            JsonNode jsonNode1 = childNode.get(childNodeName).get(BUCKETS);
            Iterator<JsonNode> iterable1 = jsonNode1.iterator();
            while(iterable1.hasNext())
            {
                JsonNode jsonNode2 = iterable1.next();
                jsonNodeParser(jsonNode2,dataObj,flatStructure,level+1);
            }
        }
        // if result contains only aggregated fields.
        else if (groupByFields.length==0 && childNode !=null)
        {
            Map<String,String> flatValues = new LinkedHashMap<>();
            for (DataField dataField : aggregationFields){
                String columnName = dataField.getName();
                flatValues.put(columnName, String.valueOf(childNode.get(columnName).get(VALUE)));
            }
            flatStructure.add(flatValues);
        }
        else
        {
            Map<String,String> flatValues = new LinkedHashMap<>();
            flatValues.putAll(dataObj);
            for (DataField dataField : aggregationFields){
                String columnName = dataField.getName();
                flatValues.put(columnName, String.valueOf(childNode.get(columnName).get(VALUE)));
            }
            flatStructure.add(flatValues);
        }
        return flatStructure;
    }

    /**
     *  ES response parsing as JSON Node.
     * @param jsonNode
     * @return
     */
    public List<Object> parseData(JsonNode jsonNode)
    {
        logger.debug(this.getClass().getName() + " parseData starts here");
      //  JsonNode jsonNode1 = jsonNode.get(DATA);
        prepareGroupByFields(dataFields);
        Map<String,String> dataObj= new LinkedHashMap<>();
        List<Object> flatStructure = new ArrayList<>();
        flatStructure = jsonNodeParser(jsonNode,dataObj,flatStructure,0);
        logger.debug(this.getClass().getName() + " parseData ends here");
        return flatStructure;
    }

    /**
     *
     * @param fieldmap
     * @param level
     * @return
     */
    private String getColumnNames(String [] fieldmap , int level )
    {
        /** .keyword may present in the es-mapping fields
         take out form the columnName to get actual column name
         if present */
        int index = fieldmap.length-level;
        String columnName = fieldmap[index];
        String [] split = columnName.split("\\.");
        if (split.length>=2)
            return split[0];
        return columnName;
    }

    /**
     *
     * @param jsonNode
     */
    private String childNodeName(JsonNode jsonNode) {
        Iterator<String> keys = jsonNode.fieldNames();
        while (keys.hasNext())
        {
            String key = keys.next();
            if(key.contains(GROUP_BY_FIELD))
            {
                return key;
            }
        }
        return null;
    }

    /**
     * Fetch the group By fields for parsing aggregation result.
     */
    private void prepareGroupByFields(List<DataField> dataFields)
    {
        groupByFields = new String[dataFields.size()-aggregationFields.size()];
        int fieldCount =0;
        for (DataField dataField :dataFields)
        {
            if (dataField.getAggregate()==null)
                groupByFields[fieldCount++]=dataField.getColumnName();
        }
        logger.debug(this.getClass().getName() + " prepareGroupByFields ends");
    }


}
