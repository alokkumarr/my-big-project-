const webpackHelper = require('./webpack.helper');
const DefinePlugin = require('webpack/lib/DefinePlugin');

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
      {
        enforce: 'pre',
        test: /\.html$/,
        loader: 'htmlhint-loader'
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
      },
      {
        test: /\.html$/,
        loader: 'html-loader'
      },
      {
        test: /\.(css|scss)$/,
        loader: 'null-loader'
      },
      {
        test: /\.(css|scss)$/,
        loader: 'raw-loader'
      },
      {
        test: /\.(eot|woff|woff2|ttf)$/,
        loader: 'null-loader'
      },
      {
        test: /\.(png|jpg|svg)$/,
        loader: 'null-loader'
      }
    ]
  }
};
