const element = require('protractor');
const path = require('path');
const LoginPage = require('../javascript/pages/loginPage.po');
const AnalyzePage = require('../javascript/pages/analyzePage.po');
const WorkbenchPage = require('../javascript/pages/workbenchPage.po');
const commonFunctions = require('../javascript/helpers/commonFunctions');
const Using = require('jasmine-data-provider');
const users = require('../javascript/data/users.js');
const Protractor = require('protractor');
const protractorConf = require('../../../conf/protractor.conf');

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
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach((done) => {
    setTimeout(() => {
    browser.waitForAngular();
    AnalyzePage.main.doAccountAction('logout');
    done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  beforeAll(() => {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  afterAll(() => {
    commonFunctions.logOutByClearingLocalStorage();
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
      // commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.createNewFolderElem);
      // browser.sleep(300);
      // expect(WorkbenchPage.newDialogue.dialogue.isDisplayed()).toBeTruthy();
      // const folderName = 'Test_Folder';
      // const DialogueInput = WorkbenchPage.newDialogue.getInputOfDialogue('workbench');
      // DialogueInput.clear().sendKeys(folderName);
      // const SubmitBtn = WorkbenchPage.newDialogue.submitFolderNameElem('workbench')
      // commonFunctions.waitFor.elementToBeClickableAndClick(SubmitBtn);
      // browser.sleep(500);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.fileUploadElem);
      const fileToUpload = '../demo_file.txt';
      const elem = WorkbenchPage.workbenchElems.fileUploadElem
      const absolutePath = path.resolve(__dirname, fileToUpload);
      // console.log('Absolut Path : ', absolutePath);
      WorkbenchPage.uploadFile(absolutePath, elem);

      browser.sleep(500);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.firstWindowStepperFwd);
      const fieldSeparator = WorkbenchPage.workbenchElems.fieldSeparatorElement;
      fieldSeparator.clear().sendKeys(fieldSeparatorStr);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.secondWindowStepperFwd);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.rawPreviewData);
      browser.sleep(500);

      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.thirdWindowStepperFwd);

      const dataSetName = WorkbenchPage.workbenchElems.dataSetName;
      const datasetNameTxt = 'DS_1'
      dataSetName.clear().sendKeys(datasetNameTxt);

      const dataSetDesc = WorkbenchPage.workbenchElems.dataSetDescription;
      dataSetName.clear().sendKeys('Description for ' + datasetNameTxt);

      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.dataSetActionElement.fourthWindowStepperAdd);
    });

    it('should preview a file from data grid', function() {
      expect(browser.getCurrentUrl()).toContain('/login');
      LoginPage.userLogin(data.user, users.anyUser.password);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.addDataSetBtn);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.fileElements.previewFile);
      browser.sleep(1000);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.fileElements.closeRawPreviewFile);
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

    it('should display the status of data set in card view mode', function(){
      expect(browser.getCurrentUrl()).toContain('/login');
      LoginPage.userLogin(data.user, users.anyUser.password);
      browser.sleep(1000);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.detailedDataSet.cardViewDataSetElem);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.detailedDataSet.dataSetDetailPreview);
    });

    it('should display the status of data set in list view mode', function(){
      expect(browser.getCurrentUrl()).toContain('/login');
      LoginPage.userLogin(data.user, users.anyUser.password);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.workbenchElems.listView);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.detailedDataSet.listViewDataSetElem);
      browser.sleep(1000);
      commonFunctions.waitFor.elementToBeClickableAndClick(WorkbenchPage.detailedDataSet.dataSetDetailPreview);
    });
  });
});
