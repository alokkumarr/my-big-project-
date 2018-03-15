package sncr.xdf.sql.ng;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Tuple4;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.component.WithDLBatchWriter;
import sncr.xdf.core.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.TableDescriptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class NGSQLExecutor implements Serializable {

    private Map<String, Dataset<Row>> jobDataFrames;
    private static final Logger logger = Logger.getLogger(NGSQLExecutor.class);
    private SQLDescriptor descriptor;
    private AbstractComponent parent;


    public NGSQLExecutor(AbstractComponent parent,
                         SQLDescriptor descriptor,
                         Map<String, Dataset<Row>> availableDataframes)
    {
        this.parent = parent;
        this.descriptor = descriptor;
        jobDataFrames = availableDataframes;
    }


    public String getSql() {
        return descriptor.SQL;
    }

    public Long run(NGSQLScriptDescriptor scriptDescriptor) throws Exception {
        jobDataFrames.forEach((t, df) -> logger.trace("Registered DF so far: " + t ));
        Map<String, TableDescriptor> allTables = scriptDescriptor.getScriptWideTableMap();

        switch (descriptor.statementType) {
            case UNKNOWN:
                break;
            case CREATE:
            case SELECT:
                long st = System.currentTimeMillis();
                descriptor.startTime =  st;

                for ( String tn: allTables.keySet()){

                    TableDescriptor tb = allTables.get(tn);
                    if (tb.isTargetTable){
                        logger.trace("Do not load data of target table: " + tn);
                        continue;
                    }

                    if (!tb.asReference.contains(descriptor.index)){
                        logger.trace("Do not load data not needed for this statement: " + tn);
                        continue;
                    }

                    logger.debug ("Attempt to load data for table: " + tn);
                    String location;
                    if (allTables.get(tn) != null){
                        location = allTables.get(tn).getLocation();
                    }
                    else
                    {
                        logger.error("Could not get data location from table descriptor, cancel processing");
                        return -1L;
                    }

                    if (location == null || location.isEmpty())
                    {
                        logger.error("Data location is Empty, cancel processing");
                        return -1L;
                    }

                    if (jobDataFrames.get(tn) != null){
                        continue;
                    }

                    logger.debug("Load data from: " + location  + ", registered table name: " + tn );

                    //TODO:: Add support to read from Drill partition, but do not add support to write into Drill partitions
                    Tuple4<String, List<String>, Integer, DLDataSetOperations.PARTITION_STRUCTURE> loc_desc =
                            DLDataSetOperations.getPartitioningInfo(location);

                    if (loc_desc == null)
                        throw new XDFException(XDFException.ErrorCodes.PartitionCalcError, tn);

                    logger.debug("Final location to be loaded: " + loc_desc._1()  + " for table: " + tn);
                    Dataset<Row> df = null;
                    df = parent.getReader().readDataset(tn, tb.format, loc_desc._1());
                    if (df == null){
                        throw new Exception( "Could not load data neither in parquet nor in JSON, cancel processing");
                    }
                    jobDataFrames.put(tn, df);
                    df.createOrReplaceTempView(tn);
                }

                long lt = System.currentTimeMillis();
                descriptor.loadTime = (int)((lt-st)/1000);

                Dataset<Row> sqlResult = parent.getNgctx().sparkSession.sql(descriptor.SQL);
                Dataset<Row> finalResult = sqlResult.coalesce(descriptor.tableDescriptor.numberOfFiles);

                WithDLBatchWriter pres = (WithDLBatchWriter) parent;
                pres.registerDataset(parent.getNgctx(), finalResult, descriptor.targetTableName);

                jobDataFrames.put(descriptor.targetTableName, finalResult);
                finalResult.createOrReplaceTempView(descriptor.targetTableName);
                logger.debug("SQL execution result:");

                long exet  = System.currentTimeMillis();
                descriptor.executionTime =  (int) ((exet-lt)/1000);

                logger.trace(" ==> Executed SQL: " +  descriptor.SQL + "\n ==> Target temp. file: " + descriptor.targetTransactionalLocation);

                pres.commitDataSetFromDSMap(parent.getNgctx(), finalResult, descriptor.targetTableName, descriptor.targetTransactionalLocation);

                long wt = System.currentTimeMillis();
                descriptor.writeTime = (int) ((wt - exet) / 1000);
                logger.debug(String.format("Elapsed time:  %d , Load time: %d, Execution time: %d, Write time: %d %n%n", (wt-st)/1000, (lt-st)/1000, (exet -lt)/1000, (wt-exet)/1000));
                return 0L;
            case DROP_TABLE:
                HFileOperations.deleteEnt(descriptor.tableDescriptor.getLocation());
                logger.error("Removed data set: " + descriptor.SQL);
                return 0L;
            default:
        }
        return 0L;
    }





}
