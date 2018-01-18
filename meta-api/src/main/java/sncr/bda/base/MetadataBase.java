package sncr.bda.base;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.PathFilter;
import sncr.bda.core.file.HFileOperations;


/**
 * Created by srya0001 on 10/16/2017.
 */
public abstract class MetadataBase {

    public static final String PROJECTS = "projects";
    public static final String DS_DL_SOURCES = "sources";
    public static final String DS_DL_CATALOGS = "catalogs";
    public static final String DS_DL_SETS = "sets";
    public static final String DS_DL_SET  = "set";
    public static final String DS_DL_OBS_SETS = "_obs_sets";

    public static final String DS_MD_CATEGORY = "category";
    public static final String DS_MD_SUBCATEGORY = "subcategory";


    public static final String PREDEF_DATA_SOURCE = "fs";
    public static final String PREDEF_TEMP_DIR = "temp";
    public static final String PREDEF_DATA_DIR = "data";
    public static final String PREDEF_DL_DIR = "dl";
    public static final String PREDEF_SYSTEM_DIR = "system";
    public static final String PREDEF_RAW_DIR = "raw";
    public static final String PREDEF_CTX_DIR = "ctx";
    public static final String DEFAULT_CATALOG = "data";


    public static String XDF_DATA_ROOT = "XDF_DATA_ROOT";
    public static String FILE_DESCRIPTOR = ".bda_meta";
    public static String DEFAULT_DATA_SOURCE = "fs";

    protected final FileSystem fs;
    protected String dlRoot;


    public final static PathFilter DATA_DIR_FILTER = file ->
            ((!file.getName().startsWith(FILE_DESCRIPTOR)));

    public final static PathFilter METADATA_DIR_FILTER = file ->
            ((file.getName().startsWith(FILE_DESCRIPTOR)));


    public MetadataBase(String fsr) throws Exception {
        HFileOperations.init();
        setXDFDataRoot(fsr);
        this.fs = HFileOperations.fs;
    }

    /**
     * The method sets XDF data Root from the following sources (ordered in the list)
     * - parameter
     * - system settings
     * - environment variable
     * @param a_xdfDataRoot
     */
    public String setXDFDataRoot(String a_xdfDataRoot) throws Exception {
        dlRoot = null;
        if (a_xdfDataRoot != null && !a_xdfDataRoot.isEmpty() ){
            dlRoot = a_xdfDataRoot;
        }
        else
            throw new IllegalArgumentException("XDF data root parameter must be provided");

/*
        else{
            String xdfDataRootEnv = System.getenv(XDF_DATA_ROOT);
            if (xdfDataRootEnv != null && !xdfDataRootEnv.isEmpty()) {
                dlRoot = xdfDataRootEnv;
            }
        }
        if (!HFileOperations.exists(dlRoot)){
            throw new XDFException(XDFException.ErrorCodes.NonExistingDataRoot, dlRoot);
        }
*/
        return dlRoot;
    }


    public String getRoot() {
        return dlRoot;
    }


}
