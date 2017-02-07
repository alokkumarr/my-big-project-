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
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint-loader'
      },
      {
        test: /.json$/,
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
