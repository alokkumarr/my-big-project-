package com.synchronoss.saw.storage.proxy.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.storage.proxy.model.ColumnField;
import com.synchronoss.saw.storage.proxy.model.DataField;
import com.synchronoss.saw.storage.proxy.model.RowField;
import com.synchronoss.saw.storage.proxy.model.SqlBuilder;


/**
 * This class used to parse the Elastic search aggregation
 * json response for pivot analysis.
 */
public class PivotSNCRFlattener {

  public final static String ROW_LEVEL = "row_level";
  public final static String COLUMN_LEVEL ="column_level" ;
  public final static String DATA = "data";
  public final static String KEY = "key";
  public final static String KEY_AS_STRING ="key_as_string";
  public final static String BUCKETS = "buckets";
  public final static String VALUE = "value";

    private String[] pivotFields ;
    private SqlBuilder builder;


    private static final Logger logger = LoggerFactory.getLogger(PivotSNCRFlattener.class);

    public PivotSNCRFlattener(SqlBuilder builder)
    {
      this.builder = builder;
       preparePivotFields();
    }

    /**
     * JSON node parser based on the pivot fields
     * to convert flatStructure.
     * @param jsonNode
     * @param dataObj
     * @param flatStructure
     * @param pivotFields
     * @param level
     * @return
     */
    private List<Object> jsonNodeParser(JsonNode jsonNode, Map<String,String> dataObj, List<Object> flatStructure,
                                       String[] pivotFields, int level)
    {
        JsonNode childNode = jsonNode;
        if (childNode.get(KEY)!=null)
        {
            String columnName = getColumnNames(pivotFields,level);
            if (childNode.get(KEY_AS_STRING)!=null)
                dataObj.put(columnName,childNode.get(KEY_AS_STRING).textValue());
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
                jsonNodeParser(jsonNode2,dataObj,flatStructure,pivotFields,level+1);
            }
        }
        else
        {
            Map<String,String> flatValues = new LinkedHashMap<>();
            flatValues.putAll(dataObj);
            for (DataField dataField : builder.getDataFields()){
                String columnName = dataField.getColumnName();
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
        JsonNode jsonNode1 = jsonNode.get(DATA);
        Map<String,String> dataObj= new LinkedHashMap<>();
        List<Object> flatStructure = new ArrayList<>();
        flatStructure = jsonNodeParser(jsonNode1,dataObj,flatStructure,pivotFields,0);
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
        String columnName = fieldmap[level-1];
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
            if(key.contains(ROW_LEVEL)
                    || key.contains(COLUMN_LEVEL))
            {
                return key;
            }
        }
        return null;
    }



    /**
     * Combine pivot row level and column level as pivot fields,
     * for parsing aggregation result.
     */
    private void preparePivotFields()
    {
        logger.debug(this.getClass().getName() + " preparePivotFields starts");
        List<RowField> rowFields = builder.getRowFields();
        List<ColumnField> columnFields = builder.getColumnFields();
        pivotFields = new String[rowFields.size()+columnFields.size()];
        int fieldCount =0;
        for (RowField rowField :rowFields)
        {
            pivotFields[fieldCount++] =rowField.getColumnName();
        }
        for (ColumnField columnField :columnFields)
        {
            pivotFields[fieldCount++] =columnField.getColumnName();
        }
        logger.debug(this.getClass().getName() + " preparePivotFields ends");
    }

    

    }

