package sncr.xdf.sql;

import org.apache.log4j.Logger;
import sncr.xdf.component.WithMovableResult;

/**
 * Created by srya0001 on 6/23/2017.
 */
public class SQLMoveDataDescriptor extends WithMovableResult.MoveDataDescriptor {

    public SQLMoveDataDescriptor(SQLDescriptor descriptor, String destDir) {
        super(descriptor.targetTransactionalLocation,
              descriptor.location,
              descriptor.targetObjectName,
              descriptor.tableDescriptor.mode.toLowerCase(),
              descriptor.tableDescriptor.format.toLowerCase()
              );
        this.dest = destDir;
    }

}
