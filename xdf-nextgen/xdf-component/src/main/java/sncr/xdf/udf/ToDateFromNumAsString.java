package sncr.xdf.udf;

import org.apache.spark.sql.api.java.UDF1;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by skbm0001 on 7/05/2018.
 */
public class ToDateFromNumAsString implements UDF1<Integer, String>, Serializable
{
    @Override
    public String call(Integer date) throws Exception {
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate t = LocalDate.parse(String.valueOf(date), frm);
        return t.toString();
    }

}
