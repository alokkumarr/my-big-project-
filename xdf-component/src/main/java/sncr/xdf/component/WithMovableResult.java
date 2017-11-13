package sncr.xdf.component;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.xdf.context.Context;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.core.file.HDirOperations;

import java.io.IOException;
import java.util.List;

/**
 * Created by srya0001 on 9/11/2017.
 */
public interface WithMovableResult {


    public default int doMove(Context ctx, List<MoveDataDescriptor> resultDataDesc) {
        try {
            if (resultDataDesc == null || resultDataDesc.isEmpty()) {
                WithMovableResultAux.logger.warn("Final file collection is Empty, nothing to move.");
                return 0;
            }

            int fileCounter = 0;
            for (MoveDataDescriptor moveTask : resultDataDesc) {
                WithMovableResultAux.logger.info("Moving data ( " + moveTask.objectName + ") from " + moveTask.source + " to " + moveTask.dest);
                Path objOutputPath = new Path(moveTask.dest);
                try {
                    if (ctx.fs.exists(objOutputPath)) {
                        if (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) {
                            FileStatus[] list = ctx.fs.listStatus(objOutputPath);
                            for (int i = 0; i < list.length; i++) {
                                ctx.fs.delete(list[i].getPath(), true);
                            }
                        }
                    } else {
                        WithMovableResultAux.logger.warn("Output directory: " + objOutputPath + " for data object: " + moveTask.objectName + " does not Exists -- create it");
                        ctx.fs.mkdirs(objOutputPath);
                    }
                } catch (IOException e) {
                    WithMovableResultAux.logger.warn("IO exception in attempt to create/clean up: destination directory", e);
                    return -1;
                }
                //If output files are PARQUET files - clean up temp. directory - remove
                // _metadata and _common_? files.
                if (moveTask.format.equalsIgnoreCase(DLDataSetOperations.FORMAT_PARQUET)) {
                    HDirOperations.cleanupDataDirectory(moveTask.source);
                } else if (moveTask.format.equalsIgnoreCase(DLDataSetOperations.FORMAT_JSON)) {
                }

                //get list of files to be processed
                FileStatus[] files = ctx.fs.listStatus(new Path(moveTask.source));

                WithMovableResultAux.logger.debug("Prepare the list of the files, number of files: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getLen() > 0) {
                        String srcFileName = moveTask.source + Path.SEPARATOR + files[i].getPath().getName();
                        //Move data files with new name to output location
                        String destFileName = generateOutputFileName(ctx, moveTask, fileCounter);
                        Path dest = new Path(destFileName);
                        WithMovableResultAux.logger.debug(String.format("Move from: %s to %s", srcFileName, dest.toString()));
                        Options.Rename opt = (moveTask.mode.equalsIgnoreCase(DLDataSetOperations.MODE_REPLACE)) ? Options.Rename.OVERWRITE : Options.Rename.NONE;
                        Path src = new Path(srcFileName);
                        Path dst = new Path(destFileName);
                        ctx.fc.rename(src, dst, opt);
                    }
                    fileCounter++;
                }
                WithMovableResultAux.logger.debug("Remove TMP directory if it Exists: " + moveTask.source);
                Path tmpDIR = new Path(moveTask.source);
                if (ctx.fs.exists(tmpDIR))
                    ctx.fs.delete(tmpDIR, true);
                WithMovableResultAux.logger.debug("Data Objects were successfully moved from " + moveTask.source + " into " + moveTask.dest);
            } //<-- for
            //outputJson.setSuccessStatus();
            return 0;
        }
        catch(IOException e){
            WithMovableResultAux.logger.error("IO exception during move operation, cancel moving stage: ", e);
        }
        catch(Exception e){
            WithMovableResultAux.logger.error("Exception during move operation, cancel moving stage: ", e);
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

        {
            mode = DLDataSetOperations.FORMAT_PARQUET;
            format = DLDataSetOperations.FORMAT_JSON;
        }

        public MoveDataDescriptor(String src,
                                  String dest,
                                  String objectName,
                                  String mode,
                                  String format) {
            this.source = src;
            this.dest = dest;
            this.mode = mode.toLowerCase();
            this.format = format.toLowerCase();
            this.objectName = objectName;
        }
    }

    class WithMovableResultAux {
        private static final Logger logger = Logger.getLogger(WithMovableResult.class);
   }

}
