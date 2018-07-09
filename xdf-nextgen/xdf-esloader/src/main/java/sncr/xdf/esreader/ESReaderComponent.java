package sncr.xdf.esreader;

import sncr.xdf.component.Component;
import sncr.xdf.component.WithSparkContext;

/**
 * Created by skbm0001 on 31/1/2018.
 */
public class ESReaderComponent extends Component implements WithSparkContext {
    @Override
    protected int execute() {
        return 0;
    }

    @Override
    protected int archive() {
        return 0;
    }

    @Override
    protected String mkConfString() {
        return null;
    }
}
