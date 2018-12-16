var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const Header = require('../../pages/components/Header');
const commonFunctions = require('../../pages/utils/commonFunctions')

describe('Executing logout tests from logout.test.js', () => {

  beforeAll(() => {
    logger.info('Starting logout tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach((done) => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach((done) => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['LOGOUT']['positiveTests'] ? testDataReader.testData['LOGOUT']['positiveTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {

      let loginPage = new LoginPage();
      loginPage.loginAs(data.user, /analyze/);
      let header = new Header();
      header.verifyLogo();
      // DO the logout
      header.doLogout();

    }).result.testInfo = { testId: id, data: data, feature: 'LOGOUT', dataProvider: 'positiveTests' };
  });
});
