package com.razor.raw.core.pojo;

import java.io.Serializable;
import java.util.List;

public class ReportResultItem implements Serializable {

	private static final long serialVersionUID = 1456838519155034274L;
	List<ReportAttribute> reportAttributes = null;
	public List<ReportAttribute> getReportAttributes() {
		return reportAttributes;
	}
	public void setReportAttributes(List<ReportAttribute> reportAttributes) {
		this.reportAttributes = reportAttributes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
