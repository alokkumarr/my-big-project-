const login = require('../javascript/pages/loginPage.po');

describe('Generate data tests', function () {

  it('restler.js Validate if doAuthenticate returns token', function () {
    /*const rest = require('restler');

    const payload = {"masterLoginId": "user@email.com", "password": "pass"};
    let aToken;

    rest.postJson('http://localhost/security/doAuthenticate', payload).on('complete', function (data, response) {
      if (response.statusCode === 200) {
        browser.sleep(5000);
        console.log(data.aToken);
      } else {
        console.log("error");
      }
      console.log(response);
      console.log(data);
    });*/

    //console.log(aToken);
    login.loginAs("admin");
    browser.sleep(50000);
  });
});
