const path = require('path');
const webpackMerge = require('webpack-merge');
const webpackHelper = require('./webpack.helper');
const commonConfig = require('./webpack.common.js');
const pkg = require('../package.json');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

const vendorKeys = Object.keys(pkg.dependencies).map(key => {
  // devextreme has no index.js or a main set in package.json, so we have to manually select the main file
  if (key === 'devextreme') {
    return path.join(key, 'ui', 'data_grid');
  }

  return key;
});

const styleGuideChunks = ['vendor', 'styleguide'];

/**
 * Webpack configuration
 */
module.exports = webpackMerge(commonConfig, {
  entry: {
    styleguide: './styleguide/index',
    vendor: vendorKeys
  },

  output: {
    path: webpackHelper.root('build/styleguide')
  },

  module: {
    rules: [
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
    new CleanWebpackPlugin(['build/styleguide'], {
      root: webpackHelper.root(),
      verbose: true
    }),
    new DefinePlugin({
      '__DEVELOPMENT__': false
    }),
    new HtmlWebpackPlugin({
      template: 'styleguide/index.html',
      filename: 'index.html',
      favicon: webpackHelper.root('assets/img/favicon.png'),
      hash: true,
      chunks: styleGuideChunks,
      chunksSortMode: webpackHelper.sortChunks(styleGuideChunks)
    }),
    new CommonsChunkPlugin({
      names: ['vendor'],
      minChunks: Infinity
    })
  ],

  devServer: {
    port: 3001,
    host: 'localhost',
    historyApiFallback: true,
    watchOptions: {
      aggregateTimeout: 300,
      poll: 1000
    },
    outputPath: webpackHelper.root('build/styleguide')
  }
});
