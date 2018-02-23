package sncr.bda.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import sncr.bda.base.MetadataBase;

/**
 * Created by srya0001 on 10/26/2017.
 */
/**
 * @author spau0004
 *
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

    public void createDataSet(String project, String source, String catalog, String setName) throws Exception {
        // Have to check if project Exists
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        if(!fs.exists(projectPath)){
            throw new Exception("ProjectService " + project + " doesn't Exists.");
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
            throw new Exception("ProjectService is not specified");
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
            throw new Exception("ProjectService is not specified");
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
            throw new Exception("ProjectService is not specified");
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

    public ArrayList<String> getListOfStagedFiles(String rqProject, String subDir, String relativePath) throws Exception {
        logger.trace("getListOfStagedFiles : Getting the list of directories & folder for specified directory in the cluster for the project : " + rqProject);
        if (rqProject == null) {
            throw new Exception("ProjectService is not specified.");
        }
        Path stagingRoot = new Path(rqProject);
        Path requestRoot;
        if(subDir != null && !subDir.isEmpty()){
            requestRoot = new Path(stagingRoot + Path.SEPARATOR + subDir);
        } else {
            requestRoot = stagingRoot;
        }
        URI relativeSelfPath = stagingRoot.toUri().relativize(requestRoot.toUri());
        String strRelativeSelfPath = "" + relativeSelfPath;
        if(strRelativeSelfPath.isEmpty()){
            strRelativeSelfPath = rqProject;
        }
        logger.trace("Staging " + stagingRoot);
        logger.trace("Requested list of files from " + requestRoot);
        logger.trace("Relative path (self) " + relativeSelfPath);
        ArrayList<String> list = new ArrayList<>();
        Path p = new Path(requestRoot + Path.SEPARATOR + "*");
        logger.trace("getListOfStagedFiles : Getting the list of directories & files from the path : " + p.toUri().toString());
        FileStatus[] plist = fs.globStatus(p);
        for (FileStatus f : plist) {
            Boolean isDir = f.isDirectory();
            String name = f.getPath().getName();
            logger.trace("getListOfStagedFiles : FileStatus[] : " + name);
            boolean countOfFiles = listAllFilePath(f.getPath(), fs).size()>0?true:false;
            logger.trace("getListOfStagedFiles : FileStatus[]:countOfFiles : " + name);
            Long size = f.getLen();
            f.getModificationTime();
            String record = "{"
                            + "\"name\":\"" + name + "\", "
                            + "\"size\":" + size + ", "
                            + "\"isDirectory\":" + isDir + ", "
                            + "\"path\":\"" + relativePath + "\", "
                            + "\"subDirectories\":\"" + countOfFiles  + "\""
                            + "}";
            list.add(record);
        }
        logger.trace("Getting the list of directories & folder for specified directory in the cluster." + list);
        return list;
    }
   
    /**
     * This method is used to get the list of sub directories
     * @param hdfsFilePath
     * @param fs
     * @return List<String>
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<String> listAllFilePath(Path hdfsFilePath, FileSystem fs)
        throws FileNotFoundException, IOException {
          List<String> filePathList = new ArrayList<String>();
          Queue<Path> fileQueue = new LinkedList<Path>();
          fileQueue.add(hdfsFilePath);
          logger.trace("fileQueue.add : " + hdfsFilePath);
          while (!fileQueue.isEmpty()) {
            Path filePath = fileQueue.remove();
            if (fs.isDirectory(filePath)) {
              logger.trace("listAllFilePath fs.isDirectory : " + filePath.toUri().toString());
              filePathList.add(filePath.toString());
              logger.trace("filePathList.add : " + filePath.toUri().toString());
            } 
          }
          logger.trace("listAllFilePath filePathList : " + filePathList);
          return filePathList;
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
        //TODO:: Add creating project record in metastore, table Glob

    }

    public void deleteDataSet(String project, String source, String catalog, String setName) throws Exception {
        Path projectPath = new Path(dlRoot + Path.SEPARATOR + project);
        if(!fs.exists(projectPath)){
            throw new Exception("ProjectService " + project + " doesn't Exists.");
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
            throw new Exception("ProjectService " + project + " doesn't Exists.");
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
    public int moveToRaw(String projectPath, String absoluteFilePath, String directory, String asName) throws Exception {

        // Build full path to directory
        Path rawPath = new Path(dlRoot
                + Path.SEPARATOR + projectPath);

        // Create staging directory if necessary
        if(!fs.exists(rawPath)){
            fs.mkdirs(rawPath);
        }

        // Check if we have file to upload
        if(asName != null) {
            // Yes, we have file to upload
            Path filePath = new Path(rawPath + Path.SEPARATOR + asName);
            if(fs.exists(filePath)){
              String fileDate = new SimpleDateFormat("mmss").format(new Date());
              asName = asName.substring(0, asName.indexOf('.')) +"_"+fileDate + asName.substring(asName.indexOf('.'), asName.length());
              filePath = new Path(rawPath + Path.SEPARATOR + asName);
            }
            Path localPath = new Path("file://" + absoluteFilePath);
            fs.copyFromLocalFile(true, localPath, filePath);
        }
        return 0;
    }


}

