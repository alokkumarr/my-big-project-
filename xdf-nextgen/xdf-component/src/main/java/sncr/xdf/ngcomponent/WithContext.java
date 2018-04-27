package sncr.xdf.ngcomponent;

import sncr.xdf.adapters.readers.DLBatchReader;
import sncr.xdf.context.InternalContext;
import sncr.xdf.context.NGContext;

public interface WithContext {

    NGContext getNgctx();
    InternalContext getICtx();
    DLBatchReader getReader();

}
