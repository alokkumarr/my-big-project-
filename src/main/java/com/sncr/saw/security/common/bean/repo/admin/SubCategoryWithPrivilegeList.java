/**
 * 
 */
package com.sncr.saw.security.common.bean.repo.admin;

import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryWithPrivilegeDetails;

import java.util.List;

/**
 * @author pman0003
 *
 */
public class SubCategoryWithPrivilegeList {
	
	private List<SubCategoryWithPrivilegeDetails> subCategories;
	private String error;
	private String ValidityMessage;
	private Boolean valid;
	/**
	 * @return the subCategories
	 */
	public List<SubCategoryWithPrivilegeDetails> getSubCategories() {
		return subCategories;
	}
	/**
	 * @param subCategories the subCategories to set
	 */
	public void setSubCategories(List<SubCategoryWithPrivilegeDetails> subCategories) {
		this.subCategories = subCategories;
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the validityMessage
	 */
	public String getValidityMessage() {
		return ValidityMessage;
	}
	/**
	 * @param validityMessage the validityMessage to set
	 */
	public void setValidityMessage(String validityMessage) {
		ValidityMessage = validityMessage;
	}
	/**
	 * @return the valid
	 */
	public Boolean getValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(Boolean valid) {
		this.valid = valid;
	}	
		
}
