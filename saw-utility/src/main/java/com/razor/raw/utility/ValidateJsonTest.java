package com.razor.raw.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.razor.raw.core.pojo.Column;
import com.razor.raw.core.pojo.Columns;
import com.razor.raw.core.pojo.Parameter;
import com.razor.raw.core.pojo.Parameters;
import com.razor.raw.core.pojo.Report;



public class ValidateJsonTest {
	/*public static void main(String[] args) {
		JsonValidatorUtility jsonUtil =  new JsonValidatorUtility();
		String jsonFile = "D:\\TestFolder\\Report.json";
		Report report = jsonUtil.isValidReport(jsonFile);
		
		if(report != null){
			JsonUtilService service = new JsonUtilService();
			service.insertReport(report);
			
		}*/
/*		  Gson gson = new Gson();
	        Report report = new Report();
	        report.setReportId(1);
	        report.setProductViewsId(1);
	        report.setReportCategoryId(1);
	        report.setTenantId("PGI");
	        report.setReportName("Report1");
	        report.setReportDescription("Report1 Description");
	        report.setReportQuery("Select * form ABC tABLE");
	        report.setReportQueryType("String");
	        report.setDisplayStatus("Y");
	        report.setDesignerQuery(false);
	        report.setCreatedUser("Ajay");
	        report.setModifiedUser("Ajay");
	        report.setCreatedDate("12-08-2015");
	        report.setModifiedDate("08-08-2015");
	        Parameters params = new Parameters();
	        Parameter param = new Parameter();
	        param.setLookup(null);
	        param.setDefaultValue("String");
	        param.setName("PARAM_NAME");
	        param.setType("String");
	        param.setValue("Ajay");
	        param.setDisplay("Y");
	        param.setOptional(false);
	        param.setRawReportsId(1);
	        param.setRawReportParametersId(1);
	        List<Parameter> paramList = new ArrayList<Parameter>();
	        paramList.add(param);
	        params.setParameter(paramList);
	        Column col = new Column();
	        col.setAlign("left");
	        col.setLabel("label");
	        col.setName("name");
	        col.setPattern("dd-mm-yy");
	        List<Column> colList = new ArrayList<Column>();
	        colList.add(col);
	        Columns columns = new Columns();
	        columns.setColumn(colList);
	        report.setColumns(columns);
	        report.setParametarised(true);
	        report.setScheduled(true);
	        System.out.println("Hello");
	        System.out.println(gson.toJson(report));*/

}
