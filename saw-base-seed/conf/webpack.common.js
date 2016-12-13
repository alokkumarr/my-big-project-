const webpackHelper = require('./webpack.helper');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const NoErrorsPlugin = require('webpack/lib/NoErrorsPlugin');
const OccurrenceOrderPlugin = require('webpack/lib/optimize/OccurrenceOrderPlugin');
const StyleLintPlugin = require('stylelint-webpack-plugin');

const autoprefixer = require('autoprefixer');

const MODULE_DIR = 'node_modules';

/**
 * Webpack configuration
 */
module.exports = {
  context: webpackHelper.root('src'),
  entry: {
    app: './app/index',
    login: './login/index'
  },
  output: {
    path: webpackHelper.root('dist'),
    filename: '[name].bundle.js',
    chunkFilename: '[id].bundle.js'
  },

  debug: false,
  devtool: null,

  resolve: {
    modulesDirectories: [MODULE_DIR, webpackHelper.root('src')]
  },

  resolveLoader: {
    modulesDirectories: [MODULE_DIR],
    moduleTemplates: ['*-loader'],
    extensions: ['', '.js']
  },

  module: {
    preLoaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint'
      },
      {
        test: /\.html$/,
        loader: 'htmlhint'
      }
    ],

    loaders: [
      {
        test: /.json$/,
        loaders: 'json'
      },
      {
        test: /\.(css|scss)$/,
        loaders: [
          'style',
          'css',
          'postcss',
          'sass'
        ]
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: [
          'ng-annotate',
          'babel'
        ]
      },
      {
        test: /.html$/,
        loaders: 'html'
      },
      {
        test: /\.(eot|woff|woff2|ttf|svg|png|jpg)$/,
        loader: 'url-loader?limit=30000&name=[name]-[hash].[ext]'
      }
    ]
  },

  plugins: [
    new OccurrenceOrderPlugin(),
    new NoErrorsPlugin(),
    new StyleLintPlugin({
      configFile: webpackHelper.root('.stylelintrc')
    })
  ],

  postcss: () => {
    return [autoprefixer];
  }
};
