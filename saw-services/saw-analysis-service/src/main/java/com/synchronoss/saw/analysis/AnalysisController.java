package com.synchronoss.saw.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analysis entity controller.
 */
@RestController
public class AnalysisController {
  @RequestMapping("/health")
  @ResponseBody
  public String health() {
    return "OK";
  }

  /**
   * Handle creation of analysis entity.
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  @ResponseBody
  public String create(@RequestBody String json) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree("{}");
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
