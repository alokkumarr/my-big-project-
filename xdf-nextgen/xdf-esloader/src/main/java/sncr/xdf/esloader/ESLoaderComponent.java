package sncr.xdf.esloader;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.ESLoader;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.component.Component;
import sncr.xdf.esloader.esloadercommon.ElasticSearchLoader;
import sncr.xdf.exceptions.FatalXDFException;
import sncr.xdf.exceptions.XDFException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by skbm0001 on 29/1/2018.
 */
public class ESLoaderComponent extends Component implements WithSparkContext, WithDataSetService {
    private static final Logger logger = Logger.getLogger(ESLoaderComponent.class);

    //TODO: Remove this
    public static String ESLOADER_DATASET;

    private Map<String, Object> esDataset;
    private String dataSetName;
    private String inputDataFormat;

    ESLoaderComponent() {
        super.componentName = "esloader";
    }

    public static void main(String[] args) {
        ESLoaderComponent component = new ESLoaderComponent();

        try {
            if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected int Execute() {
        int retVal = 0;
        try {
            ESLoader esLoaderConfig = this.ctx.componentConfiguration.getEsLoader();
            if (this.inputDataSets != null && !this.inputDataSets.isEmpty()) {
                ESLOADER_DATASET = this.inputDataSets.keySet().iterator().next();
            }

            esDataset = this.inputDataSets.get(ESLOADER_DATASET);
            logger.debug("ES Dataset = " + esDataset);

            dataSetName = (String)esDataset.get(DataSetProperties.Name.name());
            inputDataFormat = (String)esDataset.get(DataSetProperties.Format.name());

            Map<String, Dataset> dataSetMap = createDatasetMap();

            Dataset<Row> inputDataset = dataSetMap.get(this.dataSetName);


            ElasticSearchLoader loader = new ElasticSearchLoader(this.ctx.sparkSession, esLoaderConfig);

            loader.loadSingleObject(this.dataSetName, inputDataset, inputDataFormat);

            return 0;
        } catch (Exception ex) {
            logger.error(ex);
            logger.debug(ExceptionUtils.getStackTrace(ex));
            retVal = -1;
        }

        return retVal;
    }

    @Override
    protected int Archive() {
        return 0;
    }

    @Override
    protected String mkConfString() {
        return null;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return ESLoaderComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);
        ESLoader esLoaderConfig = compConf.getEsLoader();
        if (esLoaderConfig == null)
            throw new XDFException(XDFException.ErrorCodes.NoComponentDescriptor, "es-loader");

        if (esLoaderConfig.getEsNodes() == null || esLoaderConfig.getEsNodes().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Nodes configuration missing.");
        }
        if (esLoaderConfig.getEsPort() == 0) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Port configuration missing.");
        }
        if (esLoaderConfig.getDestinationIndexName() == null || esLoaderConfig.getDestinationIndexName().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Destination Index Name missing.");
        }
        if (esLoaderConfig.getEsUser() == null || esLoaderConfig.getEsUser().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Username missing.");
        }
        if (esLoaderConfig.getEsPass() == null || esLoaderConfig.getEsPass().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch Password missing.");
        }
        if (esLoaderConfig.getEsClusterName() == null || esLoaderConfig.getEsClusterName().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: ElasticSearch clustername configuration missing.");
        }

        return compConf;
    }

    private Map<String, Dataset> createDatasetMap() {
        Map<String, Dataset> dataSetmap = new HashMap();

        for ( Map.Entry<String, Map<String, Object>> entry : this.inputDataSets.entrySet()) {
            Map<String, Object> desc = entry.getValue();
            String loc = (String)desc.get(DataSetProperties.PhysicalLocation.name());
            String format = (String)desc.get(DataSetProperties.Format.name());
            logger.debug("Physical location = + " + loc + ". Format = " + format);
            Dataset ds = null;
            switch (format.toLowerCase()) {
                case "json":
                    ds = ctx.sparkSession.read().json(loc); break;
                case "parquet":
                    ds = ctx.sparkSession.read().parquet(loc); break;
                default:
                    error = "Unsupported data format: " + format;
                    logger.error(error);
                    throw new FatalXDFException(XDFException.ErrorCodes.UnsupportedDataFormat, -1);
            }
            dataSetmap.put(entry.getKey(), ds);
        }

        return dataSetmap;
    }
}
