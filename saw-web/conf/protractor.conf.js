const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;

// Note: Prefix with "../saw-web" because end-to-end tests are invoked
// from "saw-dist" when run against the distribution package.  The
// same path also works when run directly out of "saw-web".
const testDir = '../saw-web/src/test';

exports.config = {
  framework: 'jasmine2',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  getPageTimeout: 600000,
  allScriptsTimeout: 500000,
  directConnect: true,

  capabilities: {
    // Workaround: If running against distribution package in
    // continuous integration, use Firefox until Chrome is available
    // on Bamboo agents.  When changing browser, also update in
    // "doc/development.md" and "doc/development-mac.md".
    browserName: webpackHelper.distRun() ? 'firefox' : 'chrome',
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
    },
    'moz:firefoxOptions': {
      args: ['--headless']
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

  suites: webpackHelper.distRun() ? {
    /* Suites for test run invoked from Maven which is used in Bamboo
     * continuous integration.  Note: In the long term there should
     * just be a single set of suites used everywhere (for both
     * continuous integration and local front-end development).
     * However, for now use a separate suite that allows enabling
     * known working tests (working reliably without flakiness)
     * incrementally one by one in continuous integration, while
     * working on fixing the rest.  */
    authentication: [],
    analyses: [
      webpackHelper.root(testDir + '/e2e-tests/goToAnalyze.test.js')
    ]
  } : {
    /* Suites for test run invoked from Protractor directly on local
     * saw-web front-end development server */
    authentication: [
      webpackHelper.root(testDir + '/e2e-tests/login.test.js')
    ],
    analyses: [
      /*webpackHelper.root(testDir + '/e2e-tests/priviliges.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/goToAnalyze.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/createChart.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/createPivot.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/createReport.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/charts/createAndDeleteCharts.test.js'),
      webpackHelper.root(testDir + '/e2e-tests/charts/previewForCharts.test.js')*/
      //webpackHelper.root(testDir + '/e2e-tests/debug.test.js') // for testing purposes
      //webpackHelper.root(testDir + '/javascript/e2e/spec/analyses.test.js'), // obsolete
    ]
  },

  onPrepare() {
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000000;
    //jasmine.getEnv().defaultTimeoutInterval = 10000000; //another option if above doesn't work
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
    browser.driver.get(webpackHelper.sawWebUrl());

    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, 600000);
  }
};
