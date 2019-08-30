package com.synchronoss.saw.apipull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.BodyParameters;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.HttpMethod;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import com.synchronoss.saw.apipull.service.HttpClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class HttpClientTest {

  RestTemplate restTemplate;
  HttpClient httpClient;
  ApiResponse apiResponse;
  Gson gson = new GsonBuilder().setPrettyPrinting().create();
  String url =
      "https://openweathermap.org/data/2.5/forecast/hourly?zip=94040&appid=b6907d289e10d714a6e88b30761fae22";
  List<HeaderParameter> headerParameterList = new ArrayList<>();

  @Before
  public void init() {
    restTemplate = new RestTemplate();
    httpClient = new HttpClient();
  }

  @Test
  public void testGet() throws Exception {

    SipApiRequest sipApiRequest = new SipApiRequest();
    sipApiRequest.setUrl(url);

    apiResponse = httpClient.execute(sipApiRequest);
    Assert.assertNotNull(apiResponse.getResponseBody());
    Assert.assertNotNull(apiResponse.getHttpHeaders());
    //    System.out.println("ApiResponse body : " + gson.toJson(apiResponse.getResponseBody()));
    //    System.out.println("Content Type :" + apiResponse.getContentType());
    //    System.out.println("Response header :" + gson.toJson(apiResponse.getHttpHeaders()));
  }

  @Test
  public void testPost() throws Exception {

    SipApiRequest sipApiRequest = new SipApiRequest();

    List<HeaderParameter> headerParameterList = new ArrayList<>();
    HeaderParameter headerParameter = new HeaderParameter();
    headerParameter.setDatatype("String");
    headerParameter.setKey("Accept");
    headerParameter.setValue("application/json, text/plain, */*");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Authorization");
    headerParameter.setValue("Bearer undefined");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Connection");
    headerParameter.setValue("keep-alive");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Content-Type");
    headerParameter.setValue("application/json");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Content-Length");
    headerParameter.setValue(74);
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Host");
    headerParameter.setValue("saw-rd601.ana.dev.vaste.sncrcorp.net");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Origin");
    headerParameter.setValue("http://saw-rd601.ana.dev.vaste.sncrcorp.net");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Referer");
    headerParameter.setValue("http://saw-rd601.ana.dev.vaste.sncrcorp.net/sip/web/");
    headerParameterList.add(headerParameter);

    headerParameter = new HeaderParameter();
    headerParameter.setKey("Accept-Language");
    headerParameter.setValue("en-US,en;q=0.9,kn;q=0.8");
    headerParameterList.add(headerParameter);

    sipApiRequest.setUrl("http://saw-rd601.ana.dev.vaste.sncrcorp.net/sip/security/doAuthenticate");
    sipApiRequest.setHeaderParameters(headerParameterList);
    sipApiRequest.setHttpMethod(HttpMethod.POST);
    BodyParameters bodyParameters = new BodyParameters();
    bodyParameters.setType("application/json");
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode objectNode = mapper.createObjectNode();
    objectNode.put("masterLoginId", "sawadmin@synchronoss.com");
    objectNode.put("password", "Sawsyncnewuser1!");
    bodyParameters.setContent(objectNode);
    sipApiRequest.setBodyParameters(bodyParameters);

    apiResponse = httpClient.execute(sipApiRequest);
    Assert.assertNotNull(apiResponse.getResponseBody());
    Assert.assertNotNull(apiResponse.getHttpHeaders());
    //    System.out.println("ApiResponse body : " + gson.toJson(apiResponse.getResponseBody()));
    //    System.out.println("Content Type :" + apiResponse.getContentType());
    //    System.out.println("Response header :" + gson.toJson(apiResponse.getHttpHeaders()));
  }
}
