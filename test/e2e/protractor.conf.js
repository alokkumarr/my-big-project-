exports.config = {
  framework: 'jasmine2',
  seleniumAddress: 'http://localhost:4444/wd/hub',
  getPageTimeout: 60000,
  allScriptsTimeout: 500000,
  directConnect: true,

  capabilities: {
    'browserName'  : 'chrome',
    'chromeOptions': {
      args: [
        'incognito',
        'disable-extensions',
        'disable-web-security'
      ]
    }
  },

  jasmineNodeOpts: {
    isVerbose             : true,
    defaultTimeoutInterval: 120000,
    showTiming            : true,
    includeStackTrace     : true,
    realtimeFailure       : true,
    showColors            : true
  },

  suites: {
    analyses: ['spec/analyses.spec.js']
  },

  baseURL: 'http://localhost:3000'

};
