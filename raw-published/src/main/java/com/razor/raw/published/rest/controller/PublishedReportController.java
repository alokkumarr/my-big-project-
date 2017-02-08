package com.razor.raw.published.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razor.raw.core.pojo.PublishedReport;
import com.razor.raw.published.rest.bean.CopyReportReqBean;
import com.razor.raw.published.rest.bean.DeleteReportReqBean;
import com.razor.raw.published.rest.properties.RPProperties;
import com.razor.raw.published.rest.service.PublishedReportService;

/**
 * 
 * @author surendra.rajaneni
 *
 */
@EnableOAuth2Resource
@RestController
public class PublishedReportController {

	private static final Logger logger = LoggerFactory
			.getLogger(PublishedReportController.class);

	@Autowired
	PublishedReportService publishedReportService;

	@Autowired
	RPProperties rpProperties;

	@RequestMapping(value = "/getPublishedReport", method = RequestMethod.GET)
	public List<PublishedReport> getPublishedReports(
			@RequestParam String userId, @RequestParam String tenantId,
			@RequestParam String prodId, @RequestParam String categoryId) {
		logger.debug(this.getClass().getName() + " - showSummary - START");
		return publishedReportService.getPublishedReports(userId, tenantId,
				prodId, categoryId);
	}

	@RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
	public void downloadFile(@RequestParam String fileName,
			HttpServletResponse response) {
		logger.debug("PublishedReportController - getFile - START");
		String filePath = rpProperties.getPublishedReportPath()
				+ File.separator + fileName;
		try {

			response.reset();

			response.setContentType("application/octet-stream");

			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");

			InputStream is = new FileInputStream(filePath);

			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {

			logger.error("PublishedReportController - getFile - Exception - ",
					e);
		}
		logger.debug("PublishedReportController - getFile - END");
	}

	@RequestMapping(value = "/deletePublishedReport", method = RequestMethod.POST)
	public boolean deletePublishedReport(
			@RequestBody DeleteReportReqBean deleteReportReqBean) {
		logger.debug("PublishedReportController - deletePublishedReport - START - rawPublishedReportsId : "
				+ deleteReportReqBean.getRawPublishedReportsId());
		boolean flag = false;
		if (!StringUtils.isEmpty(deleteReportReqBean.getRawPublishedReportsId())) {
			long rawPublishedReportsId1 = Long.parseLong(deleteReportReqBean.getRawPublishedReportsId());
			publishedReportService.deletePublishReport(rawPublishedReportsId1);
			flag = true;
		} else
			logger.debug("PublishedReportController - deletePublishedReport - fail");
		logger.debug("PublishedReportController - deletePublishedReport - END");
		return flag;
	}

	@RequestMapping(value = "/copyPublishedReport", method = RequestMethod.POST)
	public boolean copyPublishedReport(
			@RequestBody CopyReportReqBean copyReportReqBean) {
		logger.debug("PublishedReportController - copyPublishedReport - START");
		boolean flag = publishedReportService.isPublishedReportExist(
				copyReportReqBean.getReportName(),
				copyReportReqBean.getTetantId(), copyReportReqBean.getProdId(),
				copyReportReqBean.getDestCategoryId());
		if (!flag) {
			copyReportReqBean.getPublishedReport().setReportName(
					copyReportReqBean.getReportName());
			copyReportReqBean.getPublishedReport().setReportDescription(
					copyReportReqBean.getReportDescription());
			copyReportReqBean.getPublishedReport().setPublishReportId(0);
			copyReportReqBean.getPublishedReport().setReportCategoryId(
					Long.parseLong(copyReportReqBean.getDestCategoryId()));
			copyReportReqBean.getPublishedReport().setDisplayStatus(true);

			publishedReportService.insertPublishedReports(copyReportReqBean
					.getPublishedReport());
			logger.debug("PublishedReportController - copyPublishedReport - END - success");
			return true;
		}
		logger.debug("PublishedReportController - copyPublishedReport - END - failure");
		return false;

	}
}
