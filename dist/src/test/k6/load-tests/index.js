import http from 'k6/http';
import { sleep, check } from 'k6';
import { conf } from './conf/loadtest.conf.js';
import { AnalyzeTestCase } from './analyze/analyze.loadTest.js';
import { WorkbenchTestCase } from './workbench/workbench.loadTest.js';
import { LoginTestCase } from './login/login.loadTest.js';
 
export let options = {};
let requestConfig = conf.requestConfig
let GlobalData = conf.GlobalData;
let executeLoadTest = [AnalyzeTestCase.jobScheduler, AnalyzeTestCase.getListOfAnalysis, AnalyzeTestCase.addNewAnalysis, AnalyzeTestCase.executeUserAnalysis, AnalyzeTestCase.createNewAnalysis, AnalyzeTestCase.fetchSchedulerJob, AnalyzeTestCase.exportAnalysis];
let executeAnalysisIDs = [AnalyzeTestCase.executeUserAnalysis, AnalyzeTestCase.fetchSchedulerJob, AnalyzeTestCase.exportAnalysis];
let createAnalysisIds = [AnalyzeTestCase.createNewAnalysis];

// options.stages = conf.config.stages;
 options.vus = conf.config.vus;
 let loginResponse;
 let randomNumber;
// Get the demo json file...
const demoConf = JSON.parse(open("./json/test.conf.json"));

export function setup() {
    let response = {};
    loginResponse = LoginTestCase.UserLogin();
    conf.GlobalData.token.setAccessToken(loginResponse.aToken);
    conf.GlobalData.token.setRefreshToken(loginResponse.rToken);

    response.accessToken = loginResponse.aToken;
    response.refreshoken = loginResponse.rToken;

   let getAnalysisRequest = AnalyzeTestCase.getListOfAnalysis();   
   let getAnalysisResponse = http.post(getAnalysisRequest.url, (getAnalysisRequest.body), getAnalysisRequest.params);
   response.analysisList = JSON.parse(getAnalysisResponse.body); 
 
   let getCreateAnalysisRequest = AnalyzeTestCase.addNewAnalysis();   
   let getCreateAnalysisResponse = http.post(getCreateAnalysisRequest.url, (getCreateAnalysisRequest.body), getCreateAnalysisRequest.params);
   response.createAnalysisList = JSON.parse(getCreateAnalysisResponse.body);
   return response;
}

export default function(data) {
    // Using batch processing.. 
    randomNumber = Math.floor(Math.random() * executeLoadTest.length);
    
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
       } else  {
           batchArr.push(executeLoadTest[index]());
       }
     } 
      // num = conf.utlity.getRandomNumber(data.analysisList.contents.analyze);
      // batchArr.push(executeLoadTest[executeLoadTest.length-1](data.analysisList.contents.analyze[num].id));
      let value = '';
      batchArr = batchArr.filter(item => { 
        return item !== value;
    });
      let responses = http.batch( batchArr);
} 

export function teardown() {
    conf.GlobalData.token.setAccessToken('');
    conf.GlobalData.token.setRefreshToken('');

    executeLoadTest = [];
    executeAnalysisIDs = [];
    createAnalysisIds = [];
}
