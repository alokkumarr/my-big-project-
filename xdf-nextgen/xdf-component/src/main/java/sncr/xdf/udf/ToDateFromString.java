package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF1;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToDateFromString implements UDF1<String, Date>, Serializable
{
    @Override
    public Date call(String s) throws Exception {
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate t = LocalDate.parse(s, frm);
        return Date.valueOf(t);
    }

}
