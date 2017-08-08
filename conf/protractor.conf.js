const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;

exports.config = {
  framework: 'jasmine2',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  getPageTimeout: 60000,
  allScriptsTimeout: 500000,
  directConnect: true,

  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: [
        //'incognito',
        'disable-extensions',
        'disable-web-security'
      ]
    }
  },

  jasmineNodeOpts: {
    isVerbose: true,
    defaultTimeoutInterval: 120000,
    showTiming: true,
    includeStackTrace: true,
    realtimeFailure: true,
    showColors: true
  },

  suites: {

    authentication: [
      webpackHelper.root('src/test/javascript/e2e/spec/login.spec.js')
    ],

    analyses: [
      // webpackHelper.root('src/test/javascript/e2e/spec/analyses.spec.js'),
      webpackHelper.root('src/test/javascript/e2e/spec/goToAnalyze.spec'),
      // webpackHelper.root('src/test/javascript/e2e/spec/createChart.spec.js')
      webpackHelper.root('src/test/javascript/e2e/spec/createPivot.spec.js')
    ]
  },

  onPrepare() {
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    browser.manage().timeouts().pageLoadTimeout(10000);
    browser.manage().timeouts().implicitlyWait(10000);
    browser.driver.manage().window().maximize();
    browser.driver.get('http://localhost:3000');
    browser.sleep(2000);
  }
};
