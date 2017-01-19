const chalk = require('chalk');
const webpackHelper = require('./webpack.helper');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const NoEmitOnErrorsPlugin = require('webpack/lib/NoEmitOnErrorsPlugin');
const LoaderOptionsPlugin = require('webpack/lib/LoaderOptionsPlugin');
const StyleLintPlugin = require('stylelint-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');

const autoprefixer = require('autoprefixer');

const MODULE_DIR = 'node_modules';

/**
 * Webpack configuration
 */
module.exports = {
  context: webpackHelper.root('src/main/javascript'),
  entry: {
    app: './app/index',
    login: './login/index'
  },
  output: {
    path: webpackHelper.root('dist'),
    filename: 'js/[name].bundle.js'
  },

  devtool: false,

  resolve: {
    modules: [MODULE_DIR, webpackHelper.root('src/main/javascript')],
    alias: {
      fonts: webpackHelper.root('assets/fonts'),
      img: webpackHelper.root('assets/img')
    }
  },

  resolveLoader: {
    modules: [MODULE_DIR]
  },

  module: {
    rules: [
      {
        enforce: 'pre',
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint-loader',
        options: {
          fix: false
        }
      },
      {
        enforce: 'pre',
        test: /\.html$/,
        loader: 'htmlhint-loader'
      },
      {
        test: /.json$/,
        loaders: 'json-loader'
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: 'ng-annotate-loader!babel-loader'
      },
      {
        test: /.html$/,
        loaders: 'html-loader'
      },
      {
        test: /\.(eot|woff|woff2|ttf)$/,
        loader: 'file-loader?name=fonts/[name].[ext]&publicPath=../'
      },
      {
        test: /\.(png|jpg|svg)$/,
        loader: 'file-loader?name=img/[name].[ext]&publicPath=../'
      }
    ]
  },

  plugins: [
    new NoEmitOnErrorsPlugin(),
    new ProgressBarPlugin({
      format: chalk.blue.bold('   build') + ' [:bar] ' + chalk.green.bold(':percent') + ' (:elapsed seconds) ',
      clear: false
    }),
    new LoaderOptionsPlugin({
      options: {
        postcss: [autoprefixer]
      }
    }),
    new StyleLintPlugin({
      configFile: webpackHelper.root('.stylelintrc')
    }),
    new ExtractTextPlugin('css/[name].css')
  ]
};
