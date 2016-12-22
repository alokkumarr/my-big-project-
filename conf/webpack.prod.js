const webpackMerge = require('webpack-merge');
const indexOf = require('lodash/indexOf');
const gte = require('lodash/gte');
const path = require('path');
const webpackHelper = require('./webpack.helper');
const commonConfig = require('./webpack.common.js');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

const pkg = require('../package.json');

const vendorKeys = Object.keys(pkg.dependencies).map(key => {
  // devextreme has no index.js or a main set in package.json, so we have to manually select the main file
  if (key === 'devextreme') {
    return path.join(key, 'client_exporter');
  }

  return key;
});

const appChunks = ['vendor', 'app'];
const loginChunks = ['vendor', 'login'];

module.exports = webpackMerge(commonConfig, {
  entry: {
    vendor: vendorKeys
  },

  module: {
    loaders: [
      {
        test: /\.(css|scss)$/,
        loaders: ExtractTextPlugin.extract({
          fallbackLoader: 'style',
          loader: 'css?minify!sass!postcss'
        })
      }
    ]
  },

  plugins: [
    new CleanWebpackPlugin(['dist'], {
      root: webpackHelper.root(),
      verbose: true
    }),
    new DefinePlugin({
      '__DEVELOPMENT__': false
    }),
    new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'app.html',
      hash: true,
      chunks: appChunks,
      chunksSortMode: webpackHelper.sortChunks(appChunks)
    }),
    new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login.html',
      hash: true,
      chunks: loginChunks,
      chunksSortMode: webpackHelper.sortChunks(loginChunks)
    }),
    // new CommonsChunkPlugin({
    //   names: ['vendor'],
    //   minChunks: Infinity
    // })
  ],

  eslint: {
    configFile: webpackHelper.root('conf/eslint-prod-rules.js')
  }
});
