package sncr.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * This generic class can be used as to access internal services
 * @author spau0004
 *
 */
public class InternalServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(InternalServiceClient.class);
    private String url = "";
    private String dataLocation;
    public InternalServiceClient(String url) {
        super();
        this.url = url;
    }
    /**
     * This method will be used to access the semantic service to get the
     * details of semantic a particular semantic node
     * @param object
     * @return Object
     */
    public String retrieveObject(Object object) throws IOException {
        logger.trace("request from retrieveObject :"+ object);
        Object node = null;
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        node = mapper.readValue(result.toString(), object.getClass());
        JsonNode jsonNode = mapper.readTree(mapper.writeValueAsString(node));
        ObjectNode rootNode = (ObjectNode) jsonNode;
        logger.trace("response object :" + mapper.writeValueAsString(node));
        return mapper.writeValueAsString(node);
    }

}
