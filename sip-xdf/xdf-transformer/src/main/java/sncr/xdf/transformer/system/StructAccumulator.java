package sncr.xdf.transformer.system;

import org.apache.log4j.Logger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.util.AccumulatorV2;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srya0001 on 1/8/2018.
 */
public class StructAccumulator extends AccumulatorV2<Tuple2<String,StructField>,Map<String,StructField>> {

    private static final Logger logger = Logger.getLogger(StructAccumulator.class);

    private Map<String, StructField> a = new HashMap<>();;

    @Override
    public boolean isZero() {
        return a.isEmpty();
    }

    private void set(Map<String, StructField> map){a = map;}

    private Map<String, StructField> get(){ return a; }

    @Override
    public AccumulatorV2<Tuple2<String, StructField>, Map<String, StructField>> copy() {
        StructAccumulator newAcc = new StructAccumulator();
        Map<String, StructField> b = new HashMap<>();
        a.forEach(b::put);
        newAcc.set(b);
        return newAcc;
    }

    @Override
    public void reset() {
        a.clear();
    }

    @Override
    public void add(Tuple2<String, StructField> v) {
        if (a.containsKey(v._1())){
            //TODO:: Make sure sameType works as expected.
            if ( v._2().dataType().toString().equalsIgnoreCase(a.get(v._1()).dataType().toString()) ) {
                logger.debug(String.format("Add: f = %s,  t = %s already exists in schema accumulator",
                        v._1(),
                        v._2().dataType().toString()));
                return;
            }
            else{
                //Only in case of NullType we need to replace current DataType with new one
                if ( a.get(v._1()).dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString()) ||
                     v._2().dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString())) {
                    if (a.get(v._1()).dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString())) {
                        logger.debug("Replace in schema accumulator: f = " + v._1() + ",  t = " + v._2().dataType().toString());
                        a.put(v._1(), v._2());
                    } else {
                        logger.debug("Keep non-Null data type in schema accumulator: f = " + v._1() + ",  t = " + a.get(v._1()).dataType().toString());
                    }
                }
                else{
                    throw new IllegalArgumentException(
                    String.format("Struct cannot contain one field [%s] with two different data types " +
                                    " existing field: %s new field: %s ",
                            v._1(),
                            a.get(v._1()).dataType().toString(),
                            v._2().dataType().toString()));
                }
            }
        }
        else{
            logger.debug("Added to schema accumulator: f = " + v._1() + ",  t = " +  v._2().dataType().toString());
            a.put(v._1(), v._2());
        }
    }


    @Override
    public void merge(AccumulatorV2<Tuple2<String, StructField>, Map<String, StructField>> other) {
        StructAccumulator sacc = (StructAccumulator) other;
        sacc.get().forEach( (k, v) -> {
            if (a.containsKey(k)){
                if (v.dataType().toString().equalsIgnoreCase(a.get(k).dataType().toString())) {
                    logger.debug(String.format("f = %s,  t = %s already exists in schema accumulator", k, v.dataType().toString()));
                }
                else {
                    if ( a.get(k).dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString()) ||
                                v.dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString()))
                    {
                        if ( !a.get(k).dataType().toString().equalsIgnoreCase(DataTypes.NullType.toString())) {
                            logger.debug("Keep non-Null data type in schema accumulator: f = " + k + ",  t = " + a.get(k).dataType().toString());
                        }
                        else {
                            logger.debug("Replace data type in schema accumulator: f = " + k + ",  t = " + v.dataType().toString());
                            a.put(k, v);
                        }
                    }
                    else{

                        throw new IllegalArgumentException(
                                String.format("Struct cannot contain one field [%s] with two different data types " +
                                        " existing field: %s new field: %s", k, a.get(k).dataType().toString(), v.dataType().toString()));
                    }
                }
            }
            else{
                logger.debug("Added at merge to schema accumulator: f = " + k + ",  t = " +  v.dataType().toString());
                a.put(k, v);
            }
        });
    }

    @Override
    public Map<String, StructField> value() {
        return a;
    }
}
