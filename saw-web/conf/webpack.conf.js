const webpackHelper = require('./webpack.helper');
const autoprefixer = require('autoprefixer');
const chalk = require('chalk');
const path = require('path');

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const NoEmitOnErrorsPlugin = require('webpack/lib/NoEmitOnErrorsPlugin');
const LoaderOptionsPlugin = require('webpack/lib/LoaderOptionsPlugin');
const UglifyJsPlugin = require('webpack/lib/optimize/UglifyJsPlugin');

const StyleLintPlugin = require('stylelint-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const WebpackBuildVersion = require('./webpack.version');

const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = function (env) {
  const isDevelopment = env === 'development';
  const isProduction = env === 'production';
  const enableMock = true;

  const MODULE_DIR = 'node_modules';
  const BUILD_DIR = 'dist/'

  /**
   * Webpack configuration
   */
  const conf = {
    context: webpackHelper.root('src/main/javascript'),
    entry: {
      app: './app/index',
      login: './login/index'
    },

    output: {
      path: webpackHelper.root(BUILD_DIR),
      filename: 'js/[name].bundle.js',
      sourceMapFilename: isDevelopment ? '[file].map' : ''
    },

    devtool: isDevelopment ? 'source-map' : false,

    resolve: {
      extensions: [".ts", ".js"],
      modules: [MODULE_DIR, webpackHelper.root('src/main/javascript')],
      alias: {
        fonts: webpackHelper.root('assets/fonts'),
        img: webpackHelper.root('assets/img'),
        api: webpackHelper.root('src/main/mock/api')
      }
    },

    resolveLoader: {
      modules: [MODULE_DIR]
    },

    module: {
      exprContextCritical: false,
      rules: [
        // preloaders
        {
          enforce: 'pre',
          test: /\.[jt]s$/,
          exclude: /node_modules/,
          loader: 'tslint-loader',
          options: {
            fix: false,
            typeCheck: false, // tslint-loader is way too slow with this enabled. Use pre-push hook for typechecking
            tsConfigFile: webpackHelper.root('tsconfig.json'),
            configFile: isDevelopment ?
              webpackHelper.root('conf/tslint-dev.json') :
              webpackHelper.root('conf/tslint-prod.json')
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
          test: /\.[jt]s$/,
          exclude: /node_modules/,
          loaders: ['ng-annotate-loader', {
            loader: 'ts-loader',
            options: {
              configFile: webpackHelper.root('conf/tsconfig.json')
            }
          }]
        },
        {
          test: /\.html$/,
          loader: 'html-loader'
        },
        {
          test: /\.(css|scss)$/,
          loader: ExtractTextPlugin.extract({
            fallback: 'style-loader',
            use: [
              {
                loader: 'css-loader',
                options: {
                  minimize: isProduction
                }
              },
              'sass-loader',
              'postcss-loader'
            ]
          })
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
        format: `${chalk.blue.bold('   build')} [:bar] ${chalk.green.bold(':percent')} (:elapsed seconds) `,
        clear: false
      }),
      new DefinePlugin({
        '__DEVELOPMENT__': JSON.stringify(isDevelopment),
        '__PRODUCTION__': JSON.stringify(isProduction),
        '__MOCK__': JSON.stringify(enableMock)
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
      // new BundleAnalyzerPlugin({
      //   generateStatsFile: true
      // })
    ]
  };

  if (isDevelopment) {
    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'index.html',
      hash: true,
      chunks: ['app']
    }));

    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login.html',
      hash: true,
      chunks: ['login']
    }));

    conf.devServer = {
      port: 3000,
      host: 'localhost',
      historyApiFallback: true,
      watchOptions: {
        aggregateTimeout: 300,
        poll: 1000
      }
    };
  }

  if (isProduction) {
    const commonVendorKeys = ['angular', 'angular-material', 'jquery', 'angular-ui-router', 'angular-translate', '@angular/material', '@angular/core'];
    const pkg = require(webpackHelper.root('package.json'));

    const appChunks = ['commonVendor', 'app'];
    const loginChunks = ['commonVendor', 'login'];

    conf.entry.commonVendor = commonVendorKeys;

    conf.plugins.push(new CleanWebpackPlugin([BUILD_DIR], {
      root: webpackHelper.root(),
      verbose: true
    }));

    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'index.html',
      favicon: webpackHelper.root('assets/favicon/favicon.ico'),
      hash: true,
      chunks: appChunks,
      chunksSortMode: webpackHelper.sortChunks(appChunks)
    }));

    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login.html',
      favicon: webpackHelper.root('assets/favicon/favicon.ico'),
      hash: true,
      chunks: loginChunks,
      chunksSortMode: webpackHelper.sortChunks(loginChunks)
    }));

    conf.plugins.push(new CommonsChunkPlugin({
      names: ['commonVendor'],
      minChunks: Infinity
    }));

    conf.plugins.push(new UglifyJsPlugin({
      sourceMap: false,
      mangle: false
    }));

    conf.plugins.push(new WebpackBuildVersion('build.json'));

    conf.plugins.push(new CopyWebpackPlugin([{
      from: webpackHelper.root('assets/i18n'),
      to: webpackHelper.root('dist/assets/i18n')
    }]));
  }

  return conf;
};