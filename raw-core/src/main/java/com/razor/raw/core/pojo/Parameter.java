package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author surendra.rajaneni
 *
 */
public class Parameter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4691125899324043815L;
	protected List<Lookup> lookup;
	protected String lookupValue;
    protected String defaultValue;
    protected String name;
    protected String type;
    protected String value;
    protected String display;
    private boolean optional;
    private int index;
    private long rawReportsId;
    private long rawReportParametersId;
    
   
    /**
     * 
     * @return lookup string
     */
	public String getLookupValue() {
		return lookupValue;
	}
	/**
	 * 
	 * @param lookupValue to set the value of lookupValue
	 */
	public void setLookupValue(String lookupValue) {
		this.lookupValue = lookupValue;
	}

	public long getRawReportsId() {
		return rawReportsId;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public void setRawReportsId(long rawReportsId) {
		this.rawReportsId = rawReportsId;
	}

	public long getRawReportParametersId() {
		return rawReportParametersId;
	}

	public void setRawReportParametersId(long rawReportParametersId) {
		this.rawReportParametersId = rawReportParametersId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLookup(List<Lookup> lookup) {
		this.lookup = lookup;
	}

	

   

	

	/**
     * Gets the value of the lookup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lookup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLookup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Lookup }
     * 
     * 
     */
    public List<Lookup> getLookup() {
        if (lookup == null) {
            lookup = new ArrayList<Lookup>();
        }
        return this.lookup;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the display property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the value of the display property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplay(String value) {
        this.display = value;
    }

}
