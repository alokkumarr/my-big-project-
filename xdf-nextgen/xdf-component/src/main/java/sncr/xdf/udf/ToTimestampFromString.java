package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF2;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToTimestampFromString implements UDF2<String, String, Timestamp>, Serializable {

    @Override
    public Timestamp call(String date, String time) throws Exception {
        DateTimeFormatter dfrm = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime ldt = LocalDateTime.parse((date + " " + time), dfrm);
        return java.sql.Timestamp.valueOf(ldt);
    }
}
