const webpackHelper = require('./webpack.helper');

const MODULE_DIR = 'node_modules';

module.exports = {
  resolve: {
    modules: [
      MODULE_DIR,
      webpackHelper.root('src/main/javascript')
    ]
  },

  module: {
    rules: [
      // preloaders
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint-loader',
        options: {
          fix: false,
          configFile: webpackHelper.root('conf/eslint-dev-rules.js')
        }
      },
      // loaders
      {
        test: /\.json$/,
        loader: 'json-loader'
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'ng-annotate-loader!babel-loader'
      }
    ]
  }
};
