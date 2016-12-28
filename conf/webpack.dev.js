const webpackMerge = require('webpack-merge');
const webpackHelper = require('./webpack.helper');
const commonConfig = require('./webpack.common.js');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

/**
 * Webpack configuration
 */
module.exports = webpackMerge(commonConfig, {
  output: {
    sourceMapFilename: '[file].map'
  },

  debug: true,
  devtool: 'source-map',
  noInfo: true,

  stats: {
    colors: true
  },

  module: {
    loaders: [
      {
        test: /\.(css|scss)$/,
        loaders: ExtractTextPlugin.extract({
          fallbackLoader: 'style',
          loader: 'css!sass!postcss'
        })
      }
    ]
  },

  plugins: [
    new DefinePlugin({
      '__DEVELOPMENT__': true
    }),
    new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'index.html',
      hash: true,
      chunks: ['app']
    }),
    new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login/index.html',
      hash: true,
      chunks: ['login']
    })
  ],

  eslint: {
    configFile: webpackHelper.root('conf/eslint-dev-rules.js')
  },

  devServer: {
    port: 3000,
    host: 'localhost',
    historyApiFallback: true,
    watchOptions: {
      aggregateTimeout: 300,
      poll: 1000
    },
    outputPath: webpackHelper.root('dist')
  }
});
