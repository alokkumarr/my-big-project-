package sncr.bda.conf;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Object contains alias name and loading mode
 *
 */
public class Alias {

    /**
     * Name of alias
     * (Required)
     *
     */
    @SerializedName("aliasName")
    @Expose
    private String aliasName;
    /**
     * Mode of output
     * (Required)
     *
     */
    @SerializedName("mode")
    @Expose
    private Alias.Mode mode = Alias.Mode.fromValue("append");

    /**
     * No args constructor for use in serialization
     *
     */
    public Alias() {
    }

    /**
     *
     * @param aliasName
     * @param mode
     */
    public Alias(String aliasName, Alias.Mode mode) {
        super();
        this.aliasName = aliasName;
        this.mode = mode;
    }

    /**
     * Name of alias
     * (Required)
     *
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * Name of alias
     * (Required)
     *
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * Mode of output
     * (Required)
     *
     */
    public Alias.Mode getMode() {
        return mode;
    }

    /**
     * Mode of output
     * (Required)
     *
     */
    public void setMode(Alias.Mode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("aliasName", aliasName).append("mode", mode).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(aliasName).append(mode).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Alias) == false) {
            return false;
        }
        Alias rhs = ((Alias) other);
        return new EqualsBuilder().append(aliasName, rhs.aliasName).append(mode, rhs.mode).isEquals();
    }

    public enum Mode {

        @SerializedName("replace")
        REPLACE("replace"),
        @SerializedName("append")
        APPEND("append");
        private final String value;
        private final static Map<String, Alias.Mode> CONSTANTS = new HashMap<String, Alias.Mode>();

        static {
            for (Alias.Mode c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Mode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Alias.Mode fromValue(String value) {
            Alias.Mode constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}