package synchronoss.spark.rt.common;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.spark.SparkContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asor0002 on 7/21/2016.
 * Implement actions related to monitoring
 */
public class AppMonitoringInfo {
    private static final Logger logger = Logger.getLogger(AppMonitoringInfo.class);

    private static org.apache.hadoop.conf.Configuration fsConf = new org.apache.hadoop.conf.Configuration();
    private static String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    // Store process information in file
    public static int store(SparkContext sc,
                            String instanceName,
                            String controlFilePath,
                            com.typesafe.config.Config appConfig) {

        try {
            // Should be local FS, not HDFS
            FileSystem fs = FileSystem.getLocal(fsConf);
            // Extract process topic
            String stream = appConfig.getString("streams.topic").split(":")[0];
            String fullPath = controlFilePath; //+ File.separator + instanceName + ".pid";
            logger.info("Writing application monitoring data into " + fullPath);

            // Create/rewrite file
            Path path = new Path(fullPath);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fs.create(path, true)));

            // PID
            br.write(pid);
            br.newLine();
            // Stream
            br.write(stream);
            br.newLine();
            // Start timestamp
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ");
            br.write(formatter.format(new Date()));
            br.newLine();
            // Instance name/id
            br.write(instanceName);
            br.newLine();
            // Spark application ID
            br.write(sc.applicationId());
            br.newLine();
            // Spark master connection string
            br.write(sc.getConf().get("spark.master"));
            br.newLine();
            br.close();

            // Make sure file will be cleaned up on exit
            fs.deleteOnExit(path);
        } catch(Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    // Check if shutdown request has been issued
    public static boolean checkForShutdown(String controlFileLocation, String instanceName){
        String fullPath = controlFileLocation + /* File.separator + instanceName +  "." + pid +*/ ".stop";
        try {
            // Should be local FS, not HDFS
            FileSystem fs = FileSystem.getLocal(fsConf);
            // Will return false if file doesn't exists
            return fs.deleteOnExit(new Path(fullPath));
        } catch(Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
