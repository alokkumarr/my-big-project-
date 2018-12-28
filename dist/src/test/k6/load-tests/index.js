import http from 'k6/http';
import { Counter, Gauge, Rate, Trend} from "k6/metrics";
import { conf } from './conf/loadtest.conf.js';
import { AnalyzeTestCase } from './analyze/analyze.loadTest.js';
import { WorkbenchTestCase } from './workbench/workbench.loadTest.js';
import { LoginTestCase } from './login/login.loadTest.js';
 
export let options = {};
let requestConfig = conf.requestConfig
let GlobalData = conf.GlobalData;

let executeLoadTest = [AnalyzeTestCase.jobScheduler, AnalyzeTestCase.getListOfAnalysis, AnalyzeTestCase.addNewAnalysis, AnalyzeTestCase.executeUserAnalysis, AnalyzeTestCase.createNewAnalysis, AnalyzeTestCase.fetchSchedulerJob, AnalyzeTestCase.exportAnalysis, WorkbenchTestCase.dataSetDataPreview, WorkbenchTestCase.getTheListOfDirectories, WorkbenchTestCase.getTheListOfDataPods, WorkbenchTestCase.rawPreviewData, WorkbenchTestCase.dataSetPreviewSummary, WorkbenchTestCase.parsedPreviewData];
let executeAnalysisIDs = [AnalyzeTestCase.executeUserAnalysis, AnalyzeTestCase.fetchSchedulerJob, AnalyzeTestCase.exportAnalysis];
let createAnalysisIds = [AnalyzeTestCase.createNewAnalysis];

// let workbenchAPIList = [WorkbenchTestCase.dataSetDataPreview, WorkbenchTestCase.getTheListOfDirectories, WorkbenchTestCase.getTheListOfDataPods, WorkbenchTestCase.rawPreviewData, WorkbenchTestCase.dataSetPreviewSummary, WorkbenchTestCase.parsedPreviewData];
let workbenchDataSetNameList = [WorkbenchTestCase.dataSetDataPreview, WorkbenchTestCase.dataSetPreviewSummary];
let workBenchFileNameList = [WorkbenchTestCase.rawPreviewData, WorkbenchTestCase.parsedPreviewData];

// options.vus = conf.config.vus;
options.vus = 10;

 let loginResponse;
 let randomNumber;

 let startTime = new Counter("start_time");
 let endTime = new Counter("end_time");
 let testDuration = new Counter("test_duration");

// Get the demo json file...
const demoConf = JSON.parse(open("./json/test.conf.json"));

export function setup() {
    let response = {};
    let previewFileName = [];
    loginResponse = LoginTestCase.UserLogin();

    conf.GlobalData.token.setAccessToken(loginResponse.aToken);
    conf.GlobalData.token.setRefreshToken(loginResponse.rToken);

    response.accessToken = loginResponse.aToken;
    response.refreshToken = loginResponse.rToken;

   let getAnalysisRequest = AnalyzeTestCase.getListOfAnalysis();   
   let getAnalysisResponse = http.post(getAnalysisRequest.url, (getAnalysisRequest.body), getAnalysisRequest.params);
   response.analysisList = JSON.parse(getAnalysisResponse.body); 
 
   let getCreateAnalysisRequest = AnalyzeTestCase.addNewAnalysis();   
   let getCreateAnalysisResponse = http.post(getCreateAnalysisRequest.url, (getCreateAnalysisRequest.body), getCreateAnalysisRequest.params);
   response.createAnalysisList = JSON.parse(getCreateAnalysisResponse.body);

   response.workbench = {};
   let getDataSetListRequest = WorkbenchTestCase.getListOfDataSets();
   let getDataSetListResponse = http.get(getDataSetListRequest.url, getDataSetListRequest.params);
   response.workbench.dataSetList = JSON.parse(getDataSetListResponse.body);

   let getDirectoryListRequest = WorkbenchTestCase.getTheListOfDirectories('/');
   let getDirectoryListResponse = http.post(getDirectoryListRequest.url, getDirectoryListRequest.body, getDirectoryListRequest.params);
   response.workbench.directoryList = JSON.parse(getDirectoryListResponse.body);
   response.workbench.directoryList =  response.workbench.directoryList.data;
   response.workbench.subDirectoryList = [];

   response.workbench.directoryList = response.workbench.directoryList.filter(item => { 
     return (!item.name.includes('.txt') && !item.name.includes('.csv'));
    });
   
   if(response.workbench.directoryList.length > 0) {
    var randomNum = conf.utlity.getRandomNumber(response.workbench.directoryList);
    let getSubDirectoryListRequest = WorkbenchTestCase.getTheListOfSubDirectories( response.workbench.directoryList[randomNum].name );
    let getSubDirectoryListResponse = http.post(getSubDirectoryListRequest.url, getSubDirectoryListRequest.body, getSubDirectoryListRequest.params);
    let subDirectoryList = JSON.parse(getSubDirectoryListResponse.body);
    response.workbench.subDirectoryList = subDirectoryList.data;
   }   
   return response;
}


// Analyze Module test Cases
export default function(data) {
    let startDate = new Date();
    startTime.add(new Date(startDate));

    // Using batch processing.. 
    randomNumber = Math.floor(Math.random() * (executeLoadTest.length - executeLoadTest.length/2 + 1) + executeLoadTest.length/2);
    
    conf.GlobalData.token.setAccessToken(data.accessToken);
    conf.GlobalData.token.setRefreshToken(data.refreshoken);

    // randomNumber = 5; // executeLoadTest.length;
    if( randomNumber <= 0) randomNumber = 1;
    let batchArr = new Array();
    let index = 0;
    let num = 0;
    for (let i = 0; i <= randomNumber; i++) {
        index = Math.floor(Math.random() * executeLoadTest.length);
        if(executeAnalysisIDs.indexOf(executeLoadTest[index]) >= 0) {
          if (data.analysisList.contents !== undefined) {
            num = conf.utlity.getRandomNumber(data.analysisList.contents.analyze);
            batchArr.push(executeLoadTest[index](data.analysisList.contents.analyze[num].id));
          }
       } else if (createAnalysisIds.indexOf(executeLoadTest[index]) >= 0) {
           if(data.createAnalysisList.contents !== undefined) {
            num = conf.utlity.getRandomNumber(data.createAnalysisList.contents[0].ANALYZE);
            batchArr.push(executeLoadTest[index](data.createAnalysisList.contents[0].ANALYZE[num].id));
           }
       } else if ( workbenchDataSetNameList.indexOf(executeLoadTest[index]) >= 0 ) {
           if (data.workbench.dataSetList.length > 0) {
               num = conf.utlity.getRandomNumber(data.workbench.dataSetList);
               batchArr.push( executeLoadTest[index](data.workbench.dataSetList[num].system.name) );
            }
        }
        else if ( workBenchFileNameList.indexOf(executeLoadTest[index]) >= 0 ) {
            if (data.workbench.subDirectoryList.length > 0) {
                num = conf.utlity.getRandomNumber(data.workbench.subDirectoryList);
                batchArr.push( executeLoadTest[index](data.workbench.subDirectoryList[num].path + '/' + data.workbench.subDirectoryList[num].name) );
            }
        }
        else  {
            batchArr.push(executeLoadTest[index]());
        }
    }

    let value = '';
    batchArr = batchArr.filter(item => {
        return item !== value;
    });
    let responses = http.batch( batchArr ); 
    let endDate = new Date();
    endTime.add(new Date(endDate));
    let duration = 0;
    for (let ind in responses) {
        duration += responses[ind].timings.duration
    }
    testDuration.add(duration);

} 

export function teardown() {
    conf.GlobalData.token.setAccessToken('');
    conf.GlobalData.token.setRefreshToken('');

    executeLoadTest = [];
    executeAnalysisIDs = [];
    createAnalysisIds = [];
}
