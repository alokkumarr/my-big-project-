const login = require('../javascript/pages/loginPage.po.js');
const analyze = require('../javascript/pages/analyzePage.po.js');

describe('Debug logout', () => {

  it('Log in + log out', () => {
    login.loginAs('admin');
    analyze.main.doAccountAction('logout');
  });

});
