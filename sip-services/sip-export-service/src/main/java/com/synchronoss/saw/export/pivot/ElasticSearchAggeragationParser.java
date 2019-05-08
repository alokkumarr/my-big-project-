package com.synchronoss.saw.export.pivot;

import com.fasterxml.jackson.databind.JsonNode;

import com.synchronoss.saw.export.generate.ExportBean;
import com.synchronoss.saw.export.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * This class used to parse the Elastic search aggregation
 * json response for pivot analysis.
 */
public class ElasticSearchAggeragationParser {

    private String[] pivotFields ;
    private Analysis analysis;

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchAggeragationParser.class);

    public ElasticSearchAggeragationParser(Analysis analysis)
    {
       this.analysis = analysis;
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
    private List<Object> jsonNodeParser(JsonNode jsonNode, Map dataObj, List<Object> flatStructure,
                                       String[] pivotFields, int level)
    {
        JsonNode childNode = jsonNode;
        if (childNode.get(PivotParsingConstants.KEY)!=null)
        {
            String columnName = getColumnNames(pivotFields,level);
            if (childNode.get(PivotParsingConstants.KEY_AS_STRING)!=null)
                dataObj.put(columnName,childNode.get(PivotParsingConstants.KEY_AS_STRING).textValue());
            else
                dataObj.put(columnName,childNode.get(PivotParsingConstants.KEY).textValue());
        }
        String childNodeName =  childNodeName(childNode);

        if (childNodeName!=null
                && childNode.get(childNodeName)!=null)
        {
            JsonNode jsonNode1 = childNode.get(childNodeName).get(PivotParsingConstants.BUCKETS);
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
            for (DataField dataField : analysis.getSqlBuilder().getDataFields()){
                String columnName = dataField.getColumnName();
                flatValues.put(columnName, String.valueOf(childNode.get(columnName).get(PivotParsingConstants.VALUE)));
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
        JsonNode jsonNode1 = jsonNode.get(PivotParsingConstants.DATA);
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
            if(key.contains(PivotParsingConstants.ROW_LEVEL)
                    || key.contains(PivotParsingConstants.COLUMN_LEVEL))
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
        List<RowField> rowFields = analysis.getSqlBuilder().getRowFields();
        List<ColumnField> columnFields = analysis.getSqlBuilder().getColumnFields();
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

    /**
     *
     * @param exportBean
     * @param analysis
     */
    public void setColumnDataType(ExportBean exportBean, Analysis analysis)
    {
        logger.debug(this.getClass().getName() + " setColumnDataType starts");
        List<RowField> rowFields = analysis.getSqlBuilder().getRowFields();
        List<ColumnField> columnFields = analysis.getSqlBuilder().getColumnFields();
        List<DataField> dataFields = analysis.getSqlBuilder().getDataFields();
        DataField.Type[] columnDataType = new DataField.Type[rowFields.size()
                +columnFields.size()+dataFields.size()];
        int count = 0;
        for ( RowField rowField : rowFields )
        {
            switch (rowField.getType()) {
                case DATE:
                    columnDataType[count++] = DataField.Type.DATE;
                    break;
                case TIMESTAMP:
                    columnDataType[count++] = DataField.Type.TIMESTAMP;
                    break;
                case LONG:
                    columnDataType[count++] = DataField.Type.LONG;
                    break;
                case DOUBLE:
                    columnDataType[count++] = DataField.Type.DOUBLE;
                    break;
                case INT:
                    columnDataType[count++] = DataField.Type.INT;
                    break;
                case FLOAT:
                    columnDataType[count++] = DataField.Type.FLOAT;
                    break;
                case STRING:
                    columnDataType[count++] = DataField.Type.STRING;
                    break;

            }
        }

        for ( ColumnField columnField : columnFields )
        {
            switch (columnField.getType()) {
                case DATE:
                    columnDataType[count++] = DataField.Type.DATE;
                    break;
                case TIMESTAMP:
                    columnDataType[count++] = DataField.Type.TIMESTAMP;
                    break;
                case LONG:
                    columnDataType[count++] = DataField.Type.LONG;
                    break;
                case DOUBLE:
                    columnDataType[count++] = DataField.Type.DOUBLE;
                    break;
                case INT:
                    columnDataType[count++] = DataField.Type.INT;
                    break;
                case FLOAT:
                    columnDataType[count++] = DataField.Type.FLOAT;
                    break;
                case STRING:
                    columnDataType[count++] = DataField.Type.STRING;
                    break;
            }
        }

        for ( DataField dataField : dataFields )
        {
            columnDataType[count++] = dataField.getType();
            }
            exportBean.setColumnDataType(columnDataType);
        logger.debug(this.getClass().getName() + " setColumnDataType ends");
        }

    }

