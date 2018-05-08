
package sncr.bda.conf;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
     * script sile name name
     * 
     */
    @SerializedName("script")
    @Expose
    private String script;
    /**
     * Location containing Jexl/Janino script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script or Jexl script
     * 
     */
    @SerializedName("scriptLocation")
    @Expose
    private String scriptLocation;
    /**
     * Standard beginning of script. The preamble pre-populate output record with input values if there is match between fields
     * 
     */
    @SerializedName("scriptPreamble")
    @Expose
    private String scriptPreamble;
    /**
     * Script engine: Jexl/Janino
     * 
     */
    @SerializedName("scriptEngine")
    @Expose
    private Transformer.ScriptEngine scriptEngine = Transformer.ScriptEngine.fromValue("janino");
    /**
     * List of additional import statements for Janino script.s
     * 
     */
    @SerializedName("additionalImports")
    @Expose
    private Set<String> additionalImports = new LinkedHashSet<String>();
    /**
     * If # of failed records exceeds this threshold, processing will be cancelled
     * 
     */
    @SerializedName("threshold")
    @Expose
    private Integer threshold = 0;
    /**
     * List of output fields: field name, field type.
     * 
     */
    @SerializedName("outputSchema")
    @Expose
    private Set<OutputSchema> outputSchema = new LinkedHashSet<OutputSchema>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Transformer() {
    }

    /**
     * 
     * @param scriptLocation
     * @param outputSchema
     * @param scriptPreamble
     * @param scriptEngine
     * @param threshold
     * @param additionalImports
     * @param script
     */
    public Transformer(String script, String scriptLocation, String scriptPreamble, Transformer.ScriptEngine scriptEngine, Set<String> additionalImports, Integer threshold, Set<OutputSchema> outputSchema) {
        this.script = script;
        this.scriptLocation = scriptLocation;
        this.scriptPreamble = scriptPreamble;
        this.scriptEngine = scriptEngine;
        this.additionalImports = additionalImports;
        this.threshold = threshold;
        this.outputSchema = outputSchema;
    }

    /**
     * script sile name name
     * 
     * @return
     *     The script
     */
    public String getScript() {
        return script;
    }

    /**
     * script sile name name
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
     * Location containing Jexl/Janino script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script or Jexl script
     * 
     * @return
     *     The scriptLocation
     */
    public String getScriptLocation() {
        return scriptLocation;
    }

    /**
     * Location containing Jexl/Janino script, special value: 'inline' means scripts field itself is base64 encoded Java (Janino-compatible) script or Jexl script
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

    /**
     * Standard beginning of script. The preamble pre-populate output record with input values if there is match between fields
     * 
     * @return
     *     The scriptPreamble
     */
    public String getScriptPreamble() {
        return scriptPreamble;
    }

    /**
     * Standard beginning of script. The preamble pre-populate output record with input values if there is match between fields
     * 
     * @param scriptPreamble
     *     The scriptPreamble
     */
    public void setScriptPreamble(String scriptPreamble) {
        this.scriptPreamble = scriptPreamble;
    }

    public Transformer withScriptPreamble(String scriptPreamble) {
        this.scriptPreamble = scriptPreamble;
        return this;
    }

    /**
     * Script engine: Jexl/Janino
     * 
     * @return
     *     The scriptEngine
     */
    public Transformer.ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    /**
     * Script engine: Jexl/Janino
     * 
     * @param scriptEngine
     *     The scriptEngine
     */
    public void setScriptEngine(Transformer.ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public Transformer withScriptEngine(Transformer.ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    /**
     * List of additional import statements for Janino script.s
     * 
     * @return
     *     The additionalImports
     */
    public Set<String> getAdditionalImports() {
        return additionalImports;
    }

    /**
     * List of additional import statements for Janino script.s
     * 
     * @param additionalImports
     *     The additionalImports
     */
    public void setAdditionalImports(Set<String> additionalImports) {
        this.additionalImports = additionalImports;
    }

    public Transformer withAdditionalImports(Set<String> additionalImports) {
        this.additionalImports = additionalImports;
        return this;
    }

    /**
     * If # of failed records exceeds this threshold, processing will be cancelled
     * 
     * @return
     *     The threshold
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * If # of failed records exceeds this threshold, processing will be cancelled
     * 
     * @param threshold
     *     The threshold
     */
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Transformer withThreshold(Integer threshold) {
        this.threshold = threshold;
        return this;
    }

    /**
     * List of output fields: field name, field type.
     * 
     * @return
     *     The outputSchema
     */
    public Set<OutputSchema> getOutputSchema() {
        return outputSchema;
    }

    /**
     * List of output fields: field name, field type.
     * 
     * @param outputSchema
     *     The outputSchema
     */
    public void setOutputSchema(Set<OutputSchema> outputSchema) {
        this.outputSchema = outputSchema;
    }

    public Transformer withOutputSchema(Set<OutputSchema> outputSchema) {
        this.outputSchema = outputSchema;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(script).append(scriptLocation).append(scriptPreamble).append(scriptEngine).append(additionalImports).append(threshold).append(outputSchema).toHashCode();
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
        return new EqualsBuilder().append(script, rhs.script).append(scriptLocation, rhs.scriptLocation).append(scriptPreamble, rhs.scriptPreamble).append(scriptEngine, rhs.scriptEngine).append(additionalImports, rhs.additionalImports).append(threshold, rhs.threshold).append(outputSchema, rhs.outputSchema).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum ScriptEngine {

        @SerializedName("jexl")
        JEXL("jexl"),
        @SerializedName("janino")
        JANINO("janino");
        private final String value;
        private static Map<String, Transformer.ScriptEngine> constants = new HashMap<String, Transformer.ScriptEngine>();

        static {
            for (Transformer.ScriptEngine c: values()) {
                constants.put(c.value, c);
            }
        }

        private ScriptEngine(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Transformer.ScriptEngine fromValue(String value) {
            Transformer.ScriptEngine constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
