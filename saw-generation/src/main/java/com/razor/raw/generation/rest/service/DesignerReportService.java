package com.razor.raw.generation.rest.service;

import java.util.List;

import com.razor.raw.core.pojo.DrillView;
import com.razor.raw.generation.rest.bean.DesignerReportReqBean;
import com.razor.raw.generation.rest.bean.DesignerSaveReportBean;
import com.razor.raw.generation.rest.bean.ReportViewBean;
import com.razor.raw.generation.rest.bean.Valid;
import com.razor.raw.utility.beans.DeleteReportReq;

public interface DesignerReportService {

	ReportViewBean getDesignerReport(DesignerReportReqBean reportViewReqBean);

	List<DrillView> getViewList(String tenantId,  String productID);

	Valid insertDesignerReport(DesignerSaveReportBean designerSaveReportBean);

	Valid updateDesignerReport(DesignerSaveReportBean designerSaveReportBean);

	Valid inactivateDesignerReport(DeleteReportReq deleteReportReq);
}
