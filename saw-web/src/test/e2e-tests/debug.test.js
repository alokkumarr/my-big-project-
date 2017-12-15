const login = require('../javascript/pages/common/login.po.js');
const analyze = require('../javascript/pages/common/analyzePage.po.js');

describe('Debug logout', () => {

  it('Log in + log out', () => {
    login.loginAs('admin');
    analyze.main.doAccountAction('logout');
  });

});
