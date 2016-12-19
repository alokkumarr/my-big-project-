const webpackHelper = require('./webpack.helper');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const NoErrorsPlugin = require('webpack/lib/NoErrorsPlugin');
const OccurrenceOrderPlugin = require('webpack/lib/optimize/OccurrenceOrderPlugin');
const StyleLintPlugin = require('stylelint-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

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
    filename: '[name].bundle.js'
  },

  debug: false,
  devtool: null,

  resolve: {
    modulesDirectories: [MODULE_DIR, webpackHelper.root('src/main/javascript')]
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
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: 'ng-annotate!babel'
      },
      {
        test: /.html$/,
        loaders: 'html'
      },
      {
        test: /\.(eot|woff|woff2|ttf)$/,
        loader: 'file?name=/fonts/[name].[ext]'
      },
      {
        test: /\.(png|jpg|svg)$/,
        loader: 'file?name=/img/[name].[ext]'
      }
    ]
  },

  plugins: [
    new OccurrenceOrderPlugin(),
    new NoErrorsPlugin(),
    new StyleLintPlugin({
      configFile: webpackHelper.root('.stylelintrc')
    }),
    new ExtractTextPlugin('/css/[name]-[contenthash].css')
  ],

  postcss: () => {
    return [autoprefixer];
  }
};
