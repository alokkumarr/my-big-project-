/opt/mapr/spark/spark-current/bin/spark-shell --conf "spark.driver.extraJavaOptions=-Dderby.system.home=/home/mapr" --master=yarn --executor-memory 2g --total-executor-cores 4

import org.apache.spark._
import org.apache.spark.api.java._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.{GenericRow, GenericRowWithSchema}
import scala.collection.JavaConverters._
import java.sql.Timestamp

val s1 = StructType(StructField("key1", IntegerType, true) ::
StructField("key2", StringType, true) ::
StructField("val1", IntegerType, true) :: 
StructField("val2", StringType, true) :: 
StructField("desc", StringType, true) :: Nil)


val s2 = StructType(StructField("k1", IntegerType, true) ::
StructField("k2", StringType, true) ::
StructField("v1", IntegerType, true) :: 
StructField("v2", StringType, true) :: 
StructField("v3", DoubleType, true) :: Nil)


val a11 = Array( 1, "ref 1",  5, "data1", "description text 1" )
val a12 = Array( 2, "ref 2", 10, "data2", "description text 2" )
val a13 = Array( 3, "ref 3", 15, "data3", "description text 3" )
val a14 = Array( 4, "ref 4", 20, "data4", "description text 4" )
val a15 = Array( 5, "ref 5", 25, "data5", "description text 5" )

val r11: Row = new GenericRowWithSchema(a11, s1)
val r12: Row = new GenericRowWithSchema(a12, s1)
val r13: Row = new GenericRowWithSchema(a13, s1)
val r14: Row = new GenericRowWithSchema(a14, s1)
val r15: Row = new GenericRowWithSchema(a15, s1)

val rs1 = Seq( r11,  r12,  r13,  r14, r15 )
val rdd1 = spark.sparkContext.makeRDD[Row](rs1, 1)
val jrdd1 = new JavaRDD[Row](rdd1)
val df1 = spark.createDataFrame(jrdd1, s1)
df1.cache
df1.show
val ls = "hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dinp/TRREF01/data"
df1.write.parquet(ls)

val a21 = Array( 1, "id1",  5, "ref 11", 34.11 )
val a22 = Array( 2, "id2",  6, "ref 12", 31.22 )
val a23 = Array( 3, "id3",  7, "ref 13", 33.12 )
val a24 = Array( 4, "id4",  8, "ref 14", 37.68 )
val a25 = Array( 5, "id5",  9, "ref 15", 38.78 )

val r21: Row = new GenericRowWithSchema(a21, s2)
val r22: Row = new GenericRowWithSchema(a22, s2)
val r23: Row = new GenericRowWithSchema(a23, s2)
val r24: Row = new GenericRowWithSchema(a24, s2)
val r25: Row = new GenericRowWithSchema(a25, s2)


val rs2 = Seq( r21,  r22,  r23,  r24, r25 )
val rdd2 = spark.sparkContext.makeRDD[Row](rs2, 1)
val jrdd2 = new JavaRDD[Row](rdd2)
val df2 = spark.createDataFrame(jrdd2, s2)
df2.cache
df2.show
val ls2 = "hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dinp/TRREF02/data"
df2.write.parquet(ls2)


