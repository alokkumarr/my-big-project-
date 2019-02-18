package com.synchronoss.saw.storage.proxy.model.response;

import java.io.Serializable;

public class ProductModuleDocs implements Serializable {

    private static final long serialVersionUID = 7684700123455735559L;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

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

    private String doc;
    private boolean valid;
    private String message;

}
