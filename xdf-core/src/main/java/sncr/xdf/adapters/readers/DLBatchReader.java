package sncr.xdf.adapters.readers;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import scala.Tuple2;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;

public class DLBatchReader {
    private static final Logger logger = Logger.getLogger(DLBatchReader.class);
    private final NGContext ngctx;

    public DLBatchReader(NGContext ngctx){ this.ngctx = ngctx;}


    public Dataset readDataset(String name, String format, String loc_desc) throws Exception {
        Dataset ds;
        switch ( format.toLowerCase() )
        {
            case "parquet" :
                try{
                    ds = ngctx.sparkSession.read().load(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as parquet: " + loc_desc + ", cancel processing.");
                }
                break;
            case "json" :
                try{
                    ds = ngctx.sparkSession.read().json(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as JSON: " + loc_desc + ", cancel processing.");
                }
                break;
            default:
                throw new XDFException( XDFException.ErrorCodes.UnsupportedDataFormat);
        }
        ngctx.registerDatset(name, ds);
        return ds;
    }


    public JavaRDD<String> readToRDD(String location, int headerSize){

        JavaRDD<String> rdd = null;

        if (headerSize > 0)
            rdd = new JavaSparkContext(ngctx.sparkSession.sparkContext())
                    .textFile(location, ngctx.defaultPartNumber)
                    // Add line numbers
                    .zipWithIndex()
                    // Filter out header based on line number
                    .filter(new HeaderFilter(headerSize))
                    // Get rid of file numbers
                    .keys();
        else
            rdd = new JavaSparkContext(ngctx.sparkSession.sparkContext())
                    .textFile(location, ngctx.defaultPartNumber);

        ngctx.registerInputDatset(rdd);
        return rdd;
    }



    public class HeaderFilter implements Function<Tuple2<String, Long>, Boolean> {

        Integer headerSize;
        public HeaderFilter(Integer headerSize){
            this.headerSize = headerSize;
        }
        public Boolean call(Tuple2<String, Long> in){
            return in._2() >= headerSize;
        }
    }

}
