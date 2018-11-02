package com.synchronoss.saw.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ChannelType {
    SFTP("sftp"),
    SCP("scp"),
    JDBC("jdbc");
    private final String value;
    private final static Map<String, ChannelType> CONSTANTS = new HashMap<String, ChannelType>();

    static {
        for (ChannelType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ChannelType(String value) {
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
    public static ChannelType fromValue(String value) {
    	ChannelType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
