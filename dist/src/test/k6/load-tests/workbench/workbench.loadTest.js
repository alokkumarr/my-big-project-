import http from 'k6/http';
import { sleep, check } from 'k6';

export const WorkbenchTestCase = {
    getListOfDataSets : (aToken) => {
        let dataSetListRequest = {
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
        })
    },

    dataSetPreviewSummary : (aToken) => {
        let dataSetPreviewRequest = {
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
        })
    },

    dataSetDataPreview : (aToken) => {
        let dataSetDataPreviewRequest = {
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
        })

    },

    getTheListOfDirectories : (aToken) => {
        let directoryListRequest = {
            method: 'POST',
            url: 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net/saw/services/internal/workbench/projects/workbench/raw/directory',
            params: {
                headers: {
                    'Content-Type' : 'application/json; charset=UTF-8',
                    'Accept-Language' : 'en-GB,en-US;q=0.9,en;q=0.8',
                    'Authorization': "Bearer " + GlobalData.token.getAccessToken()
                }
            },
            body: {'path':'/'}
        }    
        let directoryListResponse = http.post(directoryListRequest.url, JSON.stringify(directoryListRequest.body), directoryListRequest.params);
        check(directoryListResponse, {
            'directoryListResponse status is ok : ': (r) => directoryListResponse.status == 200
        });
    },

    parsedPreviewData : (aToken) => {
        let parsedPreviewRequest = {
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
        });
    },

    rawPreviewData : (aToken) => {
        let rawPreviewRequest = {
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
        });
    }
}