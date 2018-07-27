package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToTimestampFromStringAsString implements UDF2<String, String, String>, Serializable {

    @Override
    public String call(String date, String time) throws Exception {
        DateTimeFormatter dfrm = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime ldt = LocalDateTime.parse((date + " " + time), dfrm);
        return ldt.toString();
    }
}
