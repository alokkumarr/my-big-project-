package sncr.xdf.dataprofiler;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.util.LongAccumulator;

public class ParseString implements Function<String, Row> {
    private LongAccumulator maxFieldCount;
    private LongAccumulator minFieldCount;

    public ParseString(LongAccumulator maxFieldCount, LongAccumulator minFieldCount){
        this.maxFieldCount = maxFieldCount;
        this.minFieldCount = minFieldCount;

    }
    public Row call(String in){

        CsvParserSettings settings = new CsvParserSettings();

        CsvParser parser = new CsvParser(settings);
        String[] line = parser.parseLine(in);

        System.out.println("in = " + in);

        for(int i = 0; i < line.length; i++){
            System.out.println("=======> " + line[i]);
        }

        System.out.println("=================================" + line.length);

        maxFieldCount.add(line.length);

        Row r = RowFactory.create(line);
        //Row r = RowFactory.create();
        return r;
    }

}
