var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const ChangePassword = require('../../pages/ChangePasswordPage');
const Header = require('../../pages/components/Header');
const commonFunctions = require('../../pages/utils/commonFunctions')
const users = require('../../helpers/data-generation/users');

describe('Executing login tests from ChangePasswd.test.js', () => {
  beforeAll(() => {
    logger.info('Starting login tests...');
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

  using(testDataReader.testData['CHANGEPWD']['negativeTests'] ? testDataReader.testData['CHANGEPWD']['negativeTests'] : {}, (data, id) => {
        it(`${id}:${data.description}`, () => {
        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users.anyUser.password);
        let header = new Header();
        header.verifyLogo();
        header.doChangePassword();
        let changePwd = new ChangePassword();
        changePwd.doChangePwd(data.password,data.newpassword,data.confirmpwd)
        changePwd.clickOnChangeButton();
        changePwd.verifyError(data.expected.message);
        header.doLogout();
      }).result.testInfo = { testId: id, data: data, feature: 'CHANGEPWD', dataProvider: 'negativeTests' };
  });

  using(testDataReader.testData['CHANGEPWD']['positiveTests'] ? testDataReader.testData['CHANGEPWD']['positiveTests'] : {}, (data, id) => {
      it(`${id}:${data.description}`, () => {
      let loginPage = new LoginPage();
      loginPage.doLogin(users[data.user].loginId,users.anyUser.password);
      let header = new Header();
      header.verifyLogo();
      header.doChangePassword();
      let changePwd = new ChangePassword();
      changePwd.doChangePwd(data.password,data.newpassword,data.confirmpwd)
      changePwd.clickOnChangeButton();
      changePwd.verifyError(data.expected.message);
      header.verifyChangePassword();
      loginPage.doLogin(users[data.user].loginId,data.newpassword);
      header.verifyLogo();
      header.doLogout();
  }).result.testInfo = { testId: id, data: data, feature: 'CHANGEPWD', dataProvider: 'positiveTests' };
});
});
