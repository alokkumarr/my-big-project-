package sncr.xdf.sql.ng;

import org.apache.hadoop.fs.Path;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.sql.SQLDescriptor;

import java.util.List;

public class NGSQLMoveDataDescriptor extends MoveDataDescriptor {

    public NGSQLMoveDataDescriptor(SQLDescriptor descriptor, String destDir, List<String> keys) {
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
