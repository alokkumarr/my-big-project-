const element = require('protractor');
const path = require('path');
const LoginPage = require('../javascript/pages/loginPage.po');
const AnalyzePage = require('../javascript/pages/analyzePage.po');
const WorkbenchPage = require('../javascript/pages/workbenchPage.po');
// const WorkBenchService = require('../../main/javascript/app/modules/workbench/services/workbench.service');
const CONFIG =  require('../../../../saw-web/conf/protractor.conf');
const commonFunctions = require('../javascript/helpers/commonFunctions');
const Using = require('jasmine-data-provider');
const users = require('../javascript/data/users.js');
const Protractor = require('protractor');
const protractorConf = require('../../../../saw-web/conf/protractor.conf');

const ec = Protractor.ExpectedConditions;
describe('Workbench Tests : workbench.test.js',  () => {
    const UserLists = {
        'admin': {user: users.admin.loginId},
        'user': {user: users.userOne.loginId},
    }
    beforeEach((done) => {
        setTimeout(() => {
            browser.waitForAngular();
            expect(browser.getCurrentUrl()).toContain('/login');
            done();
        }, CONFIG.timeouts.pageResolveTimeout);
    });

    afterEach((done) => {
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
    
    Using(UserLists, function (data, description) {
        const fieldSeparatorStr = ",";
          it('should display card view by default by ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            WorkbenchPage.validateCardViewMode();
          });

          it('should display data sets view by default by ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            WorkbenchPage.validateSetViewMode();
          }); 

          it('should refresh portal ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.refreshBtn);
          }); 

          it('should display portal in list view when list is selected ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.listView);
          });

          it('should display portal in card view when card is selected ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.cardView);
          }); 

          it('should display portal in sets when data sets is selected ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.setsView);
          });

          it('should display portal in pods when data pods is selected ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.podsView);
          });

          it('should add new dataset ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.addDataSetBtn);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.fileElements.fileName);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.nextStepperBtn);
            const fieldSeparator = WorkbenchPage.workbenchElems.fieldSeparatorElement;
            fieldSeparator.clear().sendKeys(fieldSeparatorStr);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.nextStepperBtn);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.nextStepperBtn);
        });
        
        it('should execute sql  ' + description, function () {
            expect(browser.getCurrentUrl()).toContain('/login');
            LoginPage.userLogin(data.user, users.anyUser.password);
            commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.dataSetAction);            
            const menuAction = WorkbenchPage.dataSetActionElement.menuAction;
            commonFunctions.waitFor.elementToBePresent(menuAction);
            commonFunctions.waitFor.elementToBeEnabledAndVisible(menuAction);
            commonFunctions.waitFor.elementToBeClickableAndClick(menuAction);
          });

        it('should upload a file', function() {
            const fileToUpload = 'demo/demo_file.txt';
            const elem = WorkbenchPage.workbenchElems.fileUploadElem
            const absolutePath = path.resolve(__dirname, fileToUpload);
            WorkbenchPage.uploadFile(absolutePath, elem);
        });
    });
});