let testDataReader = require('../../testdata/testDataReader');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const Header = require('../../pages/components/Header');
const RoleManagement = require('../../pages/admin/RoleManagementPage');
const SideMenuNav = require('../../pages/admin/SideMenuNav');
const UserManagement= require('../../pages/admin/UserManagementPage');
const PrivilegeManagement = require('../../pages/admin/PrivilageManagementPage');
const commonFunctions = require('../../pages/utils/commonFunctions');
const APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const users = require('../../helpers/data-generation/users');

  describe('Executing tests from User-Management.test.js', () => {
    beforeAll(() => {
      logger.info("Executing Admin Page User Management tests - Create Role & User...");
      jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
    });

    beforeEach(done => {
      setTimeout(() => {
        done();
      }, protractorConf.timeouts.pageResolveTimeout);

    });

    afterEach(done => {
      logger.info('Clear Storage...');
      setTimeout(() => {
        // Logout by clearing the storage
        commonFunctions.clearLocalStorage();
        done();
      }, protractorConf.timeouts.pageResolveTimeout);
    });

    using(testDataReader.testData['ADMIN_MODULE']['positiveTest'] ? testDataReader.testData['ADMIN_MODULE']['positiveTest'] : {}, (data, id) => {
      it(`${id}:${data.description}`, () => {

        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        let header = new Header();
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        let userMgnPage = new UserManagement();
        let sideMenuNav = new SideMenuNav();
        let roleManagement = new RoleManagement();
        let privilegeManagementPage = new PrivilegeManagement();

        //Validate Role Field and Create Role
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.clickAddRole();
        roleManagement.clickRoleName();
        roleManagement.fillRoleDescription(data.roleDescription);
        roleManagement.clickStatus();
        roleManagement.selectStatus(data.activeStatus);
        roleManagement.clickRoleType();
        roleManagement.selectRoleType(data.roleType);
        roleManagement.validateRole("true");
        roleManagement.fillRoleName(data.role);
        roleManagement.validateRole("false");
        roleManagement.clickCreateRole();

        //Create User
        sideMenuNav.clickMenu();
        sideMenuNav.clickUser();
        userMgnPage.clickNewUser();
        userMgnPage.clickRole();
        userMgnPage.selectRole(data.role);
        userMgnPage.fillFirstName(data.firstName);
        userMgnPage.fillMiddleName(data.middleName);
        userMgnPage.fillLastName(data.lastName);
        userMgnPage.fillLoginId(data.loginid);

        //validate Email
        userMgnPage.fillEmailId(data.invalidEmail);
        userMgnPage.validateEmail("true");
        userMgnPage.fillEmailId(data.email);
        userMgnPage.validateEmail("false");

        // Validating Password
        userMgnPage.fillPassword(data.spacePassword);
        userMgnPage.fillEmailId(data.email);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.lowercasePassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.uppercasePassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.lowerUpperCasePassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.lowercaseNumberPassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.uppercaseNumberPassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);

        userMgnPage.fillPassword(data.numberPassword);
        userMgnPage.clickCreate();
        userMgnPage.validatePassword(data.passwordStatus);
        userMgnPage.validatePasswordErrorText(data.errorMessage);
        userMgnPage.fillPassword(data.pwd);
        userMgnPage.clickStatus();
        userMgnPage.selectStatus(data.activeStatus);
        userMgnPage.clickCreate();
        loginPage.clickAccountSettings();
        loginPage.clickLogout();

        if(data.roleType === "ADMIN") {
          loginPage.doLogin(data.loginid,data.pwd);
        } else {
          loginPage.doLogin(data.loginid,data.pwd);
          header.verifyLogo();
          header.clickOnModuleLauncher();
          header.verifyStatusOfAdminLink();
          loginPage.clickAccountSettings();
          loginPage.clickLogout();
          loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        }

        //Delete the Admin User and Role
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        userMgnPage.DeleteCreatedUser(data.email);
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.selectCreatedRole(data.role);
        privilegeManagementPage.deletePrivilege();
        privilegeManagementPage.clickConfirmDelete();
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.deleteRole(data.role);
        roleManagement.clickConfirmDelete();
        loginPage.clickAccountSettings();
        loginPage.clickLogout();
      }).result.testInfo = { testId: id, data: data, feature: 'ADMIN_MODULE', dataProvider: 'positiveTest' };
    });

    using(testDataReader.testData['ADMIN_MODULE']['negativeTest'] ? testDataReader.testData['ADMIN_MODULE']['negativeTest'] : {}, (data, id) => {
        it(`${id}:${data.description}`, () => {
        let loginPage = new LoginPage();
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        let header = new Header();
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        let userMgnPage = new UserManagement();
        let sideMenuNav = new SideMenuNav();
        let roleManagement = new RoleManagement();
        let privilegeManagementPage = new PrivilegeManagement();

        //Create Role
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.clickAddRole();
        roleManagement.fillRoleName(data.role);
        roleManagement.fillRoleDescription(data.roleDescription);
        roleManagement.clickStatus();
        roleManagement.selectStatus(data.inactiveStatus);
        roleManagement.clickRoleType();
        roleManagement.selectRoleType(data.roleType);
        roleManagement.clickCreateRole();

        //verify the status of created Role
        sideMenuNav.clickMenu();
        sideMenuNav.clickUser();
        userMgnPage.clickNewUser();
        userMgnPage.clickRole();
        userMgnPage.validateStatusOfCreatedRole(data.role);
        userMgnPage.clickCancel();

        //Edit Role
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.editCreatedRole(data.role);
        roleManagement.clickStatus();
        roleManagement.selectStatus(data.activeStatus);
        roleManagement.clickCreate();

        //Create User with Inactive Status
        sideMenuNav.clickMenu();
        sideMenuNav.clickUser();
        userMgnPage.clickNewUser();
        userMgnPage.clickRole();
        userMgnPage.selectRole(data.role);
        userMgnPage.fillFirstName(data.firstName);
        userMgnPage.fillMiddleName(data.middleName);
        userMgnPage.fillLastName(data.lastName);
        userMgnPage.fillLoginId(data.loginid);
        userMgnPage.fillPassword(data.pwd);
        userMgnPage.fillEmailId(data.email);
        userMgnPage.clickStatus();
        userMgnPage.selectStatus(data.inactiveStatus);
        userMgnPage.clickCreate();
        loginPage.clickAccountSettings();
        loginPage.clickLogout();

        //Delete the Created User and Role
        loginPage.doLogin(data.loginid,data.pwd);
        loginPage.verifyErrorMessage("Invalid User Credentials");
        loginPage.doLogin(users[data.user].loginId,users[data.user].password);
        header.verifyLogo();
        header.clickOnModuleLauncher();
        header.clickOnAdminLink();
        userMgnPage.DeleteCreatedUser(data.email);
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.selectCreatedRole(data.role);
        privilegeManagementPage.deletePrivilege();
        privilegeManagementPage.clickConfirmDelete();
        sideMenuNav.clickMenu();
        sideMenuNav.clickRole();
        roleManagement.deleteRole(data.role);
        roleManagement.clickConfirmDelete();
        loginPage.clickAccountSettings();
        loginPage.clickLogout();
      }).result.testInfo = { testId: id, data: data, feature: 'ADMIN_MODULE', dataProvider: 'negativeTest' };
    });
  });
