package sncr.bda.conf;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public enum ParserInputFileFormat {
    @SerializedName("csv")
    CSV("csv"),
    @SerializedName("json")
    JSON("json"),
    @SerializedName("parquet")
    PARQUET("parquet");
    private final String value;
    private static final Map<String, ParserInputFileFormat> CONSTANTS = new HashMap<>();

    static {
        for (ParserInputFileFormat c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ParserInputFileFormat(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String value() {
        return this.value;
    }

    public static ParserInputFileFormat fromValue(String value) {
        ParserInputFileFormat constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}
