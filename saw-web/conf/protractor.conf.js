const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;

exports.config = {
  framework: 'jasmine2',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  directConnect: true,

  getPageTimeout: 5000000, // enable for regular testing
  allScriptsTimeout: 5000000, // enable for regular testing
  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: [
        'disable-extensions',
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        '--headless',
        '--disable-gpu',
        '--window-size=2880,1800'
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
    charts: [
      webpackHelper.root('src/test/e2e-tests/charts/applyFiltersToCharts.js'),
      webpackHelper.root('src/test/e2e-tests/charts/createAndDeleteCharts.test.js'),
      webpackHelper.root('src/test/e2e-tests/charts/previewForCharts.test.js')
    ],
    root: [
      webpackHelper.root('src/test/e2e-tests/analyze.test.js'),
      webpackHelper.root('src/test/e2e-tests/createPivot.test.js'),
      webpackHelper.root('src/test/e2e-tests/createReport.test.js'),
      //webpackHelper.root('src/test/e2e-tests/debug.test.js') // for testing purposes
      webpackHelper.root('src/test/e2e-tests/login.test.js'),
      webpackHelper.root('src/test/e2e-tests/priviliges.test.js'),
    ]
  },

  onPrepare() {
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.DEFAULT_TIMEOUT_INTERVAL = 5000000;
    jasmine.getEnv().defaultTimeoutInterval = 10000000; //another option if above doesn't work
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000000;
    browser.manage().timeouts().pageLoadTimeout(600000);
    browser.manage().timeouts().implicitlyWait(10000);
    let jasmineReporters = require('jasmine-reporters');
    let junitReporter = new jasmineReporters.JUnitXmlReporter({

      // setup the output path for the junit reports
      // should create folder in advance
      savePath: 'target/protractor-reports',

      // conslidate all true:
      //   output/junitresults.xml
      //
      // conslidate all set to false:
      //   output/junitresults-example1.xml
      //   output/junitresults-example2.xml
      consolidateAll: true

    });
    jasmine.getEnv().addReporter(junitReporter);


    jasmine.DEFAULT_TIMEOUT_INTERVAL = 600000;
    jasmine.getEnv().defaultTimeoutInterval = 600000; //another option if above doesn't work

    browser.manage().timeouts().pageLoadTimeout(600000);
    browser.manage().timeouts().implicitlyWait(600000);
    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.driver.get('http://localhost:3000');

    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, 600000);
  }
};
