package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
public class TransportUtils {

    public static String buildDSK (String dataSecurityKey)throws JsonProcessingException, IOException
    {
        StringBuilder dskMkString = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        JsonNode objectNode = objectMapper.readTree(dataSecurityKey);
        DataSecurityKey dataSecurityKeyNode =
                objectMapper.treeToValue(objectNode,
                        DataSecurityKey.class);
        StringBuilder builder  = null;
        int dataSecuritySize = dataSecurityKeyNode.getDataSecuritykey().size();
        for (int i =0; i<dataSecurityKeyNode.getDataSecuritykey().size();i++)
        {
            builder=   new StringBuilder();
            builder.append(dataSecurityKeyNode.getDataSecuritykey().get(i).getName());
            builder.append(" IN (");
            int valueSize = dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().size();
            for (int j = 0; j<valueSize;j++){
                if (j!=valueSize-1)
                {
                    /* since this is having potential bug in initial implementation, So appending single "'" 
                    to avoid sql query error */
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j)+"'");
                    builder.append(",");
                }
                else {
                    builder.append("'"+dataSecurityKeyNode.getDataSecuritykey().get(i).getValues().get(j)+"'");
                }
            }
            builder.append(")");
            if (i!=dataSecuritySize-1){
                builder.append(" AND ");
            }
            dskMkString.append(builder);
        }
        return dskMkString.toString();
    }

}
