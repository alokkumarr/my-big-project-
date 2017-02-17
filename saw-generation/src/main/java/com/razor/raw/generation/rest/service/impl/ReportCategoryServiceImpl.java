package com.razor.raw.generation.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razor.raw.core.dao.repository.ReportCategoryRepository;
import com.razor.raw.core.pojo.ReportCategory;
import com.razor.raw.generation.rest.bean.ReportCategoryBean;
import com.razor.raw.generation.rest.service.ReportCategoryService;

@Service
public class ReportCategoryServiceImpl implements ReportCategoryService{
	private static final Logger logger = LoggerFactory
			.getLogger(ReportCategoryServiceImpl.class);
	
	@Autowired
	ReportCategoryRepository reportCategoryRepository;	
	
	/**
	 * get the list of report category acording tenantId and productId
	 * 
	 * @param 
	 * @param
	 * 
	 * @return
	 * 
	 * 
	 */
	@Override
	public List<ReportCategoryBean> getReportCategoryDetails(String tenantId,
			String productId) {
		logger.debug(this.getClass().getName() + " - getReportCategoryDetails - START");
		List<ReportCategory> reportCateforyList = reportCategoryRepository.getReportCategoryDetails(tenantId, productId);
		List<ReportCategoryBean> reportCatBeanList = new ArrayList<ReportCategoryBean>();
		ReportCategoryBean reportBean = null;
		if(reportCateforyList != null && reportCateforyList.size() > 0)
		{
			for (ReportCategory reportCategory : reportCateforyList) {
				reportBean = new ReportCategoryBean();
				reportBean.setId(reportCategory.getReportCategoryId());
				reportBean.setReportCategoryName(reportCategory
						.getReportCategoryName());
				reportBean.setReportSuperCategoryId(reportCategory
						.getReportSuperCategoryId());
				reportCatBeanList.add(reportBean);
			}
		}
		logger.debug(this.getClass().getName() + " - getReportCategoryDetails - END");
		return reportCatBeanList;
	}
	
}
