var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const UserManagement = require('../../pages/admin/UserManagement');
const Header = require('../../pages/components/Header');
const commonFunctions = require('../../pages/utils/commonFunctions')
const users = require('../../helpers/data-generation/users');

describe('Executing login tests from User-Management.test.js', () => {
  beforeAll(() => {
    logger.info('Starting login tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach((done) => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach((done) => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['USERMANAGEMENT']['positiveTests'] ? testDataReader.testData['USERMANAGEMENT']['positiveTests'] : {}, (data, id) => {
        it(`${id}:${data.description}`, () => {
        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        let header = new Header();
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        let userMgnPage = new UserManagement();
        userMgnPage.clickOnNewUserButton();
        userMgnPage.clickOnRoleDropDownButton();
        userMgnPage.selectDropDownOptions(data.roleselection);
        userMgnPage.fillFirstNameField(data.firstName);
        userMgnPage.fillMiddleNameField(data.middleName);
        userMgnPage.fillLastNameField(data.lastName);
        userMgnPage.fillLoginIdField(data.loginID);
        userMgnPage.fillPasswordField(data.pwd);
        userMgnPage.fillEmailIdField(data.email);
        //userMgnPage.clickOnCancelButton();
        userMgnPage.clickOnCreateButton();
      }).result.testInfo = { testId: id, data: data, feature: 'USERMANAGEMENT', dataProvider: 'positiveTests' };
  });

});