package sncr.bda;

import org.junit.Before;
import org.junit.Test;
import sncr.bda.conf.ComponentConfiguration;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Created by skbm0001 on 24/1/2018.
 */
public class ConfigLoaderTest {
    String configuration;
    String configFile;

    @Before
    public void setUp() {
        configFile = "qaconfig.json";
        File f = new File(configFile);
        System.out.println(f.getAbsolutePath());

        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            String line;
            StringBuilder build = new StringBuilder();
            while((line=buf.readLine()) != null) {
                build.append(line).append("\n");
            }
            System.out.println(build.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String destinationIndexName = "index1";
//        String indexMappingFile = "file:///path/to/file.json";
//        String documentIDField = "field1";
    }

    @Test
    public void parseConfigurationTest() throws Exception {

        String configuration1 = ConfigLoader.loadConfiguration("file:///qaconfig.json");
        ComponentConfiguration config = ConfigLoader.parseConfiguration(configuration1);

        assertEquals(true, config.getEsLoader() != null);
        assertEquals("elasticsearch", config.getEsLoader().getEsClusterName());
    }

}