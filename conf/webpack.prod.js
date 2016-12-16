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

const pkg = require('../package.json');

const vendorKeys = Object.keys(pkg.dependencies).map(key => {
  // devextreme has no index.js or a main set in package.json, so we have to manually select the main file
  if (key === 'devextreme') {
    return path.join(key, 'client_exporter');
  }

  return key;
});

// a common chunk plugin used in app and in login as well
const commonVendorKeys = ['angular', 'angular-material'];
// vendor libraries used only in app, without the libs that are in the common chunk plugin
const appOnlyVendorKeys = vendorKeys.filter(key => gte(0, indexOf(commonVendorKeys, key)));

module.exports = webpackMerge(commonConfig, {
  entry: {
    vendor: appOnlyVendorKeys,
    commonVendor: commonVendorKeys
  },

  output: {
    chunkFilename: '[id].bundle.js'
  },

  plugins: [
    new DefinePlugin({
      '__DEVELOPMENT__': false
    }),
    new HtmlWebpackPlugin({
      template: webpackHelper.root('src/app/index.html'),
      filename: 'index.html',
      hash: true,
      chunks: ['app', 'commonVendor', 'vendor']
    }),
    new HtmlWebpackPlugin({
      template: webpackHelper.root('src/login/index.html'),
      filename: 'login.html',
      hash: true,
      chunks: ['login', 'commonVendor']
    }),
    new CommonsChunkPlugin({
      names: ['commonVendor'],
      minChunks: Infinity
    })
  ],

  eslint: {
    configFile: webpackHelper.root('src/prod-eslint-rules.js')
  }
});
