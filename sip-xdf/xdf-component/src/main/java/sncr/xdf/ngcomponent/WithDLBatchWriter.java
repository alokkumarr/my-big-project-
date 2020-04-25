package sncr.xdf.ngcomponent;

import java.io.IOException;
import com.google.gson.JsonParser;
import org.apache.spark.sql.Row;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.Dataset;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import scala.Tuple3;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.utils.BdaCoreUtils;
import sncr.xdf.adapters.writers.DLBatchWriter;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.context.DSMapKey;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.MapAccumulator;
import sncr.xdf.context.NGContext;
import sncr.xdf.file.DLDataSetOperations;

/**
 * Created by srya0001 on 9/11/2017.
 */
public interface WithDLBatchWriter {


    default int commitDataSetFromOutputMap(NGContext ngctx, Dataset dataset, String dataSetName, String location, String mode){

        WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
        return helper.writeDataset(DSMapKey.parameter, dataset, dataSetName, location, mode);
    }

    default int commitDataSetFromDSMap(NGContext ngctx, Dataset dataset, String dataSetName, String location, String mode){
        WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
        return helper.writeDataset(DSMapKey.dataset, dataset, dataSetName, location, mode);
    }

    default JsonElement getSchema(NGContext ngctx, Dataset<Row> finalResult){
        WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
        return helper.extractSqlSchema(finalResult);
     }

    default int moveData(InternalContext ctx, NGContext ngctx) {
    	
    	MapAccumulator mapAccumulator = new MapAccumulator();
        try {

            WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
            if (ctx.resultDataDesc == null || ctx.resultDataDesc.isEmpty()) {
                WithDLBatchWriterHelper.logger.warn("Final file collection is Empty, nothing to move.");
                return 0;
            }
            //TODO:: Open JIRA ticket to prepare rollback artifacts.
            //TODO:: Instead of removing data - rename it to _old, _archived or anything else.
            for (MoveDataDescriptor moveTask : ctx.resultDataDesc) {
                WithDLBatchWriterHelper.logger.warn(String.format("DS: %s\nSource: %s\nDest: %s\nFormat: %s\nMode: %s",
                    moveTask.objectName, moveTask.source, moveTask.dest, moveTask.format, moveTask.mode ));

                // TODO:: Fix BDA Meta
                // Check if we have created data sample to move to final destination,
                // the check must be based on actual data presented on DL.
                // Do not rely on any settings,  see DLBatchWriter.baseWrite method
                // to see how is data are written
                if (helper.doesSampleExist(moveTask.source) ) {

                    String sampleDirSource = helper.getSampleSourceDir(moveTask);
                    String sampleDirDest = helper.getSampleDestDir(moveTask);

                    WithDLBatchWriterHelper.logger.debug("Clean up sample for " + moveTask.objectName);
                    if (helper.createOrCleanUpDestDir(sampleDirDest, moveTask.objectName,ngctx) < 0) return -1;

                    WithDLBatchWriterHelper.logger.debug("Moving sample ( " + moveTask.objectName + ") from " + sampleDirSource + " to " + sampleDirDest);
                    helper.moveFilesForDataset(sampleDirSource, sampleDirDest, moveTask.objectName, moveTask.format, moveTask.mode, ctx);

                }
                else{
                    WithDLBatchWriterHelper.logger.debug("Sample data are not presented even if settings says otherwise - skip moving sample to permanent location");
                }
                
                helper.createDestDir(moveTask.dest, moveTask.source);

                moveTask.source = helper.getActualDatasetSourceDir(moveTask.source);
                if(moveTask.partitionList == null || moveTask.partitionList.size() == 0) {
                    //Remove existing data if they are presented
                    if (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) {
                        if (helper.createOrCleanUpDestDir(moveTask.dest, moveTask.objectName, ngctx) < 0) return -1;
                    }
                    WithDLBatchWriterHelper.logger.info("Moving data ( " + moveTask.objectName + ") from " + moveTask.source + " to " + moveTask.dest);
                    helper.moveFilesForDataset(moveTask.source, moveTask.dest, moveTask.objectName, moveTask.format, moveTask.mode, ctx);
                }
                else // else - move partitions result
                {
                    Set<String> partitions = new HashSet<>();
                    Path lp = new Path(moveTask.source);

                    String m = "/"; for (String s : moveTask.partitionList) m += s + "*/"; m += "*/";
                    WithDLBatchWriterHelper.logger.debug("Glob depth: " + m);


                    FileStatus[] it = HFileOperations.fs.globStatus(new Path(moveTask.source + m ), DLDataSetOperations.FILEDIR_FILTER);
                    WithDLBatchWriterHelper.logger.debug("Got " + it.length + " files, enumerating partitions. Look for partitions into: " + lp);
                    for(FileStatus file : it){
                        if(file.isFile()){
                            // We also need list of partitions (directories) for reporting and appending/replacing
                            // We will extract parent directory of the file for that
                            String ss = file.getPath().getParent().toString();
                            
                            
                            //
                            // Potential bug: if batch name contains object name - position will be calculated incorrectly
                            //
                            /**
                             * Updated to last index as it fails case lp.getName is found in 
                             * beginning itself
                             */
                            int i = ss.lastIndexOf(lp.getName());
                            // Store full partition path for future use in unique collection
                            // Should <set> to be used instead of <map>?
                            String p = file.getPath().getParent().toString().substring(i + lp.getName().length());
                            WithDLBatchWriterHelper.logger.debug("Add partition to result set: " + p);
                            partitions.add(p);
                            // Update file counter for reporting purposes
                            ctx.globalFileCount++;
                        }
                    }
                    WithDLBatchWriterHelper.logger.warn("Done.");
                    Integer completedFileCount = 0;
                    Map<String, Tuple3<Long, Integer, Integer>> partitionsInfo = new HashMap<>();
                    // Check if configuration asks data to be copied
                    // to final processed location
                    WithDLBatchWriterHelper.logger.debug("Merge partitions (" + partitions.size() + ")...");
                    
                    
                    // Copy partitioned data to final location
                    // Process partition locations - relative paths
                    for(String e : partitions) {
                    	 
                        Integer copiedFiles = helper.copyMergePartition( e , moveTask, ctx, mapAccumulator.value());
                        partitionsInfo.put(e, new Tuple3<>(1L, copiedFiles, copiedFiles));
                        completedFileCount += copiedFiles;
                    }
                    
                    WithDLBatchWriterHelper.logger.debug("Deleting source :: "+ moveTask.source );
                    //Delete temporary data object directory
                    String normalizedPath= BdaCoreUtils.normalizePath(moveTask.source);
                    HFileOperations.fs.delete(new Path(normalizedPath ), true);
                }
            } //<-- for
            
            return 0;
        }
        catch(IOException e){
            WithDLBatchWriterHelper.logger.error("IO exception during move operation, cancel moving stage: ", e);
        }
        catch(Exception e){
            WithDLBatchWriterHelper.logger.error("Exception during move operation, cancel moving stage: ", e);
        }
        return -1;
    }

    class WithDLBatchWriterHelper extends DLBatchWriter {
        private static final Logger log = LoggerFactory.getLogger(WithDLBatchWriterHelper.class);
        private FileSystem fs = HFileOperations.getFileSystem();
        private FileContext fc = HFileOperations.getFileContext();


        public WithDLBatchWriterHelper(NGContext ngctx) {
            super(ngctx);
        }
        

        public int copyMergePartition(String partitionKey,
                                      MoveDataDescriptor moveDataDesc,
                                      InternalContext ctx, Map<String, Long> partitionKeys ) throws Exception {
            int numberOfFilesSuccessfullyCopied = 0;

            String normalizedSourcePath =BdaCoreUtils.normalizePath( moveDataDesc.source + partitionKey);
            String normalizedDestPath = BdaCoreUtils.normalizePath(moveDataDesc.dest +  partitionKey);
            Path source = new Path(normalizedSourcePath);
            Path dest = new Path(normalizedDestPath);

            String ext = "." + moveDataDesc.format.toLowerCase();

            // If we have to replace partition - just remove directory
            // Will do nothing if directory doesn't exists`
            if(! moveDataDesc.mode.toLowerCase().equals("append")) {
            	
            	
            	/**
            	 * Delete only if it is not part of current partition. 
            	 * Multiple files partition use case
            	 */
                if(HFileOperations.fs.exists(dest) ) {
                 	boolean isOldPartition = (partitionKeys.get(partitionKey) == null || partitionKeys.get(partitionKey) == 0);
                 	logger.debug("Delete check isOldPartition ???? "+ isOldPartition);
                 	if(isOldPartition) {
                 		logger.debug("######## Deleting "+ dest  + "#########");
                 		HFileOperations.fs.delete(dest, true);
                 		partitionKeys.put(partitionKey,1L);
                 	}
                }
                	
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
                    String loc = dest + Path.SEPARATOR + moveDataDesc.objectName + "." + ngctx.batchID + "." + ngctx.startTs + "." + String.format("%05d", ctx.globalFileCount++) + ext;
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

        private JsonElement extractSqlSchema(Dataset<Row> finalResult) {
            JsonParser parser = new JsonParser();
                return parser.parse(finalResult.schema().json());
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

        public int writeDataset(DSMapKey mapType, Dataset dataset, String dataSetName, String location, String mode) {

            try{
                // Some components are using outputs (Transformer), other (SQL, Parser)  outputDataSets
                // in any case we need some attributed from dataset descriptors.

                Map<String, Object> outputDS = null;
                if (mapType == DSMapKey.dataset)
                    outputDS = ngctx.outputDataSets.get(dataSetName);
                else
                    outputDS = ngctx.outputs.get(dataSetName);

                String name = (String) outputDS.get(DataSetProperties.Name.name());
                String loc = location;
                //String loc = location + Path.SEPARATOR + name;
                logger.info("Output write location : " + loc);

                format = (String) outputDS.get(DataSetProperties.Format.name());
                numberOfFiles = (Integer) outputDS.get(DataSetProperties.NumberOfFiles.name());
                keys = (List<String>) outputDS.get(DataSetProperties.PartitionKeys.name());

                //TODO:: By default - create sample for each produced dataset and mark a dataset as sampled with a sampling model
                //TODO:: Fix DataSetProperties (BDA Meta), add sampling model: sample
                String sampling = (String) outputDS.get(DataSetProperties.Sample.name());

                boolean doSampling = (sampling != null && !sampling.equalsIgnoreCase("none"));

                if (ngctx.runningPipeLine) {
                    if (ngctx.persistMode) {
                        baseWrite(dataset,  loc, !(mode.equalsIgnoreCase("append")), doSampling);
                    }
                }
                else
                {
                    baseWrite(dataset,  loc, !(mode.equalsIgnoreCase("append")), doSampling);
                }

                // Whatever a component uses: outputs or outputDataSets --
                // All final data should go to outputDataSets, to make a single source of
                // dataset descriptors.

                DateTime timeStamp = new DateTime();
                long currentTime = timeStamp.getMillis() / 1000;

                if (mapType == DSMapKey.dataset) {
                    outputDS.put(DataSetProperties.Schema.name(), extractSchema(dataset));
                    logger.warn("Dataset: " + name + ", Result schema: " + ((JsonElement) outputDS.get(DataSetProperties.Schema.name())).toString());

                    //Add record count
                    outputDS.put(DataSetProperties.RecordCount.name(), extractrecordCount(dataset));

                    //Add timestamp fields
//                outputDS.put(DataSetProperties.CreatedTime.name(), currentTime);
//                outputDS.put(DataSetProperties.ModifiedTime.name(), currentTime);
                }
                else{
                    Map<String, Object> outputDS2 = ngctx.outputDataSets.get(name);
                    outputDS2.put(DataSetProperties.Schema.name(), extractSchema(dataset));
                    logger.warn("Dataset: " + name + ", Result schema: " + ((JsonElement) outputDS2.get(DataSetProperties.Schema.name())).toString());

                    //Add record count
                    outputDS2.put(DataSetProperties.RecordCount.name(), extractrecordCount(dataset));

                    //Add timestamp fields
//                outputDS2.put(DataSetProperties.CreatedTime.name(), currentTime);
//                outputDS2.put(DataSetProperties.ModifiedTime.name(), currentTime);
                }

                return 0;
            } catch (Exception e) {
                String error = ExceptionUtils.getFullStackTrace(e);
                logger.error("Error at writing result: " + error);
                return -1;
            }
        }

        private void moveFilesForDataset(String source, String dest, String objectName, String format, String mode, InternalContext ctx) throws Exception {
        	
        	logger.debug("#### Move files starting. format ::"+ format );
            //If output files are PARQUET files - clean up temp. directory - remove
            // _metadata and _common_? files.
            if (format.equalsIgnoreCase(DLDataSetOperations.FORMAT_PARQUET)) {
                DLDataSetOperations.cleanupDataDirectory(source);
            } else if (format.equalsIgnoreCase(DLDataSetOperations.FORMAT_JSON)) {
            }

            WithDLBatchWriterHelper.logger.info("Moving files from " + source + " to " + dest);

            //get list of files to be processed
            FileStatus[] files = fs.listStatus(new Path(source));

            WithDLBatchWriterHelper.logger.warn("Prepare the list of the files, number of files: " + files.length);
            
            for (int i = 0; i < files.length; i++) {
                if (files[i].getLen() > 0) {
                    String srcFileName = source + Path.SEPARATOR + files[i].getPath().getName();

                    //move data files with new name to output location
                    String destFileName =   dest + Path.SEPARATOR +
                        objectName + "." +
                        ngctx.batchID + "." + ngctx.startTs + "." +
                        String.format("%05d", ctx.globalFileCount ) +
                        "." + format;

                    Path fdest = new Path(destFileName);
                    WithDLBatchWriterHelper.logger.debug(String.format("move from: %s to %s", srcFileName, fdest.toString()));
                    Options.Rename opt = (mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) ? Options.Rename.OVERWRITE : Options.Rename.NONE;
                    Path src = new Path(srcFileName);
                    Path dst = new Path(destFileName);
                    
                    fc.rename(src, dst, opt);
                }
                ctx.globalFileCount++;
            }
            logger.warn("Remove TMP directory if it Exists: " + source);
            Path tmpDIR = new Path(source);
            if (fs.exists(tmpDIR))
                fs.delete(tmpDIR, true);
            logger.warn("Data Objects were successfully moved from " + source + " into " + dest);
        }

        public int createOrCleanUpDestDir(String dest, String objectName, NGContext ngctx) {
            Path objOutputPath = new Path(dest);
            try {
                if (fs.exists(objOutputPath)) {

                    FileStatus[] list = fs.listStatus(objOutputPath);
                    for (int i = 0; i < list.length; i++) {
                    	
                    	if(!list[i].getPath().getName().contains(ngctx.batchID + "." + ngctx.startTs + ".")) {
                            Path normalizedPath = BdaCoreUtils.normalizePath(list[i].getPath());
                    		fs.delete(normalizedPath, true);
                    	}
                        
                    }

                } else {
                    logger.debug("Output directory: " + objOutputPath + " for data object/data sample: " + objectName + " does not Exists -- create it");
                    fs.mkdirs(objOutputPath);
                }
            } catch (IOException e) {
                logger.error("IO exception in attempt to create/clean up: destination directory", e);
                return -1;
            }
            return 0;
        }

        public int createDestDir(String dest, String objectName) {
            Path objOutputPath = new Path(dest);
            try {
                if (fs.exists(objOutputPath)) {
                	logger.debug("Output directory already exists. Use existing and not creating new" );
                } else {
                    logger.debug("Output directory: " + objOutputPath + " for data object/data sample: " + objectName + " does not Exists -- create it");
                    fs.mkdirs(objOutputPath);
                }
            } catch (IOException e) {
                logger.warn("IO exception in attempt to create/clean up: destination directory", e);
                return -1;
            }
            return 0;
        }
    }

}
