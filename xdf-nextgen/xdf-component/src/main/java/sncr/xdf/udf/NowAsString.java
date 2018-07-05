package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class NowAsString implements UDF1<String, String>, Serializable {

    @Override
    public String call(String f1) throws Exception {
        DateTimeFormatter dfrm = DateTimeFormatter.ofPattern(f1);
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.format(dfrm);
    }
}
