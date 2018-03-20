package sncr.xdf.component;

import com.google.gson.JsonElement;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import scala.Tuple3;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.context.DSMapKey;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.adapters.writers.*;



import java.io.IOException;
import java.util.*;

/**
 * Created by srya0001 on 9/11/2017.
 */
public interface WithDLBatchWriter {

    default void registerDataset(NGContext ngctx, Dataset dataset, String dataSetName){
        ngctx.registerDatset(dataSetName, dataset);
    }

    default int commitDataSetFromOutputMap(NGContext ngctx, Dataset dataset, String dataSetName, String location){
        WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
        return helper.writeDataset(DSMapKey.parameter, dataset, dataSetName, location);
    }

    default int commitDataSetFromDSMap(NGContext ngctx, Dataset dataset, String dataSetName, String location){
        WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
        return helper.writeDataset(DSMapKey.dataset, dataset, dataSetName, location);
    }

    default int moveData(InternalContext ctx, NGContext ngctx) {
        try {

            WithDLBatchWriterHelper helper = new WithDLBatchWriterHelper(ngctx);
            if (ctx.resultDataDesc == null || ctx.resultDataDesc.isEmpty()) {
                WithDLBatchWriterHelper.logger.warn("Final file collection is Empty, nothing to move.");
                return 0;
            }


            //TODO:: Open JIRA ticket to prepare rollback artifacts.
            //TODO:: Instead of removing data - rename it to _old, _archived or anything else.
            for (MoveDataDescriptor moveTask : ctx.resultDataDesc) {

                //Remove existing data if they are presented
                if (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) {
                    if (helper.removeExistingData(moveTask.dest, moveTask.objectName) < 0) return -1;
                }

                // TODO:: Fix BDA Meta
                // Check if we have created data sample to move to final destination,
                // the check must be based on actual data presented on DL.
                // Do not rely on any settings,  see DLBatchWriter.baseWrite method
                // to see how is data are written
                if (helper.doesSampleExist(moveTask.source) ) {

                    String sampleDirSource = helper.getSampleSourceDir(moveTask);
                    String sampleDirDest = helper.getSampleDestDir(moveTask);

                    WithDLBatchWriterHelper.logger.info("Clean up sample for " + moveTask.objectName);
                    if (helper.removeExistingData(sampleDirDest, moveTask.objectName) < 0) return -1;

                    WithDLBatchWriterHelper.logger.info("Moving sample ( " + moveTask.objectName + ") from " + sampleDirSource + " to " + sampleDirDest);
                    helper.moveFilesForDataset(sampleDirSource, sampleDirDest, moveTask.objectName, moveTask.format, moveTask.mode, ctx);

                }
                else{
                    WithDLBatchWriterHelper.logger.debug("Sample data are not presented even if settings says otherwise - skip moving sample to permanent location");
                }

                moveTask.source = helper.getActualDatasetSourceDir(moveTask.source);
                if(moveTask.partitionList == null || moveTask.partitionList.size() == 0) {

                    WithDLBatchWriterHelper.logger.info("Moving data ( " + moveTask.objectName + ") from " + moveTask.source + " to " + moveTask.dest);
                    helper.moveFilesForDataset(moveTask.source, moveTask.dest, moveTask.objectName, moveTask.format, moveTask.mode, ctx);
                }
                else // else - move partitions result
                {

                    Set<String> partitions = new HashSet<>();
                    Path lp = new Path(moveTask.source);

                    String m = "/"; for (String s : moveTask.partitionList) m += s + "*/"; m += "*/";
                    WithDLBatchWriterHelper.logger.trace("Glob depth: " + m);


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
                            int i = ss.indexOf(lp.getName());
                            // Store full partition path for future use in unique collection
                            // Should <set> to be used instead of <map>?
                            String p = file.getPath().getParent().toString().substring(i + lp.getName().length());
                            WithDLBatchWriterHelper.logger.debug("Add partition to result set: " + p);
                            partitions.add(p);
                            // Update file counter for reporting purposes
                            ctx.globalFileCount++;
                        }
                    }
                    WithDLBatchWriterHelper.logger.debug("Done.");
                    Integer completedFileCount = 0;
                    Map<String, Tuple3<Long, Integer, Integer>> partitionsInfo = new HashMap<>();
                    // Check if configuration asks data to be copied
                    // to final processed location
                    WithDLBatchWriterHelper.logger.debug("Merge partitions (" + partitions.size() + ")...");
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
            WithDLBatchWriterHelper.logger.error("IO exception during move operation, cancel moving stage: ", e);
        }
        catch(Exception e){
            WithDLBatchWriterHelper.logger.error("Exception during move operation, cancel moving stage: ", e);
        }
        return -1;
    }

    class WithDLBatchWriterHelper extends DLBatchWriter {
        private static final Logger logger = Logger.getLogger(WithDLBatchWriter.class);

        public WithDLBatchWriterHelper(NGContext ngctx) {
            super(ngctx);
        }


        public int copyMergePartition(String partitionKey,
                                      MoveDataDescriptor moveDataDesc,
                                      InternalContext ctx ) throws Exception {
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

        public int writeDataset(DSMapKey mapType, Dataset dataset, String dataSetName, String location) {

        try{

            // Some components are using outputs (Transformer), other (SQL, Parser)  outputDataSets
            // in any case we need some attributed from dataset descriptors.

            Map<String, Object> outputDS = null;
            if (mapType == DSMapKey.dataset)
                outputDS = ngctx.outputDataSets.get(dataSetName);
            else
                outputDS = ngctx.outputs.get(dataSetName);

            String name = (String) outputDS.get(DataSetProperties.Name.name());

            String loc = (String) outputDS.get(DataSetProperties.PhysicalLocation.name());
            if (location != null)
                loc = location + Path.SEPARATOR + name;

            format = (String) outputDS.get(DataSetProperties.Format.name());
            numberOfFiles = (Integer) outputDS.get(DataSetProperties.NumberOfFiles.name());
            keys = (List<String>) outputDS.get(DataSetProperties.PartitionKeys.name());

            String mode = (String) outputDS.get(DataSetProperties.Mode.name());


            //TODO:: By default - create sample for each produced dataset and mark a dataset as sampled with a sampling model
            //TODO:: Fix DataSetProperties (BDA Meta), add sampling model: sample
            String sampling = (String) outputDS.get("sample");

            baseWrite(dataset,  loc, !(mode.equalsIgnoreCase("append")), (sampling != null && !sampling.equalsIgnoreCase("none")));


            // Whatever a component uses: outputs or outputDataSets --
            // All final data should go to outputDataSets, to make a single source of
            // dataset descriptors.

            if (mapType == DSMapKey.dataset)
                outputDS.put(DataSetProperties.Schema.name(), extractSchema(dataset));
            else{
                Map<String, Object> outputDS2 = ngctx.outputDataSets.get(name);
                outputDS2.put(DataSetProperties.Schema.name(), extractSchema(dataset));
            }

            logger.trace("Dataset: "  + name + ", Result schema: " + ((JsonElement)outputDS.get(DataSetProperties.Schema.name())).toString());

            return 0;
        } catch (Exception e) {
            String error = ExceptionUtils.getFullStackTrace(e);
            logger.error("Error at writing result: " + error);
            return -1;
        }


    }

        private void moveFilesForDataset(String source, String dest, String objectName, String format, String mode, InternalContext ctx) throws Exception {

            //If output files are PARQUET files - clean up temp. directory - remove
            // _metadata and _common_? files.
            if (format.equalsIgnoreCase(DLDataSetOperations.FORMAT_PARQUET)) {
                DLDataSetOperations.cleanupDataDirectory(source);
            } else if (format.equalsIgnoreCase(DLDataSetOperations.FORMAT_JSON)) {
            }

            //get list of files to be processed
            FileStatus[] files = ngctx.fs.listStatus(new Path(source));

            WithDLBatchWriterHelper.logger.debug("Prepare the list of the files, number of files: " + files.length);
            for (int i = 0; i < files.length; i++) {
                if (files[i].getLen() > 0) {
                    String srcFileName = source + Path.SEPARATOR + files[i].getPath().getName();
                    //move data files with new name to output location

                    String destFileName =   dest + Path.SEPARATOR +
                                            objectName + "." +
                                            ctx.batchID + "." + ctx.startTs + "." +
                                            String.format("%05d", ctx.globalFileCount ) +
                                            "." + format;

                    Path fdest = new Path(destFileName);
                    WithDLBatchWriterHelper.logger.debug(String.format("move from: %s to %s", srcFileName, fdest.toString()));
                    Options.Rename opt = (mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) ? Options.Rename.OVERWRITE : Options.Rename.NONE;
                    Path src = new Path(srcFileName);
                    Path dst = new Path(destFileName);
                    ngctx.fc.rename(src, dst, opt);
                }
                ctx.globalFileCount++;
            }
            logger.debug("Remove TMP directory if it Exists: " + source);
            Path tmpDIR = new Path(source);
            if (ngctx.fs.exists(tmpDIR))
                ngctx.fs.delete(tmpDIR, true);
            logger.debug("Data Objects were successfully moved from " + source + " into " + dest);

        }

        public int removeExistingData(String dest, String objectName) {
            Path objOutputPath = new Path(dest);
            try {
                if (ngctx.fs.exists(objOutputPath)) {

                    FileStatus[] list = ngctx.fs.listStatus(objOutputPath);
                    for (int i = 0; i < list.length; i++) {
                        ngctx.fs.delete(list[i].getPath(), true);
                    }

                } else {
                    logger.warn("Output directory: " + objOutputPath + " for data object/ data sample: " + objectName + " does not Exists -- create it");
                    ngctx.fs.mkdirs(objOutputPath);
                }
            } catch (IOException e) {
                logger.warn("IO exception in attempt to create/clean up: destination directory", e);
                return -1;
            }
            return 0;
        }
    }

}
