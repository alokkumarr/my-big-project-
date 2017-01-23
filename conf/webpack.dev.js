const webpackMerge = require('webpack-merge');
const webpackHelper = require('./webpack.helper');
const mainConfig = require('./webpack.main.js');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

/**
 * Webpack configuration
 */
module.exports = webpackMerge(mainConfig, {
  output: {
    sourceMapFilename: '[file].map'
  },

  devtool: 'source-map',

  module: {
    rules: [
      {
        test: /\.(css|scss)$/,
        loaders: ExtractTextPlugin.extract({
          fallbackLoader: 'style-loader',
          loader: 'css-loader!sass-loader!postcss-loader'
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
      filename: 'login.html',
      hash: true,
      chunks: ['login']
    })
  ],

  devServer: {
    port: 3000,
    host: 'localhost',
    historyApiFallback: true,
    watchOptions: {
      aggregateTimeout: 300,
      poll: 1000
    },
    outputPath: webpackHelper.root('build/dist')
  }
});
