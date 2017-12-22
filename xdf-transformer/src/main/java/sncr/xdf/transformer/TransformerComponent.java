package sncr.xdf.transformer;

import org.apache.log4j.Logger;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;
import sncr.xdf.component.Component;
import sncr.xdf.component.WithDataSetService;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.component.WithSparkContext;
import sncr.xdf.conf.ComponentConfiguration;
import sncr.xdf.conf.Transformer;
import sncr.xdf.exceptions.XDFException;

/**
 * Created by srya0001 on 12/19/2017.
 * The transformer component is designed to execute only intra-record transformation.
 * The transformation should be presented in format recognized by Janino embedded compiler
 * And may have global context stored in-side Janono-code.
 * The component literally transforms rew-by-row using Spark functions.
 * The component DOES NOT PERFORM any multi-record conversion, use Spark SQL XDF Component
 * if you need to make such transformation.
 */
public class TransformerComponent extends Component implements WithMovableResult, WithSparkContext, WithDataSetService{


    private String script;

    private static final Logger logger = Logger.getLogger(TransformerComponent.class);

    {
        componentName = "transformer";
    }

    public static void main(String[] args){
        TransformerComponent component = new TransformerComponent();
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

    protected int Execute(){
        try {
//TODO:: 1. Read expression/scripts, compile it??
//TODO:: 2. Read input dataset
//TODO:: 3. ForEach row in DataSet apply expression/script.


//TODO:: Execute for each rescord
//            Object res = se.evaluate(arguments);

        }
        catch(Exception e){

        }
        return 0;
    }

    protected int Archive(){
        return 0;
    }

    protected ComponentConfiguration validateConfig(String config) throws Exception {
        return TransformerComponent.analyzeAndValidate(config);
    }

    public static ComponentConfiguration analyzeAndValidate(String cfgAsStr) throws Exception {

        ComponentConfiguration compConf = Component.analyzeAndValidate(cfgAsStr);
        Transformer transformerCfg = compConf.getTransformer();
        if (transformerCfg == null)
            throw new XDFException(XDFException.ErrorCodes.NoComponentDescriptor, "transformer");

        if (transformerCfg.getScript() == null || transformerCfg.getScript().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Transformer descriptor does not have script name.");
        }
        if (transformerCfg.getScriptLocation() == null || transformerCfg.getScriptLocation().isEmpty()) {
            throw new XDFException(XDFException.ErrorCodes.ConfigError, "Incorrect configuration: Transformer descriptor does not have script location.");
        }


        return compConf;
    }

    @Override
    protected String mkConfString() {
        return null;
    }



}
