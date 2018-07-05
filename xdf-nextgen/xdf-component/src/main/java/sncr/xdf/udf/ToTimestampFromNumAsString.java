package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToTimestampFromNumAsString implements UDF2<Long, Long, String>, Serializable {

    @Override
    public String call(Long i1, Long i2) throws Exception {
        DateTimeFormatter dfrm = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime ldt = LocalDateTime.parse((i1 + " " + i2), dfrm);
        return ldt.toString();
    }
}
