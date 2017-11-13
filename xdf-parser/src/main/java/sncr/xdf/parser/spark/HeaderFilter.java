package sncr.xdf.parser.spark;

import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

public class HeaderFilter implements Function<Tuple2<String, Long>, Boolean> {

    Integer headerSize;
    public HeaderFilter(Integer headerSize){
        this.headerSize = headerSize;
    }
    public Boolean call(Tuple2<String, Long> in){
        return in._2() >= headerSize;
    }
}
