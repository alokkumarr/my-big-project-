package com.sncr.saw.security.common.bean.repo.admin;

import java.util.List;

import com.sncr.saw.security.common.bean.Module;


public class ModuleDropDownList {
	private List<Module> Modules;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	
	public List<Module> getModules() {
		return Modules;
	}
	public void setModules(List<Module> modules) {
		Modules = modules;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getValidityMessage() {
		return ValidityMessage;
	}
	public void setValidityMessage(String validityMessage) {
		ValidityMessage = validityMessage;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
	
}
