let accessToken = '';
let refreshToken = '';

export const conf = {
    config : {
      /*  stages: [
            {duration: '20s', target: 40},
            {duration: '15s', target: 20},
            {duration: '10s', target: 10},
            {duration: '5s', target: 0}
           ], */
           // iterations : 10,
           vus : 20
     },
     requestConfig : {
        refreshAnalysisRequestBody : {"contents":{"keys":[{"customerCode":"SYNCHRONOSS","module":"ANALYZE","id":"64a97f24-d803-4db5-80fb-4656efdd3b7f","type":"report"}],"action":"execute","page":1,"pageSize":10,"analyze":[{"type":"report","chartType":null,"semanticId":"03efcd7e-1ebe-45e4-a466-d3aa8ee2c8b3","metricName":"TollFree Call Analysis","name":"Untitled Analysis","description":"","scheduled":null,"artifacts":[{"artifactName":"TollFree_Call_Analysis","columns":[{"aliasName":"","columnName":"ACCOUNT_NUMBER","displayName":"Account Number","filterEligible":true,"joinEligible":false,"name":"ACCOUNT_NUMBER","table":"TollFree_Call_Analysis","type":"string","checked":true},{"columnName":"VENDOR_NAME","displayName":"Vendor Name","filterEligible":true,"joinEligible":false,"name":"VENDOR_NAME","table":"TollFree_Call_Analysis","type":"string"},{"columnName":"CALL_DATE","displayName":"Call Date","filterEligible":true,"joinEligible":false,"name":"CALL_DATE","table":"TollFree_Call_Analysis","type":"date","checked":true,"format":"yyyy-MM-dd"},{"columnName":"MOBILE_IND","displayName":"Mobile IND","filterEligible":true,"joinEligible":false,"name":"MOBILE_IND","table":"TollFree_Call_Analysis","type":"long"},{"columnName":"REGION","displayName":"Region","filterEligible":true,"joinEligible":false,"name":"REGION","table":"TollFree_Call_Analysis","type":"string"},{"columnName":"SERVICE_TYPE","displayName":"Service Type","filterEligible":true,"joinEligible":false,"name":"SERVICE_TYPE","table":"TollFree_Call_Analysis","type":"string"},{"columnName":"CUST_TN","displayName":"Cust TN","filterEligible":true,"joinEligible":false,"name":"CUST_TN","table":"TollFree_Call_Analysis","type":"long"},{"columnName":"CALL_TYPE","displayName":"Call Type","filterEligible":true,"joinEligible":false,"name":"CALL_TYPE","table":"TollFree_Call_Analysis","type":"string"},{"columnName":"TOTAL_CALLS","displayName":"Total Calls","filterEligible":true,"joinEligible":false,"name":"TOTAL_CALLS","table":"TollFree_Call_Analysis","type":"long"},{"columnName":"TOTAL_SWITCH_MOU","displayName":"Total Switch MOU","filterEligible":true,"joinEligible":false,"name":"TOTAL_SWITCH_MOU","table":"TollFree_Call_Analysis","type":"double"},{"columnName":"TOTAL_COST","displayName":"Total Cost","filterEligible":true,"joinEligible":false,"name":"TOTAL_COST","table":"TollFree_Call_Analysis","type":"double"},{"columnName":"TOTAL_MSG","displayName":"Total MSG","filterEligible":true,"joinEligible":false,"name":"TOTAL_MSG","table":"TollFree_Call_Analysis","type":"long"},{"columnName":"CDR_JURIS","displayName":"CDR Juris","filterEligible":true,"joinEligible":false,"name":"CDR_JURIS","table":"TollFree_Call_Analysis","type":"string","checked":true},{"columnName":"SETTLEMENT_CODE","displayName":"Settlement Code","filterEligible":true,"joinEligible":false,"name":"SETTLEMENT_CODE","table":"TollFree_Call_Analysis","type":"string"},{"columnName":"CALL_DIR","displayName":"Call Dir","filterEligible":true,"joinEligible":false,"name":"CALL_DIR","table":"TollFree_Call_Analysis","type":"long"}],"artifactPosition":[20,0]}],"checked":false,"customerCode":"SYNCHRONOSS","dataSecurityKey":"","disabled":false,"esRepository":{"indexName":"","storageType":"","type":""},"id":"64a97f24-d803-4db5-80fb-4656efdd3b7f","metric":"TollFree Call Analysis","module":"ANALYZE","repository":{"storageType":"DL","objects":[{"EnrichedDataObjectId":"TollFree_Call_Analysis::parquet::1526327259381","displayName":"TollFree Call Analysis","EnrichedDataObjectName":"TollFree_Call_Analysis","description":"TollFree Call Analysis","lastUpdatedTimestamp":"undefined"}],"_number_of_elements":1},"createdTimestamp":1530275287608,"userId":1,"userFullName":"system sncr admin","sqlBuilder":{"booleanCriteria":"AND","filters":[],"orderByColumns":[],"joins":[]},"edit":false,"executionType":"preview"}]}},
        createAnalysisrequestBody : {"contents":{"keys":[{"customerCode":"SYNCHRONOSS","module":"ANALYZE","id":"03efcd7e-1ebe-45e4-a466-d3aa8ee2c8b3","analysisType":"report"}],"action":"create"}},
        addAnalysisRequestBody : {"contents":{"keys":[{"customerCode":"SYNCHRONOSS","module":"ANALYZE"}],"action":"search","select":"headers","context":"Semantic"}},
        jobSchedulerRequestBody : {"categoryId":"4","groupkey":"SYNCHRONOSS"}
    },
    
    loginConfig : {
        loginRequestBody : {
            'masterLoginId':'sawadmin@synchronoss.com',
            'password':'Sawsyncnewuser1!'
        }
    },

    GlobalData : {
        token : {
            setAccessToken : (token) => {
                accessToken = token;
            },
    
            getAccessToken : () =>{
                return accessToken;
            },
    
            setRefreshToken : (token) => {
                refreshToken = token;
            },
    
            getRefreshToken : () =>{
                return refreshToken;
            }
        }
    },

    utlity : {
        getRandomNumber : (arr) => {
            let num = 0;
            if (Array.isArray(arr)) {
                num = Math.floor(Math.random() * arr.length);
            } else {
                num = Math.floor(Math.random() * arr);
            }
            return num;            
        }
    }
 }