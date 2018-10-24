package com.sncr.saw.security.common.bean.repo.dsk;

import java.io.Serializable;

public class AttributeValues implements Serializable {
    private static final long serialVersionUID = 7684700713245735559L;
    private String attributeName;
    private String Value;


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
