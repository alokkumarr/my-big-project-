package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF2;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */

public class ToTimestampFromNum implements UDF2<Integer, Integer, Timestamp>, Serializable {

    @Override
    public Timestamp call(Integer date, Integer time) throws Exception {
        DateTimeFormatter dfrm = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime ldt = LocalDateTime.parse((date + " " + time), dfrm);
        return Timestamp.valueOf(ldt);
    }
}
