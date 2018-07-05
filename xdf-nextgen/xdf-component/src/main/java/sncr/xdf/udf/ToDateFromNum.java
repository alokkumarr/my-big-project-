package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF1;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToDateFromNum implements UDF1<Long, Date>, Serializable
{
    @Override
    public java.sql.Date call(java.lang.Long i) throws Exception {
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate t = LocalDate.parse(String.valueOf(i), frm);
        return Date.valueOf(t);
    }

}
