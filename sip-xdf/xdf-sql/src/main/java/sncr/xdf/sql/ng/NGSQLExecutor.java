package sncr.xdf.sql.ng;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;

import scala.Tuple4;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.ngcomponent.WithDLBatchWriter;
import sncr.xdf.file.DLDataSetOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.TableDescriptor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class NGSQLExecutor implements Serializable {

    private Map<String, Dataset<Row>> jobDataFrames;
    private static final Logger logger = Logger.getLogger(NGSQLExecutor.class);
    private SQLDescriptor descriptor;
    private WithContext parent;


    public NGSQLExecutor(WithContext parent,
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

    public int run(NGSQLScriptDescriptor scriptDescriptor) throws Exception {
        jobDataFrames.forEach((t, df) -> logger.trace("Registered DF so far: " + t ));

        Map<String, TableDescriptor> allTables = scriptDescriptor.getScriptWideTableMap();

        switch (descriptor.statementType) {
            case UNKNOWN:
                break;
            case CREATE:
            case SELECT:
                long st = System.currentTimeMillis();
                descriptor.startTime =  st;
                Dataset<Row> df = null;

                if (parent.getNgctx().inputDataSets.size() > 0) {

                    for (String tn : allTables.keySet()) {

                        TableDescriptor tb = allTables.get(tn);
                        if (tb.isTargetTable) {
                            logger.trace("Do not load data of target table: " + tn);
                            continue;
                        }

                        if (!tb.asReference.contains(descriptor.index)) {
                            logger.trace("Do not load data not needed for this statement: " + tn);
                            continue;
                        }

                        logger.debug("Attempt to load data for table: " + tn);
                        String location;

                        if (!tn.equalsIgnoreCase(parent.getNgctx().dataSetName))
                        {

                            if (allTables.get(tn) != null) {
                                location = allTables.get(tn).getLocation();
                            } else {
                                logger.error("Could not get data location from table descriptor, cancel processing");
                                return -1;
                            }

                            if (location == null || location.isEmpty()) {
                                logger.error("Data location is Empty, cancel processing");
                                return -1;
                            }

                                logger.debug("Load data from: " + location + ", registered table name: " + tn);

                                if (jobDataFrames.get(tn) != null) {
                                    continue;
                                }

                                //TODO:: Add support to read from Drill partition, but do not add support to write into Drill partitions
                                Tuple4<String, List<String>, Integer, DLDataSetOperations.PARTITION_STRUCTURE> loc_desc =
                                    DLDataSetOperations.getPartitioningInfo(location);

                                if (loc_desc == null)
                                	return -1;
                                   // throw new XDFException(XDFException.ErrorCodes.PartitionCalcError, tn);

                                logger.debug("Final location to be loaded: " + loc_desc._1() + " for table: " + tn);
                                
                                try {
									df = parent.getReader().readDataset(tn, tb.format, loc_desc._1());
								} catch (Exception exception) {
									logger.error("Could not load data neither in parquet nor in JSON, cancel processing " 
											+ exception.getMessage() );
									return -1;
								}
                                
                                if (df == null) {
                                	logger.error("Could not load data neither in parquet nor in JSON, cancel processing");
                                	return -1;
                                   //throw new Exception("Could not load data neither in parquet nor in JSON, cancel processing");
                                }
                                jobDataFrames.put(tn, df);
                                df.createOrReplaceTempView(tn);

                        }
                    }
                }

                if (parent.getNgctx().runningPipeLine)
                {
                	
                	
                	
                	
                    
                    parent.getNgctx().datafileDFmap.forEach((key, value) -> {
                    
                    	parent.getNgctx().datafileDFmap.get(key).createOrReplaceTempView(key);
                        StructField[] fields = parent.getNgctx().datafileDFmap.get(key).schema().fields();
                        Arrays.asList(fields).forEach((x)->logger.info(x));
                        
                	});
                }

                long lt = System.currentTimeMillis();
                descriptor.loadTime = (int)((lt-st)/1000);

                Dataset<Row> sqlResult = parent.getICtx().sparkSession.sql(descriptor.SQL);
                Dataset<Row> finalResult = sqlResult.coalesce(descriptor.tableDescriptor.numberOfFiles);
                
                
                

                WithDLBatchWriter pres = (WithDLBatchWriter) parent;

                jobDataFrames.put(descriptor.targetTableName, finalResult);
                finalResult.createOrReplaceTempView(descriptor.targetTableName);
                logger.debug("SQL execution result:");

                long exet  = System.currentTimeMillis();
                descriptor.executionTime =  (int) ((exet-lt)/1000);

                logger.debug(" ==> Executed SQL: " +  descriptor.SQL + "\n ==> Target temp. file: " + descriptor.transactionalLocation);

                String loc = descriptor.transactionalLocation + Path.SEPARATOR +  descriptor.targetTableName;
                
                logger.debug("### Adding dynamic SQL Dataset with name ::"+ descriptor.targetTableName);
                parent.getNgctx().datafileDFmap.put(descriptor.targetTableName,finalResult);
                
                if(!descriptor.isTemporaryTable) {
                	
                	logger.info("Final result schema :: ");
                	finalResult.printSchema();
                    pres.commitDataSetFromDSMap(parent.getNgctx(), finalResult, descriptor.targetTableName, loc , "append");

                    long wt = System.currentTimeMillis();
                    descriptor.writeTime = (int) ((wt - exet) / 1000);
                    logger.debug(String.format("Elapsed time:  %d , Load time: %d, Execution time: %d, Write time: %d %n%n", (wt - st) / 1000, (lt - st) / 1000, (exet - lt) / 1000, (wt - exet) / 1000));
                }
                return 0;
            case DROP_TABLE:
                HFileOperations.deleteEnt(descriptor.tableDescriptor.getLocation());
                logger.error("Removed data set: " + descriptor.SQL);
                return 0;
            default:
        }
        return 0;
    }

}
