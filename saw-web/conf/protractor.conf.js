const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
// Timeouts:
/**
 * Sets the amount of time to wait for a page load to complete before returning
 * an error.  If the timeout is negative, page loads may be indefinite.
 */
const pageLoadTimeout = 30000;

/**
 * Specifies the amount of time the driver should wait when searching for an
 * element if it is not immediately present.
 */
const implicitlyWait = 10000;

/**
 * Default time to wait in ms before a test fails
 * Fixes error: jasmine default timeout interval
 */
const defaultTimeoutInterval = 10000;

/**
 * Fixes error: Timed out waiting for asynchronous Angular tasks to finish after n seconds
 */
const allScriptsTimeout = 30000;

exports.config = {
  allScriptsTimeout: allScriptsTimeout,
  framework: 'jasmine2',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  directConnect: true,
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
    defaultTimeoutInterval: defaultTimeoutInterval,
    isVerbose: true,
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

    browser.manage().timeouts().pageLoadTimeout(pageLoadTimeout);
    browser.manage().timeouts().implicitlyWait(implicitlyWait);

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


    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.driver.get('http://localhost:3000');

    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, 30000);
  }
};
