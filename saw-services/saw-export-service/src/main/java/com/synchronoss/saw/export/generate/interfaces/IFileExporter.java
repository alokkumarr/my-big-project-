/**
 * 
 */
package com.synchronoss.saw.export.generate.interfaces;

import com.synchronoss.saw.export.generate.ExportBean;

import java.io.File;
import java.io.IOException;
import java.util.List;


public interface IFileExporter {

	StringBuffer rowMaker(String values, StringBuffer rowBuffer);
	StringBuffer appendHeader(String[] rowHeader);
	File generateFile(ExportBean exportBean, List<Object> recordRowList) throws IOException;
}
