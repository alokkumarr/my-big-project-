package com.synchronoss.saw.storage.proxy.model.response;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

public class ProductModuleDocs implements Serializable {

    private static final long serialVersionUID = 7684700123455735559L;

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<JsonObject> getDoc() {
        return doc;
    }

    public void setDoc(List<JsonObject> doc) {
        this.doc = doc;
    }

    private List<JsonObject> doc;
    private boolean valid;



    private String message;

}
