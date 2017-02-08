package com.razor.raw.generation.rest.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.core.pojo.Report;
import com.razor.raw.generation.rest.service.ReportService;

@EnableOAuth2Resource
@RestController
@RequestMapping (value = "/getListOfReportCategories")
public class ReportCategoryBean {
	private long id;
	private String reportCategoryName = null;
	private long reportSuperCategoryId = 0;
	
	@Autowired
	public ReportService reportService; 
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getReportCategoryName() {
		return reportCategoryName;
	}
	public void setReportCategoryName(String reportCategoryName) {
		this.reportCategoryName = reportCategoryName;
	}
	public long getReportSuperCategoryId() {
		return reportSuperCategoryId;
	}
	public void setReportSuperCategoryId(long reportSuperCategoryId) {
		this.reportSuperCategoryId = reportSuperCategoryId;
	}
	/**
	 * 
	 * @param categoryID
	 * @param tenantId
	 * @return list of Report
	 */
	@RequestMapping(value = "{categoryID}/getReportList", method = RequestMethod.GET)
	public List<Report> getReportDetails(@PathVariable("categoryID") long categoryID, @RequestParam("tenantId") String tenantId, @RequestParam("userID") String userID, @RequestParam("productID") String productID){
		List<Report> reportList = reportService.getReportByCategoryId(categoryID, tenantId,userID,productID);
		return reportList;
	}
	
}
