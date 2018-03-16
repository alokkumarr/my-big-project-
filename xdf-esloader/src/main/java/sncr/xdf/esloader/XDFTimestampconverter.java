package sncr.xdf.esloader;

import org.apache.spark.sql.api.java.UDF3;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;


/**
 * Created by asor0002 on 3/1/2017.
 *
 */
public class XDFTimestampconverter implements UDF3<String, String, String, String>, Serializable{
    @Override
    public String call(String _xdfDate, String _xdfTime, String fmt) throws Exception {

        // Convert xdfTimestamp to string representation and then to specified format

        String _dt = null;
        String _tm = null;

        // Check if null or empty
        if (_xdfDate == null || _xdfDate.trim().length() == 0) {
            return null;
        } else {
//            _dt = (_xdfDate == null || _xdfDate.isEmpty()) ? "10000101" : _xdfDate;
            _tm = (_xdfTime == null || _xdfTime.isEmpty()) ? "000000" : _xdfTime;
            // Convert
            DateTimeFormatter srcDtfmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter dstDtfmt = DateTimeFormatter.ofPattern(fmt);
            TemporalAccessor t = LocalDateTime.parse(_dt + _tm, srcDtfmt);
            return dstDtfmt.format(t);
        }
    }
}
