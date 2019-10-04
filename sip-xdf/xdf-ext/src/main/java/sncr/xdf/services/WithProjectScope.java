package sncr.xdf.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import sncr.bda.admin.ProjectAdmin;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.conf.Parameter;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.metastore.ProjectStore;
import sncr.xdf.context.NGContext;

public interface WithProjectScope {
    default void getProjectData(NGContext ngctx) throws Exception {
        WithProjectScopeHelper.logger.trace("Getting project metadata: "+ ngctx.applicationID);
        WithProjectScopeHelper.logger.trace("Getting project xdfDataRootSys: "+ ngctx.xdfDataRootSys);
        ProjectAdmin prjStore = new ProjectAdmin(ngctx.xdfDataRootSys);
        try {
            if (!projectPresent(ngctx.applicationID))
            {
                JsonElement prj = prjStore.readProjectData(ngctx.applicationID);
                WithProjectScopeHelper.logger.trace("getProjectData prj: "+ prj);
                WithProjectScopeHelper.logger.trace("prj: " + prj.toString());
                WithProjectScopeHelper.logger.trace("Project metadata for " + ngctx.applicationID + " is " + prj);
                JsonObject prjJo = prj.getAsJsonObject();
                JsonElement plp;
                List<Parameter> oldList = ngctx.componentConfiguration.getParameters();
                if (prjJo.has(ProjectStore.PLP)) {
                    plp = prjJo.get(ProjectStore.PLP);
                    WithProjectScopeHelper.logger.trace("plp prj: "+ plp.toString());
                    JsonArray plpJA = plp.getAsJsonArray();
                    WithProjectScopeHelper.logger.trace("plpJA prj: "+ plpJA.toString());
                    List<Parameter> newList = new ArrayList<>();
                    plpJA.forEach(plpen -> {
                        JsonObject plpJO = plpen.getAsJsonObject();
                        Parameter parameter = new Parameter(plpJO.get("name").getAsString(), plpJO.get("value").getAsString());
                        WithProjectScopeHelper.logger.trace("Process parameter  " + parameter.getName() + " value: " + parameter.getValue());
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
            else {
                WithProjectScopeHelper.logger.trace("else block adding default project.");
                addDefaultProject(ngctx);
            }

        }
        catch (Exception ex) {
            WithProjectScopeHelper.logger.error(ExceptionUtils.getFullStackTrace(ex));
            WithProjectScopeHelper.logger.trace("Project metadata for " + ngctx.applicationID + " is not found ");
            addDefaultProject(ngctx);
        }

    }

    // Default implementation of project if it is not registered with the Id
    default void addDefaultProject(NGContext ngctx) throws Exception {
        WithProjectScopeHelper.logger.warn("creating a default project metadata starts here");
        WithProjectScopeHelper.logger.warn("creating a default project id: " + ngctx.applicationID);
        WithProjectScopeHelper.logger.warn("creating a default project root: " + ngctx.xdfDataRootSys);
        JsonObject root = new JsonObject();
        root.addProperty("action", "create");
        root.addProperty("category", "Project");
        root.addProperty("id", ngctx.applicationID);
        root.addProperty("xdf-root", ngctx.xdfDataRootSys);
        if (!(HFileOperations.exists("/var/sip/xdf-ng-default/logs"))) {
            HFileOperations.createDir("/var/sip/xdf-ng-default/logs");
        }
        root.addProperty("output", "/var/sip/xdf-ng-default/logs/xdf-ng-default.log");
        JsonObject source = new JsonObject();
        source.addProperty("description", "Adding a default project with id " + ngctx.applicationID );
        JsonArray parameters = new JsonArray();

        JsonObject p1 = new JsonObject();
        p1.addProperty("name", "spark.master");
        p1.addProperty("value", "yarn-cluster");

        JsonObject p2 = new JsonObject();
        p2.addProperty("name", "spark.driver.memory");
        p2.addProperty("value", "2g");

        JsonObject p3 = new JsonObject();
        p3.addProperty("name", "spark.executor.memory");
        p3.addProperty("value", "6g");

        JsonObject p4 = new JsonObject();
        p4.addProperty("name", "spark.executor.cores");
        p4.addProperty("value", "4");

        JsonObject p5 = new JsonObject();
        p5.addProperty("name", "spark.executor.instances");
        p5.addProperty("value", "8");

        JsonObject p6 = new JsonObject();
        p6.addProperty("name", "spark.yarn.tags");
        p6.addProperty("value", "xdf-ng-default");

        parameters.add(p1);
        parameters.add(p2);
        parameters.add(p3);
        parameters.add(p4);
        parameters.add(p5);
        parameters.add(p6);

        source.add(ProjectStore.PLP, parameters);
        root.add("source", source);
        String jsonStr = root.toString();
        WithProjectScopeHelper.logger.trace("jsonStr :" + jsonStr);
        MetaDataStoreRequestAPI requestAPI = new MetaDataStoreRequestAPI(jsonStr);
        requestAPI.process();
        WithProjectScopeHelper.logger.trace("creating a default project metadata ends here");
    }

    default boolean projectPresent(String name) {
        boolean present = false;
        try {
            ProjectAdmin prjStore = new ProjectAdmin(name);
            JsonElement prj = prjStore.readProjectData(name);
            if (prj != null)
                present = true;
        } catch (Exception ex) {
            WithProjectScopeHelper.logger.trace("Project metadata for " + name + " is not found ");
        }
        return present;
    }

    class WithProjectScopeHelper {
        private static final Logger logger = Logger.getLogger(WithProjectScope.class);
    }
}
