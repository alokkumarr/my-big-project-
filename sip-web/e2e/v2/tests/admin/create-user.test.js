let testDataReader = require('../../testdata/testDataReader');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const Header = require('../../pages/components/Header');
const SideMenuNav = require('../../pages/components/SideNav');
const UserManagement= require('../../pages/admin/UserManagementPage');
const commonFunctions = require('../../pages/utils/commonFunctions');
const users = require('../../helpers/data-generation/users');

  describe('Executing tests from User-Management.test.js', () => {
    beforeAll(() => {
      logger.info('Starting Create User Test Cases...');
      jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
    });

    beforeEach(done => {
      setTimeout(() => {
        done();
      }, protractorConf.timeouts.pageResolveTimeout);

    });

    afterEach(done => {
      logger.info('DeleteReport tests...');
      setTimeout(() => {
        // Logout by clearing the storage
        commonFunctions.clearLocalStorage();
        done();
      }, protractorConf.timeouts.pageResolveTimeout);
    });

    using(testDataReader.testData['ADMIN_MODULE']['createUser'] ? testDataReader.testData['ADMIN_MODULE']['createUser'] : {}, (data, id) => {
      it(`${id}:${data.description}`, () => {
        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        let header = new Header();
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        let userPage = new UserManagement();
        let firstName = userPage.generateRandomString(data.firstName,data.firstNameLength);
        let middleName = userPage.generateRandomString(data.middleName,data.middleNameLength);
        let lastName = userPage.generateRandomString(data.lastName,data.middleNameLength);
        let loginId = userPage.generateRandomString(data.loginId,data.loginIdLength);
        let passWord = userPage.generateRandomString(data.password,data.passwordLength);
        let emailId = userPage.generateRandomString(data.email,data.emailIdLength);

        //Create User
        userPage.addUser();
        userPage.clickRole();
        userPage.chooseRole(data.roleType);
        userPage.fillFirstName(firstName);
        userPage.fillMiddleName(middleName);
        userPage.fillLastName(lastName);
        userPage.fillLoginId(loginId);
        userPage.fillPassword(passWord);
        userPage.fillEmail(emailId.concat("e2e@email.com"));
        userPage.clickStatus();
        userPage.selectStatus(data.status);
        userPage.createUser();

        //verify created User is able to login and check Admin privileges
        if(data.loginRequired){
          header.doLogout();
          loginPage.doLogin(loginId,passWord);
          if(data.status === "ACTIVE")
          {
            header.clickOnModuleLauncher();
            header.statusOfAdminLink(data.roleType);
            header.doLogout();
          }else {
            loginPage.verifyError(data.errorMessage);
          }
          loginPage.doLogin(users[data.user].loginId,users[data.user].password);
          header.clickOnModuleLauncher();
          header.clickOnAdminLink();
        }

        //Delete User
        userPage.searchUser(loginId);
        userPage.deleteUser(loginId);
        header.doLogout();
      }).result.testInfo = { testId: id, data: data, feature: 'ADMIN_MODULE', dataProvider: 'createUser' };
    });

    using(testDataReader.testData['ADMIN_MODULE']['negativeTest'] ? testDataReader.testData['ADMIN_MODULE']['negativeTest'] : {}, (data, id) => {
      it(`${id}:${data.description}`, () => {

        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        let header = new Header();
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        let userPage = new UserManagement();

        //Create User
        userPage.addUser();
        userPage.clickRole();
        userPage.chooseRole(data.roleType);
        userPage.fillFirstName(data.firstName);
        userPage.fillMiddleName(data.middleName);
        userPage.fillLastName(data.lastName);
        userPage.fillLoginId(data.loginId);

        //validate Email
        userPage.fillEmail(data.invalidEmail);
        userPage.validateEmail("true");
        userPage.fillEmail(data.email);
        userPage.validateEmail("false");

        // Validating Password
        userPage.fillPassword(data.spacePassword);
        userPage.fillEmail(data.email);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.lowercasePassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.uppercasePassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.lowerUpperCasePassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.lowercaseNumberPassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.uppercaseNumberPassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.numberPassword);
        userPage.createUser();
        userPage.validatePassword(data.passwordStatus);
        userPage.validatePasswordErrorText(data.errorMessage);

        userPage.fillPassword(data.password);
        userPage.clickStatus();
        userPage.selectStatus(data.status);
        userPage.createUser();

        //Delete User
        userPage.searchUser(data.loginId);
        userPage.deleteUser(data.loginId);
        header.doLogout();
      }).result.testInfo = { testId: id, data: data, feature: 'ADMIN_MODULE', dataProvider: 'negativeTest' };
    });
  });
