
package com.synchronoss.saw.semantic.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.synchronoss.saw.semantic.model.request.Content;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({

})
public class MetaDataObjects {

    @JsonProperty("contents")
    List<Content> contents = new ArrayList<>();  
    
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    @JsonProperty("contents")
    public List<Content> getContents() {
      return contents;
    }
    @JsonProperty("contents")
    public void setContents(List<Content> contents) {
      this.contents = contents;
    }
    
    

}
