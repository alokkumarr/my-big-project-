package com.synchronoss.saw.storage.proxy.model.response;

import com.fasterxml.jackson.databind.node.ObjectNode;

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

    private boolean valid;
    private String message;
    private List<ObjectNode> document;

    public List<ObjectNode> getDocument() {
        return document;
    }

    public void setDocument(List<ObjectNode> document) {
        this.document = document;
    }
}
