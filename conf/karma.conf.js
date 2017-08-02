const webpackHelper = require('./webpack.helper');

module.exports = function (config) {
  config.set({
    basePath: webpackHelper.root('src/'),
    logLevel: config.LOG_INFO,
    frameworks: ['mocha', 'chai'],
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
      '../node_modules/angular/angular.js',
      '../node_modules/angular-mocks/angular-mocks.js',
      'test/javascript/specs/**/*.spec.js'
    ],
    exclude: [],
    plugins: [
      require('karma-chai'),
      require('karma-mocha'),
      require('karma-mocha-reporter'),
      require('karma-coverage'),
      require('karma-phantomjs-launcher'),
      require('karma-phantomjs-shim'),
      require('karma-webpack')
    ],
    preprocessors: {
      'test/javascript/specs/**/*.spec.js': ['webpack']
    },
    webpack: require('./webpack.test'),
    webpackMiddleware: {
      noInfo: false
    }
  });
};
