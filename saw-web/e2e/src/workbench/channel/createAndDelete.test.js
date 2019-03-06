var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../protractor.conf');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const WorkBenchPage = require('../../../v2/pages/workbench/WorkBenchPage');
const Header = require('../../../v2/pages/components/Header');
const LoginPage = require('../../../v2/pages/LoginPage');
const logger = require('../../../v2/conf/logger')(__filename);
var fs = require('fs');

describe('Workbench tests: createAndDelete.test.js', () => {
  const sftpHost = 'sip-admin';
  const sftpPort = 22;
  const sftpUser = 'root';
  const sftpPassword = 'root';

  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['BIS_CHANNEL']['createAndDelete'], function(
    data,
    description
  ) {
    it(
      data.description +
        ' testDataMetaInfo: ' +
        JSON.stringify({
          test: description,
          feature: 'BIS_CHANNEL',
          dp: 'createAndDelete'
        }),
      function() {
        logger.warn(`Running testCase with id: ${data.id}`);
        logger.warn(`Data: ` + JSON.stringify(data));
        const channelName = `e2eChannel${new Date().getMilliseconds()}`;
        const channelDescription = `e2eChannel description created at ${new Date()}`;

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user);

        const header = new Header();
        header.clickOnLauncher();
        header.clickOnWorkBench();

        const workBenchPage = new WorkBenchPage();
        workBenchPage.clickOnAddChannelButton();
        workBenchPage.clickOnChannelType(data.sourceType);
        workBenchPage.clickOnChannelNextButton();
        workBenchPage.fillChannelName(channelName);
        workBenchPage.selectAccessType('rw');
        workBenchPage.enterHostName(sftpHost);
        // workBenchPage.fillUserName(sftpUser);
        // workBenchPage.fillPortNumber(sftpPort);
        // workBenchPage.fillPassword(sftpPassword);
        // workBenchPage.fillDescription(description);
        // workBenchPage.clickOnTestConnectivity();
        // workBenchPage.verifyTestConnectivityLogs();
        // workBenchPage.closeTestConnectivity();
        // workBenchPage.clickOnCreateButton();
      }
    );
  });
});
