package com.synchronoss.saw.workbench.service;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;

import sncr.bda.conf.ComponentConfiguration;

import sncr.xdf.context.ComponentServices;
import sncr.xdf.context.NGContext;

import sncr.xdf.services.NGContextServices;



public class XDFContextProvider {

    private final NGContextServices ngCtxSvc;


    public XDFContextProvider(@NotNull String root,
                              String project,
                              String component,
                              String config) throws Exception {
        String batch = "batch-" + Instant.now().toEpochMilli();
        ComponentConfiguration cfg = null;

        switch (component) {
        case "parser":
            ComponentServices[] pcs =
            {
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = NGContextServices.analyzeAndValidateParserConf(config);
            ngCtxSvc = new NGContextServices(
                pcs, root, cfg, project, component, batch);
            break;
        case "sql":
            ComponentServices[] scs =
            {
                ComponentServices.InputDSMetadata,
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = NGContextServices.analyzeAndValidateSqlConf(config);
            ngCtxSvc = new NGContextServices(
                scs, root, cfg, project, component, batch);
            break;
        case "transformer":
            ComponentServices[] tcs =
            {
                ComponentServices.InputDSMetadata,
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = NGContextServices.analyzeAndValidateTransformerConf(config);
            ngCtxSvc = new NGContextServices(
                tcs, root, cfg, project, component, batch);
            break;
        default:
            ngCtxSvc = null;
            throw new Exception("Unsupported component: " + component);
        }
        ngCtxSvc.initContext();
        ngCtxSvc.registerOutputDataSet();
    }

    public List<String> getDataSetIDs() {
        return ngCtxSvc.getNgctx().registeredOutputDSIds;
    }

    public NGContext getNGContext() {
        return ngCtxSvc.getNgctx();
    }
}
