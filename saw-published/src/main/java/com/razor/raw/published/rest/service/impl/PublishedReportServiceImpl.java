package com.razor.raw.published.rest.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razor.raw.core.dao.repository.PublishedReportRepository;
import com.razor.raw.core.pojo.PublishedReport;
import com.razor.raw.published.rest.service.PublishedReportService;

@Service
public class PublishedReportServiceImpl implements PublishedReportService {

	private static final Logger logger = LoggerFactory.getLogger(PublishedReportServiceImpl.class);
	@Autowired
	PublishedReportRepository publishedReportRepository;

	@Override
	public List<PublishedReport> getPublishedReports(String userId,
			String tetantId, String prodId, String categoryId) {
		logger.debug("PublishedReportServiceImpl - getPublishedReports - START");
		return publishedReportRepository.getPublishedReports(userId, tetantId,
				prodId, categoryId);
	}

	@Override
	public boolean isPublishedReportExist(String reportName, String tetantId,
			String prodId, String categoryId) {
		logger.debug("PublishedReportServiceImpl - isPublishedReportExist - START");
		return publishedReportRepository.isPublishedReportExist(reportName,
				tetantId, prodId, categoryId);
	}

	@Override
	public void deletePublishReport(long rawPublishedReportsId) {
		logger.debug("PublishedReportServiceImpl - deletePublishReport - START");
		//TODO: move the file from published location to deleted location 
		publishedReportRepository.deletePublishReport(rawPublishedReportsId);
		logger.debug("PublishedReportServiceImpl - deletePublishReport - END");
	}

	@Override
	public void insertPublishedReports(PublishedReport pubReport) {
		logger.debug("PublishedReportServiceImpl - insertPublishedReports - START");
		publishedReportRepository.insertPublishedReports(pubReport);
		logger.debug("PublishedReportServiceImpl - insertPublishedReports - END");
	}

	/* (non-Javadoc)
	 * @see com.razor.raw.published.rest.service.PublishedReportService#isPublishedReportFileExist(java.lang.String)
	 */
	@Override
	public boolean isPublishedReportFileExist(String filename) {
		logger.debug("PublishedReportServiceImpl - isPublishedReportExist - START");
		return publishedReportRepository.isPublishedReportFileExist(filename);
	}
	
	

}
