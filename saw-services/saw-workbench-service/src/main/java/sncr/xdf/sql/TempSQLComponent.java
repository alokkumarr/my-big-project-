package sncr.xdf.sql;

import java.util.List;
import java.util.Map;

import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.component.WithMovableResult;
import sncr.xdf.sql.ng.NGSQLMoveDataDescriptor;

/**
 * Temporary SQL component that uses NGSQLMoveDataDescriptor to work
 * around path error.  To be removed when NGSQLComponent taken into
 * use.
 */
public class TempSQLComponent extends SQLComponent {
    @Override
    protected int Move() {
        if (executor.getResultDataSets() == null
            || executor.getResultDataSets().size() == 0) {
            return 0;
        }
        Map<String, SQLDescriptor> resultDataSets =
            executor.getResultDataSets();
        outputDataSets.forEach(
            (on, obDesc) -> {
                List<String> kl = (List<String>) obDesc.get(
                    DataSetProperties.PartitionKeys.name());
                String partKeys = on + ": ";
                for (String s : kl) {
                    partKeys += s + " ";
                }
                MoveDataDescriptor desc = new NGSQLMoveDataDescriptor(
                    resultDataSets.get(on),
                    (String) obDesc.get(
                        DataSetProperties.PhysicalLocation.name()), kl);
                resultDataDesc.add(desc);
            });
        int ret = 0;
        if (this instanceof WithMovableResult) {
            ret = ((WithMovableResult) this).doMove(ctx, resultDataDesc);
        }
        return ret;
    }
}
