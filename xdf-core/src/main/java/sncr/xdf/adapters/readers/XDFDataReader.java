package sncr.xdf.adapters.readers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import sncr.xdf.context.NGContext;
import sncr.xdf.exceptions.XDFException;

public class XDFDataReader {
    private static final Logger logger = Logger.getLogger(XDFDataReader.class);
    private final NGContext ngctx;

    public XDFDataReader(NGContext ngctx){ this.ngctx = ngctx;}


    public Dataset readDataset(String format, String loc_desc) throws Exception {

        switch ( format.toLowerCase() )
        {
            case "parquet" :
                try{
                    return ngctx.sparkSession.read().load(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as parquet: " + loc_desc + ", cancel processing.");
                }
            case "json" :
                try{
                    return ngctx.sparkSession.read().json(loc_desc);
                }
                catch(Throwable t){
                    throw new Exception( "Could not load data from location as JSON: " + loc_desc + ", cancel processing.");
                }
            default:
                throw new XDFException( XDFException.ErrorCodes.UnsupportedDataFormat);
        }
    }

}
