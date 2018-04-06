const LoginPage = require('../javascript/pages/loginPage.po');
const AnalyzePage = require('../javascript/pages/analyzePage.po');
// const WorkBenchService = require('../../main/javascript/app/modules/workbench/services/workbench.service');
const CONFIG =  require('../../../../saw-web/conf/protractor.conf');
const Using = require('jasmine-data-provider');
const Protractor = require('protractor');
const browser = require('browserstack-local');
const protractorConf = require('../../../../saw-web/conf/protractor.conf');
const ec = Protractor.ExpectedConditions;

describe('Workbench Tests : workbench.test.js',  () => {
    beforeEach(() => {
        setTimeout(() => {
            browser.waitForAngular();
            expect(browser.getCurrentUrl()).to.contain('/login');
            done();
        }, CONFIG.timeouts.pageResolveTimeout);
    });

    afterEach(() => {
        setTimeout(() => {
            browser.waitForAngular();
            AnalyzePage.main.doAccountAction('logout');
            done();
        }, CONFIG.timeouts.pageResolveTimeout);
    });

    beforeAll(() => {
        jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
    });

    afterAll(() => {
        browser.executeScript('window.sessionStorage.clear();');
        browser.executeScript('window.localStorage.clear();');
    });
    
});