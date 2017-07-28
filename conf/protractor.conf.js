const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
console.log('platform: ', process.platform);

const windowsPath = '../node_modules/chromedriver/lib/chromedriver/chromedriver.exe';
const unixPath = '../node_modules/protractor/bin/chromedriver';
const chromedriverPath = process.platform === 'win32' ? windowsPath : unixPath;

exports.config = {
  framework: 'jasmine2',
  chromeDriver: chromedriverPath,
  //seleniumAddress: 'http://localhost:4444/wd/hub',
  getPageTimeout: 60000,
  allScriptsTimeout: 500000,
  directConnect: true,

  multiCapabilities: [

    {
      browserName: 'chrome',
       chromeOptions: {
        args: [
          //'incognito',
          'disable-extensions',
          'disable-web-security'
        ]
      }
    },

/*    {
      browserName: 'firefox'
    }*/
  ],

  //maxSessions: 2,

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
      webpackHelper.root('src/test/javascript/e2e/spec/analyses.spec.js')
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
