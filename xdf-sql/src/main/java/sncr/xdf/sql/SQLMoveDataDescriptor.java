package sncr.xdf.sql;

import sncr.xdf.component.WithMovableResult;

import java.util.List;

/**
 * Created by srya0001 on 6/23/2017.
 */
public class SQLMoveDataDescriptor extends WithMovableResult.MoveDataDescriptor {

    public SQLMoveDataDescriptor(SQLDescriptor descriptor, String destDir, List<String> keys) {
        super(descriptor.targetTransactionalLocation,
              descriptor.location,
              descriptor.targetObjectName,
              descriptor.tableDescriptor.mode.toLowerCase(),
              descriptor.tableDescriptor.format.toLowerCase(),
                keys
              );
        this.dest = destDir;
    }

}
