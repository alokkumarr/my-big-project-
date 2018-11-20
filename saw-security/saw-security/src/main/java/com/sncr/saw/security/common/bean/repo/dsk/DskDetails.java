package com.sncr.saw.security.common.bean.repo.dsk;

import java.io.Serializable;

public class DskDetails implements Serializable {
    private static final long serialVersionUID = 7684700543215735559L;

    private String attributeName;
    private String Value;
    private String created_by;
    private String created_date;

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

}
