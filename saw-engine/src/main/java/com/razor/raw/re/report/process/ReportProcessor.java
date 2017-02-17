/**
 * 
 */
package com.razor.raw.re.report.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.razor.raw.core.dao.repository.DataSourceRepository;
import com.razor.raw.core.dao.repository.ReportExecutionLogRepository;
import com.razor.raw.core.dao.repository.ReportRepository;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Datasource;
import com.razor.raw.core.pojo.OutputFile;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Report;
import com.razor.raw.core.req.ReportReq;
import com.razor.raw.re.report.generate.ReportFileGenerator;


/**
 * 
 * @author surendra.rajaneni
 *
 */
@Component
public class ReportProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ReportProcessor.class);
	
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	DataSourceRepository dataSourceRepository;
	@Autowired
	ReportFileGenerator reportFileGenerator;
	@Autowired
	ReportExecutionLogRepository reportExecutionLogRepository;

	public void receiveMessage(ReportReq reportReq) {
		logger.debug("User activity started here:" + this.getClass().getName()+ " receiveMessage method");
		String reportName = null;
		String description = null;
		String reportQuery = null;
		boolean copyToFTP = false;
		boolean attachReportToMail = false;
		String type = null;		long reportCategoryId;
		long reportId;
		String categoryGroup = null;		String publishedBy = null;
		List<ReportParameterViewImpl> parameters = new ArrayList<ReportParameterViewImpl>();
		List<ReportColumnImpl> columns = new ArrayList<ReportColumnImpl>();
		OutputFileDetails outputFileDetails = null;
		ReportDefinationImpl reportDefinationImpl = null;
		String emailId = null;
		try {
			Report report = reportReq.getReport();
			reportName = report.getReportName();
			logger.debug(this.getClass().getName()+ " reportName : "+reportName);
			description = report.getReportDescription();
			emailId = reportReq.getEmailId();
			logger.debug(this.getClass().getName()+ " reportName : "+reportName);
			if (description == null) {
				description = reportName;
			}
			reportQuery = report.getReportQuery();
			type = report.getReportQueryType();
			publishedBy = reportReq.getPublishedBy();
			
			reportCategoryId = report.getReportCategoryId();
			reportId = report.getReportId();
			logger.debug(this.getClass().getName()+ " reportId : "+reportId);
			copyToFTP = reportReq.isCopyToFTP();
			attachReportToMail = reportReq.isAttachReportToMail();
			//categoryGroup = report.getCategoryGroup();
			if(report.getParameters() != null)
			{
			List<Parameter> rParameters = report.getParameters().getParameter();
			int index = 1;
			
			for (Parameter parameter : rParameters) {
				ReportParameterViewImpl reportParameterView = new ReportParameterViewImpl(
						index++);
				reportParameterView.setParameterName(parameter.getName());
				reportParameterView.setParameterType(parameter.getType());
				reportParameterView
						.setDefaultValue(parameter.getDefaultValue());
				reportParameterView.setValue(parameter.getValue());
				parameters.add(reportParameterView);
			}
			}
			List<Column> rColumns = report.getColumns().getColumn();
			for (Column column : rColumns) {
				ReportColumnImpl reportColumn = new ReportColumnImpl();
				BeanUtils.copyProperties(reportColumn, column);
				columns.add(reportColumn);
			}

			Datasource datasource = dataSourceRepository.getDataSourec(reportReq.getTenantId(), reportReq.getProductId()); 
			logger.debug(this.getClass().getName()+ " got the datasource");
			OutputFile outputFile = (OutputFile) reportReq.getOutputFile();
			outputFileDetails = new OutputFileDetails(outputFile.getOutputFileName(), outputFile.getOutputFormat());
			reportDefinationImpl = new ReportDefinationImpl(datasource,reportName, description, reportQuery, parameters, columns,
					outputFileDetails, type, publishedBy, reportCategoryId, reportId, categoryGroup,
					emailId, copyToFTP, attachReportToMail, reportReq.getTenantId(), reportReq.getProductId(), report);
			
			//ReportFileGenerator reportFileGenerator = new ReportFileGenerator();
			reportExecutionLogRepository.insertReportExecutionLog(reportReq.getReport().getReportId(), "Invoke File Generation process : "+reportReq.getName(), reportReq.getPublishedBy());
			reportFileGenerator.fileGenerator(reportDefinationImpl);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occured at " + this.getClass().getName(),e);
		}
		logger.debug("User activity ends here:" + this.getClass().getName()+ " receiveMessage method");
	}

}
