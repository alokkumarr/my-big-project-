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
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        "--headless",
        "--disable-gpu",
        "--window-size=2880,1800"
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
      webpackHelper.root('src/test/e2e-tests/login.test.js')
    ],

    analyses: [
      webpackHelper.root('src/test/e2e-tests/priviliges.test.js'),
      webpackHelper.root('src/test/e2e-tests/goToAnalyze.test.js'),
      webpackHelper.root('src/test/e2e-tests/createChart.test.js'),
      webpackHelper.root('src/test/e2e-tests/createPivot.test.js'),
      webpackHelper.root('src/test/e2e-tests/createReport.test.js'),
      //webpackHelper.root('src/test/javascript/e2e/spec/analyses.test.js'), // obsolete
      //webpackHelper.root('src/test/e2e-tests/debug.test.js') // for testing purposes
      webpackHelper.root('src/test/e2e-tests/charts/createAndDeleteCharts.test.js')


    ]
  },

  onPrepare() {
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.DEFAULT_TIMEOUT_INTERVAL = 500000;
    jasmine.getEnv().defaultTimeoutInterval = 500000; //another option if above doesn't work

    browser.manage().timeouts().pageLoadTimeout(30000);
    browser.manage().timeouts().implicitlyWait(10000);
    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.driver.get('http://localhost:3000');

    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, 30000);
  }
};
