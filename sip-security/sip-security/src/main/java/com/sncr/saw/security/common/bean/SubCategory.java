package com.sncr.saw.security.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class SubCategory implements Serializable {

	private static final long serialVersionUID = 6710950219794990634L;

	private boolean autoCreate;
	private String subCategoryDesc;
	private String subCategoryName;
	private List<String> privilege;

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}

	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public List<String> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}
}
