const loginPage = require('../javascript/pages/loginPage.po.js');
const header = require('../javascript/pages/components/header.co.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const users = require('../javascript/data/users.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../conf/protractor.conf');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
var fs = require('fs');

describe('Login Tests: login.test.js', () => {

  //Prerequisites: two users should exist with user types: admin and user
  const userDataProvider = {
    'admin': {user: users.admin.loginId}, // SAWQA-1
    'user': {user: users.userOne.loginId} // SAWQA-5
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    //commonFunctions.logOutByClearingLocalStorage();
  });

  using(userDataProvider, function (data, description) {
    it('Should successfully logged in by ' + description, function () {
      loginPage.userLogin(data.user, users.anyUser.password);
      expect(header.headerElements.companyLogo.isPresent()).toBeTruthy();
    });
  });
});
