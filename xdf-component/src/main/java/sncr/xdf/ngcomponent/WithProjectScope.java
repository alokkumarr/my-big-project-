package sncr.xdf.ngcomponent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import sncr.bda.conf.Parameter;
import sncr.bda.metastore.ProjectStore;
import sncr.xdf.context.InternalContext;

import java.util.ArrayList;
import java.util.List;

public interface WithProjectScope {

    default void getProjectData(InternalContext ctx) throws Exception {
        WithProjectScopeHelper.logger.debug("Getting project metadata");
        ProjectStore prjStore = new ProjectStore(ctx.xdfDataRootSys);
        JsonElement prj = prjStore.readProjectData(ctx.applicationID);
        WithProjectScopeHelper.logger.debug("Project metadata for " + ctx.applicationID + " is " + prj);

        JsonObject prjJo = prj.getAsJsonObject();
        JsonElement plp;
        List<Parameter> oldList = ctx.componentConfiguration.getParameters();
        if (prjJo.has(ProjectStore.PLP)) {
            plp = prjJo.get(ProjectStore.PLP);
            JsonArray plpJA = plp.getAsJsonArray();
            List<Parameter> newList = new ArrayList<>();
            plpJA.forEach(plpen -> {
                JsonObject plpJO = plpen.getAsJsonObject();
                Parameter parameter = new Parameter(plpJO.get("name").getAsString(), plpJO.get("value").getAsString());
                WithProjectScopeHelper.logger.debug("Process parameter  " + parameter.getName() + " value: " + parameter.getValue());
                newList.add(parameter);
            });
            for (Parameter pn : newList) {
                final boolean[] found = {false};
                oldList.forEach(po -> {
                    if (pn.getName().equalsIgnoreCase(po.getName())) {
                        found[0] = true;
                        return;
                    }
                });
                if (!found[0]) oldList.add(pn);
            }
        }
    }

    class WithProjectScopeHelper {
        private static final Logger logger = Logger.getLogger(WithDLBatchWriter.class);
    }
}
