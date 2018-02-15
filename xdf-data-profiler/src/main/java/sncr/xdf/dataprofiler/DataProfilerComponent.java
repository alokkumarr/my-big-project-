package sncr.xdf.dataprofiler;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.util.LongAccumulator;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithSparkContext;
import sncr.bda.datasets.conf.DataSetProperties;

import java.util.Map;

public class DataProfilerComponent extends Component implements WithSparkContext, WithDataSetService {
    private static final Logger logger = Logger.getLogger(DataProfilerComponent.class);


    public static void main(String[] args){
        DataProfilerComponent component = new DataProfilerComponent();
        try {
            // Spark based component
            if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            System.exit(-1);
        }

    }
/*
    public static int main(String config, String app, String batch){

        DataProfilerComponent component = new DataProfilerComponent();
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(CliHandler.OPTIONS.CONFIG.name(), config);
            parameters.put(CliHandler.OPTIONS.APP_ID.name(), app);
            parameters.put(CliHandler.OPTIONS.BATCH_ID.name(), batch);

            // Spark based component
            if (component.init(config, app, batch, null ) == 0) {
                return component.Run();
            } else {
                return -2;
            }
        } catch (Exception e){
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
*/
    public DataProfilerComponent(){
        componentName = "DataProfiler";
    }

    // Wanted to int it without cli options

    protected int Execute(){
        try {
            // Resolve physical PhysicalLocation
            if (ctx.componentConfiguration.getInputs() != null &&
                    !ctx.componentConfiguration.getInputs().isEmpty())
                inputDataSets = resolveDataParametersWithInput(dsaux);

            // Run component logic here
            // ...
            // Read input file
            if(inputDataSets != null){
                String fieldSeparator = ctx.componentConfiguration.getAnalyzer().getFieldSeparator();
                //TODO:: Get correct keys
                Map<String, String> ds1 = inputDataSets.get((String) inputDataSets.keySet().toArray()[0]);
                Dataset<String> src = ctx.sparkSession.read().textFile(ds1.get(DataSetProperties.PhysicalLocation));
                src.show(10);
                src.printSchema();

                LongAccumulator maxFieldCount = ctx.sparkSession.sparkContext().longAccumulator();
                maxFieldCount.setValue(0L);
                LongAccumulator minFieldCount = ctx.sparkSession.sparkContext().longAccumulator();
                minFieldCount.setValue(Long.MAX_VALUE);
                JavaRDD<Row> records = src.toJavaRDD().map(new ParseString(maxFieldCount, minFieldCount));
                Long cnt = records.count();

                System.out.println("Processed : " + cnt);
                Double fieldsPerRecord = maxFieldCount.value().doubleValue() / cnt.doubleValue();
                Double fieldsPerRecordReminder = maxFieldCount.value().doubleValue() - Math.round(maxFieldCount.value().doubleValue());
                System.out.println("Fields per record : " + fieldsPerRecord + "(" + fieldsPerRecordReminder + ")");

            }

        } catch(Exception e){
            e.printStackTrace();
            return -1;
        }




        return 0;
    }
    protected int Move(){
        return 0;
    }
    protected int Archive(){
        return 0;
    }


    @Override
    protected String mkConfString() {
        return "Zero Component does not have specific parameters";
    }

    public String toString(){
        return componentName;
    }

}
