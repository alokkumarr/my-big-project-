package com.sncr.saw.security.common.bean.external.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class CategoryList implements Serializable {

	private static final long serialVersionUID = -6682525829922872306L;

	private Boolean valid;
	private String message;
	private List<CategoryDetails> categories;

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<CategoryDetails> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDetails> categories) {
		this.categories = categories;
	}
}
