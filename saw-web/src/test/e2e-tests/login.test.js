const login = require('../javascript/pages/loginPage.po.js');
const header = require('../javascript/pages/components/header.co.js');
const analyze = require('../javascript/pages/analyzePage.po.js');
const users = require('../javascript/data/users.js');
const using = require('jasmine-data-provider');

describe('Login Tests: login.test.js', () => {

  //Prerequisites: two users should exist with user types: admin and user
    const userDataProvider = {
      'admin': {user: users.admin.loginId},
      'user': {user: users.userOne.loginId},
    };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 600000;
  });

    afterAll(function () {
      browser.executeScript('window.sessionStorage.clear();');
      browser.executeScript('window.localStorage.clear();');
    });

    using(userDataProvider, function (data, description) {
      it('Should successfully logged in by ' + description, function() {
        expect(browser.getCurrentUrl()).toContain('/login');
        login.userLogin(data.user, users.anyUser.password);
        expect(header.headerElements.companyLogo.isPresent()).toBeTruthy();
        analyze.main.doAccountAction('logout');
      });
    });
});
