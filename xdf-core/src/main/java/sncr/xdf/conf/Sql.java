
package sncr.xdf.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * SQL script executor specific properties
 * 
 */
public class Sql {

    /**
     * SQL script sile name name
     * 
     */
    @SerializedName("script")
    @Expose
    private String script;
    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded SQL script
     * 
     */
    @SerializedName("scriptLocation")
    @Expose
    private String scriptLocation;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sql() {
    }

    /**
     * 
     * @param scriptLocation
     * @param script
     */
    public Sql(String script, String scriptLocation) {
        super();
        this.script = script;
        this.scriptLocation = scriptLocation;
    }

    /**
     * SQL script sile name name
     * 
     */
    public String getScript() {
        return script;
    }

    /**
     * SQL script sile name name
     * 
     */
    public void setScript(String script) {
        this.script = script;
    }

    public Sql withScript(String script) {
        this.script = script;
        return this;
    }

    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded SQL script
     * 
     */
    public String getScriptLocation() {
        return scriptLocation;
    }

    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded SQL script
     * 
     */
    public void setScriptLocation(String scriptLocation) {
        this.scriptLocation = scriptLocation;
    }

    public Sql withScriptLocation(String scriptLocation) {
        this.scriptLocation = scriptLocation;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("script", script).append("scriptLocation", scriptLocation).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(script).append(scriptLocation).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Sql) == false) {
            return false;
        }
        Sql rhs = ((Sql) other);
        return new EqualsBuilder().append(script, rhs.script).append(scriptLocation, rhs.scriptLocation).isEquals();
    }

}
