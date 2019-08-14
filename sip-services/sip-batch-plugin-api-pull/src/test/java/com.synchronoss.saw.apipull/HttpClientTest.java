package com.synchronoss.saw.apipull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import com.synchronoss.saw.apipull.service.HttpClient;
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

  @Before
  public void init() {
    restTemplate = new RestTemplate();
    httpClient = new HttpClient();
  }

  @Test
  public void testGet() {

    SipApiRequest sipApiRequest = new SipApiRequest();
    sipApiRequest.setUrl(url);
    apiResponse = httpClient.execute(sipApiRequest);
    System.out.println("ApiResponse body : " + gson.toJson(apiResponse.getResponseBody()));
    System.out.println("Content Type :" + apiResponse.getContentType());
    System.out.println("Response header :"+ gson.toJson(apiResponse.getHttpHeaders()));

    //    System.out.println("Headers : " + httpHeaders.toString());
    //    Assert.assertNotNull(jsonNode);
    //    System.out.println(jsonNode);
  }
}
