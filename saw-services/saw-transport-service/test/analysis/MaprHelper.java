import java.io.*;
import java.net.*;
import java.lang.reflect.*;

/**
 * Utility to add the MapR classpath to test runner.  Required for
 * MapR-DB client connections when running tests.
 */
public class MaprHelper {
    private static boolean initialized = false;

    public static void addClasspath() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            /* Note: Currently the classpath entries are listed
             * explicitly below.  Consider instead executing the "mapr
             * classpath" and parsing the output to ensure changes are
             * picked up */
            addPath("/opt/mapr/conf");
            addPath("/opt/mapr/lib/maprbuildversion-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/maprfs-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/maprfs-diagnostic-tools-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/maprdb-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/maprdb-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/maprdb-mapreduce-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/maprdb-mapreduce-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/maprdb-shell-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/mapr-hbase-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/mapr-hbase-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/mapr-java-utils-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/mapr-streams-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/mapr-streams-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/mapr-tools-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/mapr-tools-5.2.0-mapr-tests.jar");
            addPath("/opt/mapr/lib/slf4j-api-1.7.12.jar");
            /* Disable log4j provider to avoid multiple bindings */
            //addPath("/opt/mapr/lib/slf4j-log4j12-1.7.12.jar");
            addPath("/opt/mapr/lib/log4j-1.2.17.jar");
            addPath("/opt/mapr/lib/central-logging-5.2.0-mapr.jar");
            addPath("/opt/mapr/lib/antlr4-runtime-4.5.jar");
            addPath("/opt/mapr/lib/guava-14.0.1.jar");
            addPath("/opt/mapr/lib/jackson-annotations-2.7.2.jar");
            addPath("/opt/mapr/lib/jackson-core-2.7.2.jar");
            addPath("/opt/mapr/lib/jackson-databind-2.7.2.jar");
            addPath("/opt/mapr/lib/jline-2.11.jar");
            addPath("/opt/mapr/lib/ojai-1.1.jar");
            addPath("/opt/mapr/lib/ojai-mapreduce-1.1.jar");
            addPath("/opt/mapr/lib/protobuf-java-2.5.0.jar");
            addPath("/opt/mapr/lib/spring-asm-3.0.3.RELEASE.jar");
            addPath("/opt/mapr/lib/spring-beans-3.0.3.RELEASE.jar");
            addPath("/opt/mapr/lib/spring-context-3.0.3.RELEASE.jar");
            addPath("/opt/mapr/lib/spring-core-3.0.3.RELEASE.jar");
            addPath("/opt/mapr/lib/spring-expression-3.0.3.RELEASE.jar");
            addPath("/opt/mapr/lib/spring-shell-1.2.0.M1-mapr-1607.jar");
            addPath("/opt/mapr/lib/zookeeper-3.4.5-mapr-1604.jar");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/lib/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs/lib/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/yarn/lib/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/yarn/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/mapreduce/lib/*");
            addPath("/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/mapreduce/*");
            addPath("/contrib/capacity-scheduler/*.jar");
            addPath("/opt/mapr/lib/kvstore*.jar");
            addPath("/opt/mapr/lib/libprotodefs*.jar");
            addPath("/opt/mapr/lib/baseutils*.jar");
            addPath("/opt/mapr/lib/maprutil*.jar");
            addPath("/opt/mapr/lib/json-20080701.jar");
            addPath("/opt/mapr/lib/flexjson-2.1.jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader =
            (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u.toURL());
    }
}
