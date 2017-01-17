const webpackHelper = require('./webpack.helper');

module.exports = function (config) {
  config.set({
    basePath: webpackHelper.root('src/test/javascript'),
    logLevel: config.LOG_INFO,
    frameworks: ['jasmine'],
    browsers: ['PhantomJS'],
    reporters: ['progress', 'coverage'],
    junitReporter: {
      outputDir: 'test-reports'
    },
    coverageReporter: {
      dir: webpackHelper.root('coverage'),
      type: 'html'
    },
    port: 9876,
    files: [
      'specs/**/*.js'
    ],
    exclude: [],
    plugins: [
      require('karma-jasmine'),
      require('karma-junit-reporter'),
      require('karma-coverage'),
      require('karma-phantomjs-launcher'),
      require('karma-phantomjs-shim'),
      require('karma-webpack')
    ],
    preprocessors: {
      'specs/**/*.js': ['webpack']
    },
    webpack: require('./webpack.test'),
    webpackMiddleware: {
      noInfo: true
    }
  });
};
