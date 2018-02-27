package sncr.xdf.component;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import scala.Tuple3;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.DLDataSetOperations;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by srya0001 on 9/11/2017.
 */
public interface WithMovableResult {


    public default int doMove(Context ctx, List<MoveDataDescriptor> resultDataDesc) {
        try {

            WithMovableResultHelper helper = new WithMovableResultHelper();
            if (resultDataDesc == null || resultDataDesc.isEmpty()) {
                WithMovableResultHelper.logger.warn("Final file collection is Empty, nothing to move.");
                return 0;
            }

            int fileCounter = 0;
            for (MoveDataDescriptor moveTask : resultDataDesc) {

                if (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) {
                    Path objOutputPath = new Path(moveTask.dest);
                    try {
                        if (ctx.fs.exists(objOutputPath)) {

                            FileStatus[] list = ctx.fs.listStatus(objOutputPath);
                            for (int i = 0; i < list.length; i++) {
                                ctx.fs.delete(list[i].getPath(), true);
                            }

                        } else {
                            WithMovableResultHelper.logger.warn("Output directory: " + objOutputPath + " for data object: " + moveTask.objectName + " does not Exists -- create it");
                            ctx.fs.mkdirs(objOutputPath);
                        }
                    } catch (IOException e) {
                        WithMovableResultHelper.logger.warn("IO exception in attempt to create/clean up: destination directory", e);
                        return -1;
                    }
                }

                if(moveTask.partitionList == null || moveTask.partitionList.size() == 0) {

                    WithMovableResultHelper.logger.info("Moving data ( " + moveTask.objectName + ") from " + moveTask.source + " to " + moveTask.dest);

                    //If output files are PARQUET files - clean up temp. directory - remove
                    // _metadata and _common_? files.
                    if (moveTask.format.equalsIgnoreCase(DLDataSetOperations.FORMAT_PARQUET)) {
                        DLDataSetOperations.cleanupDataDirectory(moveTask.source);
                    } else if (moveTask.format.equalsIgnoreCase(DLDataSetOperations.FORMAT_JSON)) {
                    }

                    //get list of files to be processed
                    FileStatus[] files = ctx.fs.listStatus(new Path(moveTask.source));

                    WithMovableResultHelper.logger.debug("Prepare the list of the files, number of files: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getLen() > 0) {
                            String srcFileName = moveTask.source + Path.SEPARATOR + files[i].getPath().getName();
                            //Move data files with new name to output location
                            String destFileName = generateOutputFileName(ctx, moveTask, fileCounter);
                            Path dest = new Path(destFileName);
                            WithMovableResultHelper.logger.debug(String.format("Move from: %s to %s", srcFileName, dest.toString()));
                            Options.Rename opt = (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) ? Options.Rename.OVERWRITE : Options.Rename.NONE;
                            Path src = new Path(srcFileName);
                            Path dst = new Path(destFileName);
                            ctx.fc.rename(src, dst, opt);
                        }
                        fileCounter++;
                    }
                    WithMovableResultHelper.logger.debug("Remove TMP directory if it Exists: " + moveTask.source);
                    Path tmpDIR = new Path(moveTask.source);
                    if (ctx.fs.exists(tmpDIR))
                        ctx.fs.delete(tmpDIR, true);
                    WithMovableResultHelper.logger.debug("Data Objects were successfully moved from " + moveTask.source + " into " + moveTask.dest);

                }
                else // else - move partitions result
                {

                    int fileCount = 0;
                    Set<String> partitions = new HashSet<>();
                    Path lp = new Path(moveTask.source);

                    String m = "/"; for (String s : moveTask.partitionList) m += s + "*/"; m += "*/";
                    WithMovableResultHelper.logger.trace("Glob depth: " + m);


                    FileStatus[] it = HFileOperations.fs.globStatus(new Path(moveTask.source + m ), DLDataSetOperations.FILEDIR_FILTER);
                    WithMovableResultHelper.logger.debug("Got " + it.length + " files, enumerating partitions. Look for partitions into: " + lp);
                    for(FileStatus file : it){
                        if(file.isFile()){
                            // We also need list of partitions (directories) for reporting and appending/replacing
                            // We will extract parent directory of the file for that
                            String ss = file.getPath().getParent().toString();
                            //
                            // Potential bug: if batch name contains object name - position will be calculated incorrectly
                            //
                            int i = ss.indexOf(lp.getName());
                            // Store full partition path for future use in unique collection
                            // Should <set> to be used instead of <map>?
                            String p = file.getPath().getParent().toString().substring(i + lp.getName().length());
                            WithMovableResultHelper.logger.debug("Add partition to result set: " + p);
                            partitions.add(p);
                            // Update file counter for reporting purposes
                            fileCount++;
                        }
                    }
                    WithMovableResultHelper.logger.debug("Done.");
                    Integer completedFileCount = 0;
                    Map<String, Tuple3<Long, Integer, Integer>> partitionsInfo = new HashMap<>();
                    // Check if configuration asks data to be copied
                    // to final processed location
                    WithMovableResultHelper.logger.debug("Merge partitions (" + partitions.size() + ")...");
                    // Copy partitioned data to final location
                    // Process partition locations - relative paths
                    for(String e : partitions) {
                        Integer copiedFiles = helper.copyMergePartition( e , moveTask, ctx);
                        partitionsInfo.put(e, new Tuple3<>(1L, copiedFiles, copiedFiles));
                        completedFileCount += copiedFiles;
                    }
                    //Delete temporary data object directory
                    HFileOperations.fs.delete(new Path(moveTask.source ), true);
                }
            } //<-- for
            return 0;
        }
        catch(IOException e){
            WithMovableResultHelper.logger.error("IO exception during move operation, cancel moving stage: ", e);
        }
        catch(Exception e){
            WithMovableResultHelper.logger.error("Exception during move operation, cancel moving stage: ", e);
        }
        return -1;
    }

     default String generateOutputFileName(Context ctx, MoveDataDescriptor moveDesc, int fileCounter)  {

        String fileExt = moveDesc.format;
        return moveDesc.dest + Path.SEPARATOR + moveDesc.objectName + "." + ctx.batchID + "." + ctx.startTs + "." + String.format("%05d", fileCounter ) + "." + fileExt;
    }



    class MoveDataDescriptor {

        public String source;
        public String dest;
        public String mode;
        public String format;
        public String objectName;
        public List<String> partitionList;

        {
            mode = DLDataSetOperations.MODE_APPEND;
            format = DLDataSetOperations.FORMAT_PARQUET;
        }

        public MoveDataDescriptor(String src,
                                  String dest,
                                  String objectName,
                                  String mode,
                                  String format,
                                  List<String> partitionList) {
            this.source = src;
            this.dest = dest;
            this.mode = mode.toLowerCase();
            this.format = format.toLowerCase();
            this.objectName = objectName;
            this.partitionList = partitionList;
        }
    }

    class WithMovableResultHelper {
        private static final Logger logger = Logger.getLogger(WithMovableResult.class);


        public int copyMergePartition(String partitionKey,
                                      MoveDataDescriptor moveDataDesc,
                                      Context ctx ) throws Exception {
            int numberOfFilesSuccessfullyCopied = 0;
            Path source = new Path(moveDataDesc.source + partitionKey);
            Path dest = new Path(moveDataDesc.dest +  partitionKey);

            String ext = "." + moveDataDesc.format.toLowerCase();

            // If we have to replace partition - just remove directory
            // Will do nothing if directory doesn't exists
            if(! moveDataDesc.mode.toLowerCase().equals("append")) {
                if(HFileOperations.fs.exists(dest))
                    HFileOperations.fs.delete(dest, true);
            }

            // Check if destination folder exists
            // This will create destination if not exists or do nothing if already exists
            HFileOperations.fs.mkdirs(dest);

            // Prepare the list of the files
            FileStatus[] files = null;
            switch (moveDataDesc.format){
                case "parquet":
                    files = HFileOperations.fs.listStatus(source, DLDataSetOperations.PARQUET_FILTER);
                    break;
                case "json" :
                    files = HFileOperations.fs.listStatus(source, DLDataSetOperations.JSON_FILTER);
                    break;
                case "csv" :
                    files = HFileOperations.fs.listStatus(source, DLDataSetOperations.CSV_FILTER);
                    break;
                default:
                    files = HFileOperations.fs.listStatus(source, DLDataSetOperations.PARQUET_FILTER);
                    break;
            }

            for(FileStatus s : files) {
                try {
                    // Try to copy file by file to get better control on potential copy issues
                    // If file already exists in destination it will not be replaced with new one
                    // Appropriate exception will be generated listing all already existent files
                    String loc = dest + Path.SEPARATOR + moveDataDesc.objectName + "." + ctx.batchID + "." + ctx.startTs + "." + String.format("%05d", ctx.globalFileCount++) + ext;
                    Path newName = new Path(loc);
                    HFileOperations.fc.rename(s.getPath(), newName);
                    numberOfFilesSuccessfullyCopied++;
                } catch (java.io.IOException e) {
                    logger.error(ExceptionUtils.getFullStackTrace(e));
                    throw e;
                }
            }
            return numberOfFilesSuccessfullyCopied;
        }

        private Map<String, String> listOfRemovedPartitions = new HashMap<>();

        private int removeDestPartition(Path path) throws Exception {
            // Make sure we delete partition only once during execution
            // Since multiple objects may end up inside the same destination directory
            // (if entity is configured to the same value for more than 1 object)
            // multiple objects can contribute to the same partition
            // We only have to remove partition once - when replacement partition created first time

            // Check the list of removed partitions
            if(listOfRemovedPartitions.get(path.toString()) == null) {
                if(HFileOperations.fs.exists(path))
                    HFileOperations.fs.delete(path, true);
                listOfRemovedPartitions.put(path.toString(), path.toString());
            }
            return 0;
        }

    }

}
