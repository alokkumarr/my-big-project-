
package com.synchronoss.saw.storage.proxy.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "proxy"
})
public class StorageProxyNode {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy")
    private List<StorageProxy> proxy;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy")
    public List<StorageProxy> getProxy() {
        return proxy;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("proxy")
    public void setProxy(List<StorageProxy> proxy) {
        this.proxy = proxy;
    }


}
