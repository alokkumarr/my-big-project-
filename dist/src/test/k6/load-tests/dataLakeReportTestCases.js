import http from 'k6/http';
import { Counter, Trend} from "k6/metrics";
import { conf } from './conf/loadtest.conf.js';
import { AnalyzeTestCase } from './analyze/analyze.loadTest.js';
import { LoginTestCase } from './login/login.loadTest.js';
 
export let options = {};
let requestConfig = conf.requestConfig
let GlobalData = conf.GlobalData;

options.vus = conf.config.vus;

let startTime = new Counter("start_time");
let endTime = new Counter("end_time");
let testDuration = new Trend("test_duration");

 let loginResponse;
 let randomNumber;
// Get the demo json file...
const demoConf = JSON.parse(open("./json/test.conf.json"));

export function setup() {
    let response = {};
    response.analyze = {};
    let previewFileName = [];
    loginResponse = LoginTestCase.UserLogin();

    conf.GlobalData.token.setAccessToken(loginResponse.aToken);
    conf.GlobalData.token.setRefreshToken(loginResponse.rToken);

    response.accessToken = loginResponse.aToken;
    response.refreshToken = loginResponse.rToken;

    let getListOfAnalysisTypeRequest = AnalyzeTestCase.getTheListOfAnalysisType();   
    let getListOfAnalysisTypeResponse = http.get(getListOfAnalysisTypeRequest.url, getListOfAnalysisTypeRequest.params);
    response.analyze.analysisListType = JSON.parse(getListOfAnalysisTypeResponse.body);

    response.analyze.analysisListType = response.analyze.analysisListType.contents[0].ANALYZE.filter(item => {
        return (item.metricName === 'SVARUF VIEW');
    });

    let getAnalysisFilterRequest = AnalyzeTestCase.createNewAnalysis(response.analyze.analysisListType[0].id);   
    let getAnalysisFilterResponse = http.post(getAnalysisFilterRequest.url, (getAnalysisFilterRequest.body), getAnalysisFilterRequest.params);
    response.analyze.filterAnalysisList = JSON.parse(getAnalysisFilterResponse.body);

    response.analyze.filterAnalysisList = response.analyze.filterAnalysisList.contents.analyze[0].artifacts[0].columns
   // console.log('Analysis filter list : ', JSON.stringify(response.analyze.filterAnalysisList.contents.analyze[0].artifacts[0].columns));

    return response;
}

export default function(data) {
    let startDate = new Date();
    startTime.add(new Date(startDate));

    conf.GlobalData.token.setAccessToken(data.accessToken);
    conf.GlobalData.token.setRefreshToken(data.refreshToken);

    let analysisTypeList = new Array();
    analysisTypeList = data.analyze.filterAnalysisList;

    let randomnum = Math.floor(Math.random() * analysisTypeList.length);
    let columns = [], randomColumnNum = 0;
    for (let i = 0; i < randomnum; i++) {
        randomColumnNum = Math.floor(Math.random() * analysisTypeList.length);
        columns.push(analysisTypeList[randomColumnNum]);
        // analysisTypeList.splice(randomColumnNum, 1); Somehow this piece of code is producing error. 
        analysisTypeList = analysisTypeList.filter(item => {
            return item !== analysisTypeList[randomColumnNum];
        });
    }
    let refreshAnalysisRequest = AnalyzeTestCase.refreshAnalysis();
    let reqBody = JSON.parse(refreshAnalysisRequest.body);
    reqBody.contents.analyze[0].sqlBuilder.dataFields[0].columns = columns;
    refreshAnalysisRequest.body = JSON.stringify(reqBody);
    let resp = http.post(refreshAnalysisRequest.url, refreshAnalysisRequest.body, refreshAnalysisRequest.params);
    
    let endDate = new Date();
    endTime.add(new Date(endDate));
    testDuration.add(resp.timings.duration);


}

export function teardown() {
    conf.GlobalData.token.setAccessToken('');
    conf.GlobalData.token.setRefreshToken('');
}