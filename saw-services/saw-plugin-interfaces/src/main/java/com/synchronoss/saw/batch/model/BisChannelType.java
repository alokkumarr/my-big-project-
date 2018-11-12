package com.synchronoss.saw.batch.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum BisChannelType {
    SFTP("sftp"),
    SCP("scp"),
    JDBC("jdbc");
    private final String value;
    private final static Map<String, BisChannelType> CONSTANTS = new HashMap<String, BisChannelType>();

    static {
        for (BisChannelType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private BisChannelType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static BisChannelType fromValue(String value) {
    	BisChannelType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
