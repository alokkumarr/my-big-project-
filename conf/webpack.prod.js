const path = require('path');
const webpackMerge = require('webpack-merge');
const webpackHelper = require('./webpack.helper');
const mainConfig = require('./webpack.main.js');

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
    return path.join(key, 'ui', 'data_grid');
  }

  return key;
});

const appChunks = ['vendor', 'app'];
const loginChunks = ['vendor', 'login'];

module.exports = webpackMerge(mainConfig, {
  entry: {
    vendor: vendorKeys
  },

  module: {
    rules: [
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint-loader',
        options: {
          fix: false,
          configFile: webpackHelper.root('conf/eslint-prod-rules.js')
        }
      },
      {
        test: /\.(css|scss)$/,
        loaders: ExtractTextPlugin.extract({
          fallbackLoader: 'style-loader',
          loader: 'css-loader?minimize!sass-loader!postcss-loader'
        })
      }
    ]
  },

  plugins: [
    new CleanWebpackPlugin(['build/dist'], {
      root: webpackHelper.root(),
      verbose: true
    }),
    new DefinePlugin({
      '__DEVELOPMENT__': false
    }),
    new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'index.html',
      favicon: webpackHelper.root('assets/img/favicon.png'),
      hash: true,
      chunks: appChunks,
      chunksSortMode: webpackHelper.sortChunks(appChunks)
    }),
    new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login.html',
      favicon: webpackHelper.root('assets/img/favicon.png'),
      hash: true,
      chunks: loginChunks,
      chunksSortMode: webpackHelper.sortChunks(loginChunks)
    }),
    new CommonsChunkPlugin({
      names: ['vendor'],
      minChunks: Infinity
    })
  ]
});
