package sncr.xdf.services;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srya0001 on 10/26/2017.
 */
public class DLMetadata extends MetadataBase {

    private static final Logger logger = Logger.getLogger(DLMetadata.class);

    public DLMetadata(String fsr) throws Exception {
        super(fsr);
    }

    public ArrayList<String> getListOfProjects() throws Exception{
        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(dlRoot + Path.SEPARATOR + "*");
        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            if (f.isDirectory() && !f.getPath().getName().equals(".bda_meta")) {
                String prj =f.getPath().getName();
                StringBuilder sb = new StringBuilder();
                sb.append("{")
                        .append("\"prj\":\"")
                        .append(prj)
                        .append("\"}");

                list.add(sb.toString());
            }
        }
        return list;
    }

    public ArrayList<String> getListOfContainers(String rqProject) throws Exception{

        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        strPath.append("*");

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            if (f.isDirectory() && !f.getPath().getName().equals(".bda_meta")) {
                // Must validate container names to exclude garbage
                String project = f.getPath().getParent().getName();
                String container = f.getPath().getName();
                // Build final object definition
                StringBuilder sb = new StringBuilder();
                sb.append("{").append("\"prj\":\"").append(project).append("\",\"src\":\"").append(container).append("\"").append("}");

                list.add(sb.toString());
            }
        }
        return list;
    }

    public ArrayList<String> getListOfLocations(String rqProject, String rqContainer) throws Exception{
        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        if (rqContainer != null) {
            strPath.append(rqContainer).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        strPath.append("*");

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            if (f.isDirectory() && !f.getPath().getName().equals(".bda_meta")) {
                // Must validate container names to exclude garbage
                String project = f.getPath().getParent().getParent().getName();
                String container = f.getPath().getParent().getName();
                String location = f.getPath().getName();
                // Build final object definition
                StringBuilder sb = new StringBuilder();
                sb.append("{").append("\"prj\":\"").append(project).append("\",\"src\":\"").append(container).append("\",\"cat\":\"").append(location).append("\"}");

                list.add(sb.toString());
            }
        }
        return list;
    }

    public ArrayList<String> getListOfObjects(String rqProject, String rqContainer, String rqLocation) throws Exception {
        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject ).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        if (rqContainer != null) {
            strPath.append(rqContainer).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        if (rqLocation != null) {
            strPath.append(rqLocation).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append("*").append(Path.SEPARATOR_CHAR);
        }
        strPath.append("*").append(Path.SEPARATOR_CHAR).append(".bda_meta");

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {

            String object = f.getPath().getParent().getName();
            String location = f.getPath().getParent().getParent().getName();
            String container = f.getPath().getParent().getParent().getParent().getName();
            String project = f.getPath().getParent().getParent().getParent().getParent().getName();

            //Path meta = new Path(f.getPath() + Path.SEPARATOR + ".bda_meta");
            byte[] content = new byte[(int)f.getLen()];
            FSDataInputStream file = fs.open(f.getPath());
            file.readFully(0, content);
            file.close();

            // Build final object definition
            StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append("\"prj\":\"")
                    .append(project)
                    .append("\",\"obj\":")
                    .append('"')
                    .append(object)
                    .append('"')
                    .append(",\"cat\":")
                    .append('"')
                    .append(location)
                    .append('"')
                    .append(",\"src\":")
                    .append('"')
                    .append(container)
                    .append('"')
                    .append(",\"meta\":")
                    .append(new String(content))
                    .append("}");

            list.add(sb.toString());
        }

        // Simulate OOM
        //long[][] ary = new long[Integer.MAX_VALUE][Integer.MAX_VALUE];
        return list;
    }

    public void createDataSet(String project, String source, String catalog, String setName) throws Exception {
        // Have to check if project Exists
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        if(!fs.exists(projectPath)){
            throw new Exception("Project " + project + " doesn't Exists.");
        }

        // Set default data source and catalog
        String src = (source != null)? source : PREDEF_DATA_SOURCE;
        String ctlg = (catalog != null)? catalog: PREDEF_DATA_DIR;

        // Create data set dierctory
        Path setPath = new Path(projectPath + Path.SEPARATOR
                + src + Path.SEPARATOR
                + ctlg + Path.SEPARATOR + setName);
        fs.mkdirs(setPath);

        // Create data directory
        Path dataPath = new Path(setPath + Path.SEPARATOR + PREDEF_DATA_DIR);
        fs.mkdirs(dataPath);

        // Initialize meta data file
        Path metaPath = new Path(setPath + Path.SEPARATOR + FILE_DESCRIPTOR);
        FSDataOutputStream f = fs.create(metaPath);
        f.writeBytes("{\"field\":\"value\"}");
        f.close();
    }

    public ArrayList<String> getListOfDataSources(String rqProject) throws Exception{

        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject).append(Path.SEPARATOR_CHAR).append( PREDEF_DL_DIR + Path.SEPARATOR + "*");
        } else {
            throw new Exception("Project is not specified");
        }

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            if (f.isDirectory() && !f.getPath().getName().equals(FILE_DESCRIPTOR)) {
                // Must validate container names to exclude garbage
                String project = f.getPath().getParent().getParent().getName();
                String container = f.getPath().getName();
                // Build final object definition
                StringBuilder sb = new StringBuilder();
                sb.append("{").append("\"prj\":\"").append(project).append("\",\"src\":\"").append(container).append("\"").append("}");

                list.add(sb.toString());
            }
        }
        return list;
    }

    public ArrayList<String> getListOfCatalogs(String rqProject, String rqSource) throws Exception{
        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject).append(Path.SEPARATOR_CHAR).append(PREDEF_DL_DIR).append(Path.SEPARATOR_CHAR);
        } else {
            throw new Exception("Project is not specified");
        }
        if (rqSource == null || rqSource.isEmpty()) {
            strPath.append(DEFAULT_DATA_SOURCE).append(Path.SEPARATOR_CHAR);

        } else {
            strPath.append(rqSource).append(Path.SEPARATOR_CHAR);
        }
        strPath.append("*");

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            if (f.isDirectory() && !f.getPath().getName().equals(FILE_DESCRIPTOR)) {
                // Must validate container names to exclude garbage
                String project = f.getPath().getParent().getParent().getParent().getName();
                String container = f.getPath().getParent().getName();
                String location = f.getPath().getName();
                // Build final object definition
                StringBuilder sb = new StringBuilder();
                sb.append("{").append("\"prj\":\"").append(project).append("\",\"src\":\"").append(container).append("\",\"cat\":\"").append(location).append("\"}");

                list.add(sb.toString());
            }
        }
        return list;
    }

    public ArrayList<String> getListOfSets(String rqProject, String rqSource, String rqCatalog) throws Exception {
        StringBuilder strPath = new StringBuilder(dlRoot + Path.SEPARATOR);
        if (rqProject != null) {
            strPath.append(rqProject).append(Path.SEPARATOR_CHAR).append(PREDEF_DL_DIR).append(Path.SEPARATOR_CHAR);
        } else {
            throw new Exception("Project is not specified");
        }
        if (rqSource != null && !rqSource.isEmpty()) {
            strPath.append(rqSource).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append(DEFAULT_DATA_SOURCE).append(Path.SEPARATOR_CHAR);
        }
        if (rqCatalog != null && !rqCatalog.isEmpty()) {
            strPath.append(rqCatalog).append(Path.SEPARATOR_CHAR);
        } else {
            strPath.append(DEFAULT_CATALOG).append(Path.SEPARATOR_CHAR);
        }
        strPath.append("*").append(Path.SEPARATOR_CHAR).append(FILE_DESCRIPTOR);

        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(strPath.toString());

        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {

            String object = f.getPath().getParent().getName();
            String location = f.getPath().getParent().getParent().getName();
            String container = f.getPath().getParent().getParent().getParent().getName();
            String project = f.getPath().getParent().getParent().getParent().getParent().getParent().getName();

            byte[] content = new byte[(int)f.getLen()];
            FSDataInputStream file = fs.open(f.getPath());
            file.readFully(0, content);
            file.close();

            // Build final object definition
            StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append("\"prj\":\"")
                    .append(project)
                    .append("\",\"set\":")
                    .append('"')
                    .append(object)
                    .append('"')
                    .append(",\"cat\":")
                    .append('"')
                    .append(location)
                    .append('"')
                    .append(",\"src\":")
                    .append('"')
                    .append(container)
                    .append('"')
                    .append(",\"meta\":")
                    .append(new String(content))
                    .append("}");

            list.add(sb.toString());
        }

        // Simulate OOM
        //long[][] ary = new long[Integer.MAX_VALUE][Integer.MAX_VALUE];
        return list;
    }

    public ArrayList<String> getListOfStagedFiles(String rqProject, String subDir) throws Exception {

        if (rqProject == null) {
            throw new Exception("Project is not specified");
        }

        Path stagingRoot = new Path(dlRoot + Path.SEPARATOR + rqProject + Path.SEPARATOR_CHAR + PREDEF_RAW_DIR);

        Path requestRoot;

        if(subDir != null && !subDir.isEmpty()){
            requestRoot = new Path(stagingRoot + Path.SEPARATOR + subDir);
        } else {
            requestRoot = stagingRoot;
        }

        URI relativeParentPath = stagingRoot.toUri().relativize(requestRoot.getParent().toUri());
        URI relativeSelfPath = stagingRoot.toUri().relativize(requestRoot.toUri());

        String strRelativeSelfPath = "" + relativeSelfPath;
        if(strRelativeSelfPath.isEmpty()){
            strRelativeSelfPath = "root";
        }

        //logger.info("\n===> Staging " + stagingRoot);
        //logger.info("\n===> Requested list of files from " + requestRoot);
        //logger.info("\n===> Relative path (self)" + relativeSelfPath);
        //logger.info("\n===> Relative path (parent)" + relativeParentPath);

        ArrayList<String> list = new ArrayList<>();

        Path p = new Path(requestRoot + Path.SEPARATOR + "*");

        // Parent - lloks like Akhilesh doesn't need this -- commented
        /*String record2 = "{"
                        + "\"name\":\"..\", "
                        + "\"d\":" + 1 + ", "
                        + "\"cat\":\"" + (requestRoot.equals(stagingRoot) ?  "" : relativeParentPath) + "\""
                        + "}";
        list.add(record2);

        String record3 = "{"
                         + "\"name\":\".\", "
                         + "\"d\":" + 1 + ", "
                         + "\"cat\":\"" + relativeSelfPath + "\""
                         + "}";
        list.add(record3);
*/
        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            Boolean isDir = f.isDirectory();
            String name = f.getPath().getName();
            Long size = f.getLen();
            f.getModificationTime();
            String record = "{"
                            + "\"name\":\"" + name + "\", "
                            + "\"size\":" + size + ", "
                            + "\"d\":" + isDir + ", "
                            + "\"cat\":\"" + strRelativeSelfPath + "\""
                            + "}";
            list.add(record);
            //logger.info("===> entry " + record);
        }


        return list;
    }

    public void createProject(String project) throws Exception {
        // Create project directory
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        // Create default data sources
        // Create default catalogs
        if(!fs.exists(projectPath)){
            fs.mkdirs(projectPath);
            Path projectDlPath = new Path(dlRoot + Path.SEPARATOR + project + Path.SEPARATOR + PREDEF_DL_DIR);
            fs.mkdirs(projectDlPath);
            Path fsPath = new Path(projectDlPath + Path.SEPARATOR + DEFAULT_DATA_SOURCE);
            fs.mkdirs(fsPath);
            Path dataCatalogPath = new Path(fsPath + Path.SEPARATOR + DEFAULT_CATALOG );
            fs.mkdirs(dataCatalogPath);
            Path tempPath = new Path(projectPath + Path.SEPARATOR +  PREDEF_TEMP_DIR);
            fs.mkdirs(tempPath);
            Path ctxPath = new Path(projectPath + Path.SEPARATOR +  PREDEF_CTX_DIR);
            fs.mkdirs(ctxPath);
            Path rawPath = new Path(projectPath + Path.SEPARATOR +  PREDEF_RAW_DIR);
            fs.mkdirs(rawPath);
        }
    }

    public void deleteDataSet(String project, String source, String catalog, String setName) throws Exception {
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        if(!fs.exists(projectPath)){
            throw new Exception("Project " + project + " doesn't Exists.");
        }

        // Set default data source and catalog
        String src = (source != null)? source : DEFAULT_DATA_SOURCE;
        String ctlg = (catalog != null)? catalog: DEFAULT_CATALOG;

        // Create data set directory
        Path setPath = new Path(projectPath + Path.SEPARATOR + PREDEF_DL_DIR + Path.SEPARATOR
                + src + Path.SEPARATOR
                + ctlg + Path.SEPARATOR + setName);

        if(fs.exists(setPath)) {
            fs.delete(setPath, true);
            logger.info("Deleted " + setPath);
        }
    }

    public void createDataSet(String project, String source, String catalog, String setName, String metadata) throws Exception {
        // Have to check if project Exists
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        if(!fs.exists(projectPath)){
            throw new Exception("Project " + project + " doesn't Exists.");
        }

        // Set default data source and catalog
        String src = (source != null)? source : DEFAULT_DATA_SOURCE;
        String ctlg = (catalog != null)? catalog: DEFAULT_CATALOG;

        // Create data set directory
        Path setPath = new Path(projectPath + Path.SEPARATOR + PREDEF_DL_DIR + Path.SEPARATOR
                + src + Path.SEPARATOR
                + ctlg + Path.SEPARATOR + setName);

        if(!fs.exists(setPath)) {
            fs.mkdirs(setPath);

            logger.info("Creating new data set " + setPath);

            // Create data directory
            Path dataPath = new Path(setPath + Path.SEPARATOR + DEFAULT_CATALOG);
            fs.mkdirs(dataPath);

            // Initialize meta data file
            Path metaPath = new Path(setPath + Path.SEPARATOR + FILE_DESCRIPTOR);
            FSDataOutputStream f = fs.create(metaPath);
            if(metadata != null) {
                logger.info("Writing metadata " + metadata);
                f.writeBytes(metadata);
            }
            f.close();
        }
    }

    public String  getStagingDirectory(String project){
        return dlRoot + Path.SEPARATOR + project + Path.SEPARATOR + PREDEF_RAW_DIR;
    }
    public int moveToRaw(String project, String absoluteFilePath, String directory, String asName) throws Exception {

        // Build full path to directory
        Path rawPath = new Path(dlRoot
                + Path.SEPARATOR + project
                + Path.SEPARATOR + PREDEF_RAW_DIR
                + ((directory != null && !directory.isEmpty()) ? (Path.SEPARATOR + directory) : "")
        );

        // Create staging directory if necessary
        if(!fs.exists(rawPath)){
            fs.mkdirs(rawPath);
        }

        // Check if we have file to upload
        if(asName != null) {
            // Yes, we have file to upload
            Path filePath = new Path(rawPath + Path.SEPARATOR + asName);
            Path localPath = new Path("file://" + absoluteFilePath);
            fs.copyFromLocalFile(true, localPath, filePath);
        }
        return 0;
    }


}

