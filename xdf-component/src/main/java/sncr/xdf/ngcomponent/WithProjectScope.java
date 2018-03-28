package sncr.xdf.ngcomponent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import sncr.bda.conf.Parameter;
import sncr.bda.metastore.ProjectStore;
import sncr.xdf.context.NGContext;

import java.util.ArrayList;
import java.util.List;

public interface WithProjectScope {

    default void getProjectData(NGContext ngctx) throws Exception {
        WithProjectScopeHelper.logger.debug("Getting project metadata");
        ProjectStore prjStore = new ProjectStore(ngctx.xdfDataRootSys);
        JsonElement prj = prjStore.readProjectData(ngctx.applicationID);
        WithProjectScopeHelper.logger.debug("Project metadata for " + ngctx.applicationID + " is " + prj);

        JsonObject prjJo = prj.getAsJsonObject();
        JsonElement plp;
        List<Parameter> oldList = ngctx.componentConfiguration.getParameters();
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
