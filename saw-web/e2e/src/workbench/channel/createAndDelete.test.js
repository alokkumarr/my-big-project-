var testDataReader = require('./testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const loginPage = require('./javascript/pages/loginPage.po.js');
const header = require('./javascript/pages/components/header.co.js');
const protractorConf = require('../protractor.conf');
const commonFunctions = require('./javascript/helpers/commonFunctions.js');
const WorkBenchPage = require('../../../v2/pages/workbench/WorkBenchPage');
const Header = require('../../../v2/pages/components/Header');

var fs = require('fs');

describe('Workbench tests: createAndDelete.test.js', () => {
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

  using(
    testDataReader.testData['CREATE_DELETE_CHANNEL']['positiveTests'],
    function(data, description) {
      it(
        data.description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'CREATE_DELETE_CHANNEL',
            dp: 'positiveTests'
          }),
        function() {
          loginPage.loginAs(data.user);
          const header = new Header();
          header.clickOnLauncher();
          header.clickOnWorkBench();
          // User should be redirected to work bench page
          const workBenchPage = new WorkBenchPage();
        }
      );
    }
  );
});
