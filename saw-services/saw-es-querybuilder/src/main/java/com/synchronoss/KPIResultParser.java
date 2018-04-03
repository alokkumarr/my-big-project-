package com.synchronoss;


import java.util.Map;
import java.util.List;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synchronoss.querybuilder.model.kpi.DataField;

public class KPIResultParser {

    private final static String VALUE = "value";

    private List<DataField> dataFields;

    private static final Logger logger = LoggerFactory.getLogger(KPIResultParser.class);

    public KPIResultParser(List<DataField> dataFields)
    {
        this.dataFields=dataFields;
    }

    /**
     * JSON node parser based on the KPI fields
     * to convert .
     * @param jsonNode
     * @return
     */
    public Map jsonNodeParser(JsonNode jsonNode) {
        Map<String, Object> dataObj = new HashMap();
        for (DataField dataField : dataFields) {
            logger.info("KPI result parsing for data "+ dataField.getColumnName());
            Map<String, String> data = new HashMap<>();
            for(DataField.Aggregate aggregate : dataField.getAggregate()) {
                // This is used to parse range value
                JsonNode value = jsonNode.get(dataField.getName() + "_" +aggregate.value());
                data.put("_"+aggregate.value(), String.valueOf(value.get(VALUE)));
            }
            dataObj.put(dataField.getName(), data);
        }
        return dataObj;
    }
}
