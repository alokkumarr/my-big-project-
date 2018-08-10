package com.synchronoss.saw.workbench.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum StorageType {

        ES("ES"),
        DL("DL"),
        RDMS("RDMS"),
        METADATA("METADATA");
        private final String value;
        private final static Map<String, StorageType> CONSTANTS = new HashMap<String, StorageType>();

        static {
            for (StorageType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private StorageType(String value) {
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
        public static StorageType fromValue(String value) {
          StorageType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }