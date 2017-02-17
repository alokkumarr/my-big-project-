package com.razor.raw.generation.rest.service;

import java.util.List;

import com.razor.raw.generation.rest.bean.ReportCategoryBean;
/**
 * 
 * @author AJAY.KUMAR
 * Class is used for handle the report category
 */
public interface ReportCategoryService {
	/**
	 * 
	 * @param tenantId
	 * @param productId
	 * @return list of ReportCategory
	 */
	public List<ReportCategoryBean> getReportCategoryDetails(String tenantId,
			String productId);
}
