/**
 * 
 */
package com.razor.raw.re.report.interfaces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.razor.raw.re.report.generate.ExportExcelBean;


/**
 * 
 * @author surendra.rajaneni
 *
 */
public interface IFileExporter {

	StringBuffer rowMaker(String values, StringBuffer rowBuffer);
	StringBuffer appendHeader(String[] rowHeader);
	File generateFile(ExportExcelBean exportExcelBean, String fileName, List<StringBuffer> recordRowList) throws IOException;
}
