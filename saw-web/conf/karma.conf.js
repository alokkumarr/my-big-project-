const webpackHelper = require('./webpack.helper');

module.exports = function (config) {
  config.set({
    basePath: webpackHelper.root('src/'),
    logLevel: config.LOG_INFO,
    frameworks: ['mocha', 'chai'],
    browsers: ['PhantomJS'],
    reporters: ['progress', 'coverage', 'junit'],
    coverageReporter: {
      dir: webpackHelper.root('coverage'),
      type: 'html'
    },
    junitReporter: {
      outputDir: webpackHelper.root(),
      outputFile: 'junit.xml',
      useBrowserName: false
    },
    port: 9876,
    files: [
      'test/javascript/specs/polyfills.js',
      '../node_modules/angular/angular.js',
      '../node_modules/angular-mocks/angular-mocks.js',
      '../node_modules/reflect-metadata/Reflect.js',
      '../node_modules/zone.js/dist/zone.js',
      '../node_modules/zone.js/dist/long-stack-trace-zone.js',
      '../node_modules/zone.js/dist/async-test.js',
      '../node_modules/zone.js/dist/fake-async-test.js',
      '../node_modules/zone.js/dist/sync-test.js',
      '../node_modules/zone.js/dist/proxy.js',
      '../node_modules/zone.js/dist/mocha-patch.js',
      'test/javascript/specs/**/*.spec.*'
    ],
    exclude: [],
    plugins: [
      require('karma-chai'),
      require('karma-mocha'),
      require('karma-mocha-reporter'),
      require('karma-coverage'),
      require('karma-junit-reporter'),
      require('karma-phantomjs-launcher'),
      require('karma-phantomjs-shim'),
      require('karma-webpack')
    ],
    preprocessors: {
      'test/javascript/specs/polyfills.js': ['webpack'],
      'test/javascript/specs/**/*.spec.js': ['webpack'],
      'test/javascript/specs/**/*.spec.ts': ['webpack']
    },
    webpack: require('./webpack.test')('development'),
    webpackMiddleware: {
      noInfo: false
    }
  });
};
