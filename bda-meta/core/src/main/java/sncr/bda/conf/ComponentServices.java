package sncr.bda.conf;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

public enum ComponentServices {

    @SerializedName("InputDSMetadata")
    InputDSMetadata("InputDSMetadata"),

    @SerializedName("InMemory")
    InMemory("InMemory"),

    @SerializedName("OutputDSMetadata")
    OutputDSMetadata("OutputDSMetadata"),

    @SerializedName("TransformationMetadata")
    TransformationMetadata("TransformationMetadata"),

    @SerializedName("Spark")
    Spark("Spark"),

    @SerializedName("Project")
    Project("Project"),


    @SerializedName("Persist")
    Persist("Persist"),


    @SerializedName("Sample")
    Sample("Sample");


    private final String value;
    private static final Map<String, ComponentServices> CONSTANTS =
        new HashMap<String, ComponentServices>();

    static {
        for (ComponentServices c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ComponentServices(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String value() {
        return this.value;
    }

    public static ComponentServices fromValue(String value) {
        ComponentServices constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
