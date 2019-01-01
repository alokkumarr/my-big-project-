import http from "k6/http";
import { sleep, check } from "k6";
import { Rate } from "k6/metrics";
import { parseHTML } from "k6/html";

export const AnalyzeTestCase = {
    jobScheduler : () => {        
        let schedulerRequest = {
            method: demoConf.analyze.jobScheduler.method,
            url : demoConf.baseURL + demoConf.analyze.jobScheduler.endPoint,
            body: JSON.stringify(demoConf.analyze.jobScheduler.requestBody),
            params: demoConf.analyze.jobScheduler.params          
        }   
        schedulerRequest.params.headers.Authorization = "Bearer " +  GlobalData.token.getAccessToken();
        return schedulerRequest;
    },
    
    getListOfAnalysis : () => {
        
        let analyzeRequest = {
            method: demoConf.analyze.getAnalysisList.method,
            url: demoConf.baseURL + demoConf.analyze.getAnalysisList.endPoint,
            body: JSON.stringify(demoConf.analyze.getAnalysisList.requestBody),
            params: demoConf.analyze.getAnalysisList.params            
        }    
        analyzeRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        return analyzeRequest;
    },

    addNewAnalysis : () => {
        
        let addAnalysisRequest = {
            method: demoConf.analyze.addNewAnalysis.method,
            url: demoConf.baseURL + demoConf.analyze.addNewAnalysis.endPoint, 
            body: JSON.stringify(demoConf.analyze.addNewAnalysis.requestBody),
            params : demoConf.analyze.addNewAnalysis.params           
        };
        addAnalysisRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        return addAnalysisRequest
    },

    createNewAnalysis : (id) => {
        let createAnalysisRequest = {
            method: demoConf.analyze.createNewAnalysis.method,
            url: demoConf.baseURL + demoConf.analyze.createNewAnalysis.endPoint,
            body: demoConf.analyze.createNewAnalysis.requestBody,
            params: demoConf.analyze.createNewAnalysis.params           
        }    
        createAnalysisRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        createAnalysisRequest.body.contents.keys[0].id = id;
        createAnalysisRequest.body = JSON.stringify(createAnalysisRequest.body)
        
        return createAnalysisRequest;  
    },

    refreshAnalysisWithFilters : () => {
        let refreshAnalysisRequest = {
            method: "POST",
            url: "https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/analysis",
            params: {
                headers: {
                    "Content-Type" : "application/json; charset=UTF-8",
                    "Accept-Language" : "en-GB,en-US;q=0.9,en;q=0.8",
                    "Authorization": "Bearer " + GlobalData.token.getAccessToken()
                }
            },
            body: requestConfig.refreshAnalysisRequestBody
        }  
        return refreshAnalysisRequest;
    },

    executeUserAnalysis : (id) => {
        let executeAnalysisRequest = {
            method: demoConf.analyze.executeAnalysis.method, 
            url : demoConf.baseURL + demoConf.analyze.executeAnalysis.endPoint + id + '/executions',
            params: demoConf.analyze.executeAnalysis.params
        }
        executeAnalysisRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
      
        return executeAnalysisRequest;
    },

    executeAnalysisDataPage : () => {
        let executeAnalysisDataPageRequest = {
           // method: "POST",
            url: "https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/analysis/24fda562-864c-4fb4-ad74-8e53ba4834d9/executions/24fda562-864c-4fb4-ad74-8e53ba4834d9::4592765593350947/data?page=1&pageSize=10&analysisType=esReport",
            params: {
                headers: {
                    "Content-Type" : "application/json; charset=UTF-8",
                    "Accept-Language" : "en-GB,en-US;q=0.9,en;q=0.8",
                    "Authorization": "Bearer " + GlobalData.token.getAccessToken()
                }
            },
        }
        let executeAnalysisDataPageResponse = http.get(executeAnalysisDataPageRequest.url, executeAnalysisDataPageRequest.params);
        check(executeAnalysisDataPageResponse, {
            "executeAnalysisDataPageResponse status is ok : ": (r) => executeAnalysisDataPageResponse.status == 200
        })
    },

    exportAnalysis : (id) => {

        /* let exportAnalysisRequest = {
            // method: "POST",
            url: "https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/exports/24fda562-864c-4fb4-ad74-8e53ba4834d9::4592765593350947/executions/24fda562-864c-4fb4-ad74-8e53ba4834d9/data?analysisType=esReport",
            params: {
                headers: {
                    "Content-Type" : "application/json; charset=UTF-8",
                    "Accept-Language" : "en-GB,en-US;q=0.9,en;q=0.8",
                    "Authorization": "Bearer " + GlobalData.token.getAccessToken()
                }
            },
        }
        let exportAnalysisResponse = http.get(exportAnalysisRequest.url, exportAnalysisRequest.params);
        check(exportAnalysisResponse, {
            "exportAnalysisResponse status is ok : ": (r) => exportAnalysisResponse.status == 200
        }) */

        let executeAnalysisRequest = {
            method: demoConf.analyze.executeAnalysis.method,
            url : demoConf.baseURL + demoConf.analyze.executeAnalysis.endPoint + id + '/executions',
            params: demoConf.analyze.executeAnalysis.params,
        }
        executeAnalysisRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        let executeAnalysisResponse = http.get(executeAnalysisRequest.url, executeAnalysisRequest.params);
        executeAnalysisResponse = JSON.parse(executeAnalysisResponse.body);
        if (executeAnalysisResponse.results.length > 0) {
            let randomNum = Math.floor(Math.random() * executeAnalysisResponse.results.length);
            let Id = executeAnalysisResponse.results[randomNum].id.substring(0, executeAnalysisResponse.results[randomNum].id.indexOf('::'));
     
            let exportAnalysisRequest = {
                method: demoConf.analyze.exportAnalysis.method,
                url: demoConf.baseURL + demoConf.analyze.exportAnalysis.endPoint + executeAnalysisResponse.results[randomNum].id + '/executions/' + Id + '/data?analysisType=esReport',
                params: demoConf.analyze.exportAnalysis.params,
            }
            exportAnalysisRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
            return exportAnalysisRequest;
        } else {
            return '';
        }
    },

    fetchSchedulerJob : (id) => {
        let fectSchedulerJobRequest = {
            method: demoConf.analyze.fetchSchedulerJob.method,
            url : demoConf.baseURL + demoConf.analyze.fetchSchedulerJob.endPoint,
            body: (demoConf.analyze.fetchSchedulerJob.requestBody),
            params: demoConf.analyze.fetchSchedulerJob.params,
        }
        fectSchedulerJobRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        fectSchedulerJobRequest.body.jobName = id;
        fectSchedulerJobRequest.body = JSON.stringify(fectSchedulerJobRequest.body);
        return fectSchedulerJobRequest;
    }
}