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
const StyleLintPlugin = require('stylelint-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

module.exports = function (env) {
  const isDevelopment = env === 'development';
  const isProduction = env === 'production';
  const enableMock = true;

  const MODULE_DIR = 'node_modules';

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
      path: webpackHelper.root('dist'),
      filename: 'js/[name].bundle.js',
      sourceMapFilename: isDevelopment ? '[file].map' : ''
    },

    devtool: isDevelopment ? 'source-map' : false,

    resolve: {
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
      rules: [
        {
          enforce: 'pre',
          test: /\.js$/,
          exclude: /node_modules/,
          loader: 'eslint-loader',
          options: {
            fix: false,
            configFile: isDevelopment ?
              webpackHelper.root('conf/eslint-dev-rules.js') :
              webpackHelper.root('conf/eslint-prod-rules.js')
          }
        },
        {
          enforce: 'pre',
          test: /\.html$/,
          loader: 'htmlhint-loader'
        },
        {
          test: /.json$/,
          loader: 'json-loader'
        },
        {
          test: /\.js$/,
          exclude: /node_modules/,
          loader: 'ng-annotate-loader!babel-loader'
        },
        {
          test: /.html$/,
          loader: 'html-loader'
        },
        {
          test: /\.(css|scss)$/,
          loader: ExtractTextPlugin.extract({
            fallback: 'style-loader',
            loader: `css-loader${isProduction ? '?minimize' : ''}!sass-loader!postcss-loader`
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
        format: chalk.blue.bold('   build') + ' [:bar] ' + chalk.green.bold(':percent') + ' (:elapsed seconds) ',
        clear: false
      }),
      new DefinePlugin({
        '__DEVELOPMENT__': isDevelopment,
        '__PRODUCTION__': isProduction,
        '__MOCK__': enableMock
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

    conf.entry.vendor = vendorKeys;

    conf.plugins.push(new CleanWebpackPlugin(['dist/'], {
      root: webpackHelper.root(),
      verbose: true
    }));

    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'app/index.html',
      filename: 'index.html',
      favicon: webpackHelper.root('assets/img/favicon.png'),
      hash: true,
      chunks: appChunks,
      chunksSortMode: webpackHelper.sortChunks(appChunks)
    }));

    conf.plugins.push(new HtmlWebpackPlugin({
      template: 'login/index.html',
      filename: 'login.html',
      favicon: webpackHelper.root('assets/img/favicon.png'),
      hash: true,
      chunks: loginChunks,
      chunksSortMode: webpackHelper.sortChunks(loginChunks)
    }));

    conf.plugins.push(new CommonsChunkPlugin({
      names: ['vendor'],
      minChunks: Infinity
    }));
  }

  return conf;
};
