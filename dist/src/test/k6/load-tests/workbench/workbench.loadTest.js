import http from 'k6/http';
import { sleep, check } from 'k6';

export const WorkbenchTestCase = {
    getListOfDataSets : () => {
        /* let dataSetListRequest = {
            method: 'GET',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/datasets', 
            params : {
                headers : {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            }
        };
    
        let dataSetListResponse = http.get(dataSetListRequest.url,  dataSetListRequest.params);      
        check(dataSetListResponse, {
            'dataSetListResponse status is ok : ': (r) => dataSetListResponse.status == 200
        }) */

        let dataSetListRequest = {
            method: demoConf.workbench.getListOfDataSets.method,
            url: demoConf.baseURL + demoConf.workbench.getListOfDataSets.endPoint,
            params : demoConf.workbench.getListOfDataSets.params
        };

        dataSetListRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        return dataSetListRequest;
    },

    getTheListOfDataPods : () => {
        let dataPodsListRequest = {
            method: demoConf.workbench.getTheListOfDataPods.method,
            url: demoConf.baseURL + demoConf.workbench.getTheListOfDataPods.endPoint,
            params : demoConf.workbench.getTheListOfDataPods.params
        };

        dataPodsListRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        return dataPodsListRequest;
    },

    dataSetPreviewSummary : (dataSetName) => {
        /* let dataSetPreviewRequest = {
            method: 'POST',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/previews',
            params: {
                headers: {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            },
            body: {'name':'13AprCrime'}
        }    
        let dataSetPreviewResponse = http.post(dataSetPreviewRequest.url, JSON.stringify(dataSetPreviewRequest.body), dataSetPreviewRequest.params);
        check(dataSetPreviewResponse, {
            'dataSetPreviewResponse status is ok : ': (r) => dataSetPreviewResponse.status == 200
        }) */

        let dataSetPreviewRequest = {
            method: demoConf.workbench.dataSetPreviewSummary.method,
            url: demoConf.baseURL + demoConf.workbench.dataSetPreviewSummary.endPoint,
            params: demoConf.workbench.dataSetPreviewSummary.params,
            body: (demoConf.workbench.dataSetPreviewSummary.requestBody)
        } 
        
        dataSetPreviewRequest.body.name = dataSetName;
        dataSetPreviewRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken();
        return dataSetPreviewRequest
    },

    dataSetDataPreview : (name) => {
        /* let dataSetDataPreviewRequest = {
            method: 'GET',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/previews/85aea9a9-1e3b-47d0-8cdb-ef07da7738b3', 
            params : {
                headers : {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            }
        };
    
        let dataSetDataPreviewResponse = http.get(dataSetDataPreviewRequest.url,  dataSetDataPreviewRequest.params);  

    
        check(dataSetDataPreviewResponse, {
            'dataSetDataPreviewResponse status is ok : ': (r) => dataSetDataPreviewResponse.status == 200
        }) */

        let dataSetDataPreviewRequest = {
            method: demoConf.workbench.dataSetDataPreview.method,
            url: demoConf.baseURL + demoConf.workbench.dataSetDataPreview.endPoint,
            params : demoConf.workbench.dataSetDataPreview.params,
            body: name
        };
        dataSetDataPreviewRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken()
        return dataSetDataPreviewRequest;
    },

    getTheListOfDirectories : () => {
        let directoryListRequest = {
            method: demoConf.workbench.getTheListOfDirectories.method,
            url: demoConf.baseURL + demoConf.workbench.getTheListOfDirectories.endPoint,
            params: demoConf.workbench.getTheListOfDirectories.params,
            body: JSON.stringify(demoConf.workbench.getTheListOfDirectories.requestBody)
        }    
        directoryListRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken()
        return directoryListRequest;
    },

    getTheListOfSubDirectories : (directoryPath) => {
        let requestBody = demoConf.workbench.getTheListOfSubDirectories.requestBody;
        requestBody.path = "//"+directoryPath
        let subDirectoryListRequest = {
            method: demoConf.workbench.getTheListOfSubDirectories.method,
            url: demoConf.baseURL + demoConf.workbench.getTheListOfSubDirectories.endPoint,
            params: demoConf.workbench.getTheListOfSubDirectories.params,
            body: JSON.stringify(requestBody)
        }
        subDirectoryListRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken()
        return subDirectoryListRequest;
    },

    parsedPreviewData : (previewFilePath) => {
        /* let parsedPreviewRequest = {
            method: 'POST',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/raw/directory/inspect',
            params: {
                headers: {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            },
            body: {'file':'//Workbench1234/dummy-file_0642.txt','lineSeparator':'\n','delimiter':',','quoteChar':'','quoteEscapeChar':'','headerSize':'1','fieldNamesLine':'1','dateFormats':[],'rowsToInspect':100,'delimiterType':'delimited','header':'yes'}
        }    
        let parsedPreviewResponse = http.post(parsedPreviewRequest.url, JSON.stringify(parsedPreviewRequest.body), parsedPreviewRequest.params);
        check(parsedPreviewResponse, {
            'parsedPreviewResponse status is ok : ': (r) => parsedPreviewResponse.status == 200
        }); */

        let previewFileProp = demoConf.workbench.parsedPreviewData.requestBody;
        previewFileProp.file = previewFilePath;

        let parsedPreviewRequest = {
            method: demoConf.workbench.parsedPreviewData.method,
            url: demoConf.baseURL + demoConf.workbench.parsedPreviewData.endPoint,
            params: demoConf.workbench.parsedPreviewData.params,
            body: JSON.stringify(previewFileProp)
        } 
        parsedPreviewRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken()    
        return parsedPreviewRequest;

    },

    rawPreviewFile : (filePath) => {
        /* let rawPreviewRequest = {
            method: 'POST',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/raw/directory/preview',
            params: {
                headers: {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            },
            body: {'path':'//Workbench1234/dummy-file_0642.txt'}
        }    
        let rawPreviewResponse = http.post(rawPreviewRequest.url, JSON.stringify(rawPreviewRequest.body), rawPreviewRequest.params);
        check(rawPreviewResponse, {
            'rawPreviewResponse status is ok : ': (r) => rawPreviewResponse.status == 200
        }); */

        let rawPreviewRequest = {
            method: demoConf.workbench.rawPreviewFile.method,
            url: demoConf.baseURL + demoConf.workbench.rawPreviewFile.endPoint,
            params: demoConf.workbench.rawPreviewFile.params,
            body: (demoConf.workbench.rawPreviewFile.requestBody)
        }    

        rawPreviewRequest.body.path = "//" + filePath;
        rawPreviewRequest.params.headers.Authorization = "Bearer " + GlobalData.token.getAccessToken()
        return rawPreviewRequest;

    }
}