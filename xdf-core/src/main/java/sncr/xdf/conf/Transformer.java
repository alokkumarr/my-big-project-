
package sncr.xdf.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Transformer specific properties
 * 
 */
@Generated("org.jsonschema2pojo")
public class Transformer {

    /**
     * SQL script sile name name
     * 
     */
    @SerializedName("script")
    @Expose
    private String script;
    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script
     * 
     */
    @SerializedName("scriptLocation")
    @Expose
    private String scriptLocation;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Transformer() {
    }

    /**
     * 
     * @param scriptLocation
     * @param script
     */
    public Transformer(String script, String scriptLocation) {
        this.script = script;
        this.scriptLocation = scriptLocation;
    }

    /**
     * SQL script sile name name
     * 
     * @return
     *     The script
     */
    public String getScript() {
        return script;
    }

    /**
     * SQL script sile name name
     * 
     * @param script
     *     The script
     */
    public void setScript(String script) {
        this.script = script;
    }

    public Transformer withScript(String script) {
        this.script = script;
        return this;
    }

    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script
     * 
     * @return
     *     The scriptLocation
     */
    public String getScriptLocation() {
        return scriptLocation;
    }

    /**
     * Location containing SQL script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script
     * 
     * @param scriptLocation
     *     The scriptLocation
     */
    public void setScriptLocation(String scriptLocation) {
        this.scriptLocation = scriptLocation;
    }

    public Transformer withScriptLocation(String scriptLocation) {
        this.scriptLocation = scriptLocation;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
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
        if ((other instanceof Transformer) == false) {
            return false;
        }
        Transformer rhs = ((Transformer) other);
        return new EqualsBuilder().append(script, rhs.script).append(scriptLocation, rhs.scriptLocation).isEquals();
    }

}
