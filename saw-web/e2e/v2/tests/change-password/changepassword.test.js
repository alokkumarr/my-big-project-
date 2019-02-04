var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const ChangePassword = require('../../pages/ChangePassword');
const Header = require('../../pages/components/Header');
const commonFunctions = require('../../pages/utils/commonFunctions')

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

  using(testDataReader.testData['LOGIN']['positiveTests'] ? testDataReader.testData['LOGIN']['positiveTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {

        let loginPage = new LoginPage();
      loginPage.loginAs(data.user, /analyze/);
      let header = new Header();
      header.verifyLogo();
      header.doChangePassword();

      let changePwd = new ChangePassword();
      changePwd.fillOldPassword('Sawsyncnewuser1!');
      changePwd.fillNewPassword('Raja@1234');
    //  changePwd.fillConfirmPassword('Raja@123');
    //   browser.sleep(3000);
       changePwd.clickOnCancelButton();
      

    //   let loginPage = new LoginPage();
    //   let header = new Header();
    //   let changePwd = new ChangePassword();

    //  loginPzge.loginAs(data.user, /analyze/);
    //    header.verifyLogo();
    //   await header.doChangePassword();
    //   await browser.sleep(30000);
      
    //   await changePwd.fillOldPassword('Sawsyncnewuser1!');
    //   await changePwd.fillNewPassword('Raja@1234');
    //   await changePwd.fillConfirmPassword('Raja@123');
    //   await browser.sleep(3000);
    //   await changePwd.clickOnCancelButton();
      
      }).result.testInfo = { testId: id, data: data, feature: 'LOGIN', dataProvider: 'positiveTests' };
  });

//   using(testDataReader.testData['LOGIN']['positiveTests'] ? testDataReader.testData['LOGIN']['positiveTests'] : {}, (data, id) => {
//     it(`${id}:${data.description}`, () => {

//       let loginPage = new LoginPage();
//       loginPage.loginAs(data.user, /analyze/);
//       let header = new Header();
//       header.verifyLogo();

//     }).result.testInfo = { testId: id, data: data, feature: 'LOGIN', dataProvider: 'positiveTests' };
//   });

 /* using(testDataReader.testData['LOGIN']['negativeTests'] ? testDataReader.testData['LOGIN']['negativeTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {

      commonFunctions.goToHome();
      let loginPage = new LoginPage();
      loginPage.fillUserNameField(data.user);
      loginPage.fillPasswordField(data.password);
      loginPage.clickOnLoginButton();
      loginPage.verifyError(data.expected.message);

    }).result.testInfo = { testId: id, data: data, feature: 'LOGIN', dataProvider: 'negativeTests' };
  }); */

});
