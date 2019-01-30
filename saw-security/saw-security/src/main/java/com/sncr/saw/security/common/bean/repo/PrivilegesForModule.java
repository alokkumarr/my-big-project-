package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;
import java.util.HashMap;

public class PrivilegesForModule implements Serializable {
    private static final long serialVersionUID = 7684700543251635559L;

    public HashMap<Long, String> getPriviliges() {
        return priviliges;
    }

    public void setPriviliges(HashMap<Long, String> priviliges) {
        this.priviliges = priviliges;
    }

    private HashMap<Long,String> priviliges = new HashMap<>();
    private boolean valid;

    public boolean isValid() {
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

    private String message;

}
