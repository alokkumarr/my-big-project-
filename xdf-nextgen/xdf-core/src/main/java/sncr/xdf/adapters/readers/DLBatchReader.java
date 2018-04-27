package sncr.xdf.adapters.readers;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import sncr.xdf.context.InternalContext;
import sncr.xdf.exceptions.XDFException;

public class DLBatchReader {
    private static final Logger logger = Logger.getLogger(DLBatchReader.class);
    private final InternalContext inctx;

    public DLBatchReader(InternalContext inctx){ this.inctx = inctx;}


    public Dataset readDataset(String name, String format, String loc_desc) throws Exception {
        Dataset ds;
        switch ( format.toLowerCase() )
        {
            case "parquet" :
                try{
                    ds = inctx.sparkSession.read().load(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as parquet: " + loc_desc + ", cancel processing.");
                }
                break;
            case "json" :
                try{
                    ds = inctx.sparkSession.read().json(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as JSON: " + loc_desc + ", cancel processing.");
                }
                break;
            default:
                throw new XDFException( XDFException.ErrorCodes.UnsupportedDataFormat);
        }
        inctx.registerDataset(name, ds);
        return ds;
    }


    public JavaRDD<String> readToRDD(String location, int headerSize){

        logger.debug("Read data from location: " + location);
        JavaRDD<String> rdd = null;

        if (headerSize > 0)
            rdd = new JavaSparkContext(inctx.sparkSession.sparkContext())
                    .textFile(location, inctx.defaultPartNumber)
                    // Add line numbers
                    .zipWithIndex()
                    // Filter out header based on line number
                    .filter(new HeaderFilter(headerSize))
                    // Get rid of file numbers
                    .keys();
        else
            rdd = new JavaSparkContext(inctx.sparkSession.sparkContext())
                    .textFile(location, inctx.defaultPartNumber);

        return rdd;
    }




}
