package sncr.xdf.esreader;

import org.apache.spark.sql.Dataset;

import sncr.xdf.exceptions.XDFException.ErrorCodes;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.ngcomponent.WithSpark;

public class NGESReaderComponent extends AbstractComponent implements WithSpark {
    @Override
    protected int execute() {
        return 0;
    }

    @Override
    protected int archive() {
        return 0;
    }

	@Override
	protected int execute(Dataset df) {
		return ErrorCodes.IncorrectCall.ordinal();
	}


}
