package sncr.bda;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sncr.bda.conf.ComponentConfiguration;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Created by skbm0001 on 24/1/2018.
 */
@Ignore
public class ConfigLoaderTest {
    String configuration;
    String configFile;

    // Config values
    String clusterName;
    String host;
    int port;
    String user;
    String password;
    String indexName;

    @Before
    public void setUp() {
        configFile = "qaconfig.json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(configFile).getFile());
        configuration = ConfigLoader.loadConfiguration("file:///" + file.getAbsolutePath());

        clusterName = "elasticsearch";
        host = "es01.sncr.dev.sncorp.net";
        port = 9100;
        user = "esuser";
        password = "esuser";
        indexName = "index1";
    }

    @Test
    public void parseConfigurationTest() throws Exception {
        ComponentConfiguration config = ConfigLoader.parseConfiguration(configuration);

        assertEquals(true, config.getEsLoader() != null);
        assertEquals(clusterName, config.getEsLoader().getEsClusterName());
        assertEquals(host, config.getEsLoader().getEsNodes());
        assertEquals(port, config.getEsLoader().getEsPort());
        assertEquals(user, config.getEsLoader().getEsUser());
        assertEquals(password, config.getEsLoader().getEsPass());
        assertEquals(indexName, config.getEsLoader().getDestinationIndexName());
    }

}