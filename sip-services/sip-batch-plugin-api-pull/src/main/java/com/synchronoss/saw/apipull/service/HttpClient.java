package com.synchronoss.saw.apipull.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.synchronoss.saw.apipull.pojo.ApiResponse;
import com.synchronoss.saw.apipull.pojo.HeaderParameter;
import com.synchronoss.saw.apipull.pojo.QueryParameter;
import com.synchronoss.saw.apipull.pojo.RouteMetadata.HttpMethod;
import com.synchronoss.saw.apipull.pojo.SipApiRequest;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {RestTemplateConfig.class, HttpClientConfig.class})
public class HttpClient {
  //  private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
  RestTemplate restTemplate = new RestTemplate();
  ApiResponse apiResponse;

  public ApiResponse execute(SipApiRequest sipApiRequest) {
    boolean validRequest =
        !StringUtils.isEmpty(sipApiRequest.getUrl())
            && !StringUtils.isEmpty(sipApiRequest.getHttpMethod());
    ApiResponse apiResponse = new ApiResponse();
    if (validRequest) {
      String url = sipApiRequest.getUrl();
      if ((sipApiRequest.getHttpMethod() == HttpMethod.GET)
          || (sipApiRequest.getHttpMethod() == HttpMethod.POST)) {
        boolean isGet = sipApiRequest.getHttpMethod() == HttpMethod.GET ? true : false;

        if (isGet) {
          HttpClientGet get = new HttpClientGet(url);
          if (!CollectionUtils.isEmpty(sipApiRequest.getQueryParameters())) {
            for (QueryParameter qp : sipApiRequest.getQueryParameters()) {
              get.setQueryParam(qp.getKey(), qp.getValue());
            }
          }

          if (!CollectionUtils.isEmpty(sipApiRequest.getHeaderParameters())) {
            for (HeaderParameter hp : sipApiRequest.getHeaderParameters()) {
              get.setHeaderParams(hp.getKey(), hp.getValue());
            }
          }

          apiResponse.setResponseBody(get.execute());
        }
      }
    }
    return apiResponse;
  }

  @Test
  public void test1() {

    String url =
        "https://openweathermap.org/data/2.5/forecast/hourly?zip=94040&appid=b6907d289e10d714a6e88b30761fae22";
    HttpHeaders httpHeaders = restTemplate.headForHeaders(url);
    JsonNode jsonNode = restTemplate.getForObject(url, JsonNode.class);

    HttpClientGet get =
        new HttpClientGet(
            "https://openweathermap.org/data/2.5/forecast/hourly?zip=94040&appid=b6907d289e10d714a6e88b30761fae22");
    get.setApiEndPoint("");
    JsonNode res = get.execute();
    System.out.println("Apache res : " + res);

    //    System.out.println("Headers : " + httpHeaders.toString());
    //    Assert.assertNotNull(jsonNode);
    //    System.out.println(jsonNode);
  }
}
