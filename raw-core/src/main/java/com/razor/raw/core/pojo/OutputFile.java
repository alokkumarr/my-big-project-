package com.razor.raw.core.pojo;

import java.io.Serializable;

/**
 * 
 * @author surendra.rajaneni
 *
 */

public class OutputFile implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -932976788048702235L;
	protected String outputFormat;
    protected String outputFileName;

    /**
     * Gets the value of the outputFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * Sets the value of the outputFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFormat(String value) {
        this.outputFormat = value;
    }

    /**
     * Gets the value of the outputFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Sets the value of the outputFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFileName(String value) {
        this.outputFileName = value;
    }

}
