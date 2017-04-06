package com.synchronoss.saw.composite.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contents",
    "links",
    "data"
})

public class RoutingPayload implements Serializable {

   private static final long serialVersionUID = 9199256876060889626L;

	@JsonProperty("contents")
    private Contents contents;

    @JsonProperty("links")
    private Links links;
    
    @JsonProperty("contents")
    public Contents getContents() {
		return contents;
	}

    @JsonProperty("contents")
	public void setContents(Contents contents) {
		this.contents = contents;
	}

    @JsonProperty("links")
    public Links getLinks() {
		return links;
	}

    @JsonProperty("links")
	public void setLinks(Links links) {
		this.links = links;
	}

	@JsonProperty("data")
    @JsonPropertyDescription("Allows ANY array but describes nothing.")
    private List<Object> data = null;

    @JsonProperty("data")
	public List<Object> getData() {
		return data;
	}

    @JsonProperty("data")
	public void setData(List<Object> data) {
		this.data = data;
	}
    
    
 
    
    
}
