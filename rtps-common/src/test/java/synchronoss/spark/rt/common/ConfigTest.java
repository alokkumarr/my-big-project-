package synchronoss.spark.rt.common;

import com.typesafe.config.ConfigFactory;
import org.apache.spark.SparkConf;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by asor0002 on 7/21/2016.
 * Test cases for configuration initialization
 */
public class ConfigTest {
    @Test
    public void testConfigurationInitializationForSparkConfig() {
        Map<String, Object> map = new HashMap<>(5);
        Map<String, Object> map2 = new HashMap<>(5);
        String value = "value";

        // Passing single level key
        map.put("key1", value);
        // Passing 2 level key
        map2.put("key2-1", value);
        map.put("key2", map2);
        com.typesafe.config.Config appConfig = ConfigFactory.parseMap(map);
        SparkConf sconf = new SparkConf();
        ConfigurationHelper.initConfig(sconf, appConfig, "key1", false);

        String key1 = sconf.get("key1");

        ConfigurationHelper.initConfig(sconf, appConfig, "key2", false);
        String key2 = sconf.get("key2.key2-1");

        assertEquals("Configuration value from 1st level should be propagated to Spark Configuration", key1, value);
        assertEquals("Configuration value from 2nd level should be propagated to Spark Configuration", key2, value);
    }

    @Test
    public void testConfigurationInitializationForSparkConfigCase2() {
        Map<String, Object> map = new HashMap<>(5);
        Map<String, Object> map2 = new HashMap<>(5);
        Map<String, Object> map3 = new HashMap<>(3);
        String value = "value";

        // Passing 2 level key
        map3.put("key2-2-1", value);
        map2.put("key2-1", value);
        map2.put("key2-2", map3);
        map.put("key2", map2);
        com.typesafe.config.Config appConfig = ConfigFactory.parseMap(map);
        SparkConf sconf = new SparkConf();

        // Initialize config with removed prefix (must include '.')
        ConfigurationHelper.initConfig(sconf, appConfig, "key2.", true);
        String key2 = sconf.get("key2-1");
        String key3 = sconf.get("key2-2.key2-2-1");

        assertEquals("Configuration value from 2nd level should be propagated to Spark Configuration", key2, value);
        assertEquals("Configuration value from 3rd level should be propagated to Spark Configuration", key3, value);
    }

    @Test
    public void testConfigurationInitializationForMap() {
        Map<String, Object> map = new HashMap<>(5);
        Map<String, Object> map2 = new HashMap<>(5);
        String value = "value";

        // Passing single level key
        map.put("key1", value);
        // Passing 2 level key
        map2.put("key2-1", value);
        map.put("key2", map2);
        com.typesafe.config.Config appConfig = ConfigFactory.parseMap(map);
        Map<String, String> sconf = new HashMap<>();
        ConfigurationHelper.initConfig(sconf, appConfig, "key1", false);

        String key1 = sconf.get("key1");

        ConfigurationHelper.initConfig(sconf, appConfig, "key2", false);
        String key2 = sconf.get("key2.key2-1");

        assertEquals("Configuration value from 1st level should be propagated to Spark Configuration", key1, value);
        assertEquals("Configuration value from 2nd level should be propagated to Spark Configuration", key2, value);
    }

    @Test
    public void testConfigurationInitializationForMapCase2() {
        Map<String, Object> map = new HashMap<>(5);
        Map<String, Object> map2 = new HashMap<>(5);
        Map<String, Object> map3 = new HashMap<>(3);
        String value = "value";

        // Passing 2 level key
        map3.put("key2-2-1", value);
        map2.put("key2-1", value);
        map2.put("key2-2", map3);
        map.put("key2", map2);
        com.typesafe.config.Config appConfig = ConfigFactory.parseMap(map);
        Map<String, String> sconf = new HashMap<>();

        // Initialize config with removed prefix (must include '.')
        ConfigurationHelper.initConfig(sconf, appConfig, "key2.", true);
        String key2 = sconf.get("key2-1");
        String key3 = sconf.get("key2-2.key2-2-1");

        assertEquals("Configuration value from 2nd level should be propagated to Spark Configuration", key2, value);
        assertEquals("Configuration value from 3rd level should be propagated to Spark Configuration", key3, value);
    }
}
