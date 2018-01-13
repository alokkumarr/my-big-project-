
package sncr.xdf.conf;

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
     * Script engine: Jexl/Janino
     * 
     */
    @SerializedName("scriptEngine")
    @Expose
    private Transformer.ScriptEngine scriptEngine = Transformer.ScriptEngine.fromValue("janino");
    /**
     * Scripting level: high or low, the parameter is used only for Janino, it will be ignored for Jexl engine
     * 
     */
    @SerializedName("scriptingLevel")
    @Expose
    private Transformer.ScriptingLevel scriptingLevel = Transformer.ScriptingLevel.fromValue("high");
    /**
     * Name of input dataset
     * 
     */
    @SerializedName("inputDataSet")
    @Expose
    private String inputDataSet;
    /**
     * Name of output dataset
     * 
     */
    @SerializedName("outputDataSet")
    @Expose
    private String outputDataSet;
    /**
     * Name of dataset containing rejected records
     * 
     */
    @SerializedName("rejectedDataSet")
    @Expose
    private String rejectedDataSet;
    /**
     * List of reference datasets
     * 
     */
    @SerializedName("referenceData")
    @Expose
    private Set<String> referenceData = new LinkedHashSet<String>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Transformer() {
    }

    /**
     * 
     * @param referenceData
     * @param rejectedDataSet
     * @param scriptLocation
     * @param outputDataSet
     * @param scriptingLevel
     * @param scriptEngine
     * @param script
     * @param inputDataSet
     */
    public Transformer(String script, String scriptLocation, Transformer.ScriptEngine scriptEngine, Transformer.ScriptingLevel scriptingLevel, String inputDataSet, String outputDataSet, String rejectedDataSet, Set<String> referenceData) {
        this.script = script;
        this.scriptLocation = scriptLocation;
        this.scriptEngine = scriptEngine;
        this.scriptingLevel = scriptingLevel;
        this.inputDataSet = inputDataSet;
        this.outputDataSet = outputDataSet;
        this.rejectedDataSet = rejectedDataSet;
        this.referenceData = referenceData;
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
     * Scripting level: high or low, the parameter is used only for Janino, it will be ignored for Jexl engine
     * 
     * @return
     *     The scriptingLevel
     */
    public Transformer.ScriptingLevel getScriptingLevel() {
        return scriptingLevel;
    }

    /**
     * Scripting level: high or low, the parameter is used only for Janino, it will be ignored for Jexl engine
     * 
     * @param scriptingLevel
     *     The scriptingLevel
     */
    public void setScriptingLevel(Transformer.ScriptingLevel scriptingLevel) {
        this.scriptingLevel = scriptingLevel;
    }

    public Transformer withScriptingLevel(Transformer.ScriptingLevel scriptingLevel) {
        this.scriptingLevel = scriptingLevel;
        return this;
    }

    /**
     * Name of input dataset
     * 
     * @return
     *     The inputDataSet
     */
    public String getInputDataSet() {
        return inputDataSet;
    }

    /**
     * Name of input dataset
     * 
     * @param inputDataSet
     *     The inputDataSet
     */
    public void setInputDataSet(String inputDataSet) {
        this.inputDataSet = inputDataSet;
    }

    public Transformer withInputDataSet(String inputDataSet) {
        this.inputDataSet = inputDataSet;
        return this;
    }

    /**
     * Name of output dataset
     * 
     * @return
     *     The outputDataSet
     */
    public String getOutputDataSet() {
        return outputDataSet;
    }

    /**
     * Name of output dataset
     * 
     * @param outputDataSet
     *     The outputDataSet
     */
    public void setOutputDataSet(String outputDataSet) {
        this.outputDataSet = outputDataSet;
    }

    public Transformer withOutputDataSet(String outputDataSet) {
        this.outputDataSet = outputDataSet;
        return this;
    }

    /**
     * Name of dataset containing rejected records
     * 
     * @return
     *     The rejectedDataSet
     */
    public String getRejectedDataSet() {
        return rejectedDataSet;
    }

    /**
     * Name of dataset containing rejected records
     * 
     * @param rejectedDataSet
     *     The rejectedDataSet
     */
    public void setRejectedDataSet(String rejectedDataSet) {
        this.rejectedDataSet = rejectedDataSet;
    }

    public Transformer withRejectedDataSet(String rejectedDataSet) {
        this.rejectedDataSet = rejectedDataSet;
        return this;
    }

    /**
     * List of reference datasets
     * 
     * @return
     *     The referenceData
     */
    public Set<String> getReferenceData() {
        return referenceData;
    }

    /**
     * List of reference datasets
     * 
     * @param referenceData
     *     The referenceData
     */
    public void setReferenceData(Set<String> referenceData) {
        this.referenceData = referenceData;
    }

    public Transformer withReferenceData(Set<String> referenceData) {
        this.referenceData = referenceData;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(script).append(scriptLocation).append(scriptEngine).append(scriptingLevel).append(inputDataSet).append(outputDataSet).append(rejectedDataSet).append(referenceData).toHashCode();
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
        return new EqualsBuilder().append(script, rhs.script).append(scriptLocation, rhs.scriptLocation).append(scriptEngine, rhs.scriptEngine).append(scriptingLevel, rhs.scriptingLevel).append(inputDataSet, rhs.inputDataSet).append(outputDataSet, rhs.outputDataSet).append(rejectedDataSet, rhs.rejectedDataSet).append(referenceData, rhs.referenceData).isEquals();
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

    @Generated("org.jsonschema2pojo")
    public static enum ScriptingLevel {

        @SerializedName("high")
        HIGH("high"),
        @SerializedName("low")
        LOW("low");
        private final String value;
        private static Map<String, Transformer.ScriptingLevel> constants = new HashMap<String, Transformer.ScriptingLevel>();

        static {
            for (Transformer.ScriptingLevel c: values()) {
                constants.put(c.value, c);
            }
        }

        private ScriptingLevel(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Transformer.ScriptingLevel fromValue(String value) {
            Transformer.ScriptingLevel constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
