import http from 'k6/http';
import { sleep, check } from 'k6';
import { conf } from '../conf/loadtest.conf.js';

let requestConfig = conf.loginConfig;

export const LoginTestCase = {
    UserLogin : () => {
        let loginRequest = {
            method: demoConf.auth.login.method,
            url: demoConf.baseURL + demoConf.auth.login.endPoint, 
            params : demoConf.auth.login.params,
            body: demoConf.auth.login.requestBody
        }
        let loginResponse = http.post(loginRequest.url, JSON.stringify(loginRequest.body), loginRequest.params);
        /* check(loginResponse, {
            'loginResponse status is ok : ': (r) => loginResponse.status == 200
        }) */
        return JSON.parse(loginResponse.body);
    }
};