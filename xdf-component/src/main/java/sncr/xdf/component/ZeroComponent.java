package sncr.xdf.component;

import org.apache.log4j.Logger;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;

import java.util.Map;

/**
 * Created by asor0002 on 9/11/2017.
 * ZeroComponent is simplest component.
 * It does not do any transformation
 */
public class ZeroComponent extends Component implements WithMovableResult , WithSparkContext, WithDataSetService {

    private static final Logger logger = Logger.getLogger(ZeroComponent.class);

    {
        componentName = "Zero";
    }

    public static void main(String[] args){
        ZeroComponent component = new ZeroComponent();
        try {
            // Spark based component
            if (component.collectCMDParameters(args) == 0) {
                int r = component.Run();
                System.exit(r);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    protected int Execute(){
        logger.info("Zero component does not do transformations! It only moves (copy) input dataset to output dataset");
        return 0;
    }

    @Override
    protected int Move(){

        if (inputDataSets == null || inputDataSets.isEmpty())
        {
            logger.warn("Component does not produce any Data Sets");
            return 0;
        }

        String inKey  = (String) inputDataSets.keySet().toArray()[0];
        String outKey  = (String) outputDataSets.keySet().toArray()[0];
        Map<String, Object> inDesc = inputDataSets.get(inKey);
        Map<String, Object> outDesc = outputDataSets.get(outKey);

        MoveDataDescriptor desc = new MoveDataDescriptor(
                (String)inDesc.get(DataSetProperties.PhysicalLocation.name()),
                (String) outDesc.get(DataSetProperties.PhysicalLocation.name()),
                (String) outDesc.get(DataSetProperties.Name.name()),
                (String) outDesc.get(DataSetProperties.Mode.name()),
                (String) outDesc.get(DataSetProperties.Format.name()),
                null);
        resultDataDesc.add(desc);
        logger.debug(String.format("DataSet %s will be moved from %s to %s, format: %s, mode: %s",
            inDesc.get(DataSetProperties.Name.name()),
            inDesc.get(DataSetProperties.PhysicalLocation.name()),
            outDesc.get(DataSetProperties.PhysicalLocation.name()),
            outDesc.get(DataSetProperties.Format.name()),
            outDesc.get(DataSetProperties.Mode.name())));

        return super.Move();
    }

    protected int Archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return ZeroComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {
        ComponentConfiguration cfg = Component.analyzeAndValidate(cfgAsStr);

        logger.trace("Component configuration: " + cfg.toString());
        if (cfg.getInputs() == null || cfg.getInputs().size() != 1 ){
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Zero component must have exactly one input");
        }
        if (cfg.getOutputs() == null || cfg.getOutputs().size() != 1 ){
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Zero component must have exactly one output");
        }
        return cfg;
    }

    @Override
    protected String mkConfString() {
        return "Zero Component does not have specific parameters";
    }
}
