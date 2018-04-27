package sncr.xdf.esreader;

import sncr.xdf.component.Component;
import sncr.xdf.component.WithSparkContext;

/**
 * Created by skbm0001 on 31/1/2018.
 */
public class ESReaderComponent extends Component implements WithSparkContext {
    @Override
    protected int Execute() {
        return 0;
    }

    @Override
    protected int Archive() {
        return 0;
    }

    @Override
    protected String mkConfString() {
        return null;
    }
}
