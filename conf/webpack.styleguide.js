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
const ProgressBarPlugin = require('progress-bar-webpack-plugin');
const CommonsChunkPlugin = require('webpack/lib/optimize/CommonsChunkPlugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

/**
 * Webpack configuration
 */
module.exports = () => {
  const MODULE_DIR = 'node_modules';
  const BUILD_DIR = 'build/styleguide/';

  const pkg = require(webpackHelper.root('package.json'));

  const vendorKeys = Object.keys(pkg.dependencies).map(key => {
    // devextreme has no index.js or a main set in package.json, so we have to manually select the main file
    if (key === 'devextreme') {
      return path.join(key, 'ui', 'data_grid');
    }

    return key;
  });

  const styleGuideChunks = ['vendor', 'styleguide'];

  const config = {
    context: webpackHelper.root('src/main/javascript'),
    entry: {
      styleguide: './styleguide/index',
      vendor: vendorKeys
    },
    output: {
      path: webpackHelper.root(BUILD_DIR),
      filename: 'js/[name].bundle.js'
    },

    devtool: false,

    resolve: {
      modules: [MODULE_DIR, webpackHelper.root('src/main/javascript')],
      alias: {
        fonts: webpackHelper.root('assets/fonts'),
        img: webpackHelper.root('assets/img'),
        api: webpackHelper.root('src/main/mock/api'),
      }
    },

    resolveLoader: {
      modules: [MODULE_DIR]
    },

    module: {
      rules: [
        // preloaders
        {
          enforce: 'pre',
          test: /\.js$/,
          exclude: /node_modules/,
          loader: 'tslint-loader',
          options: {
            fix: false,
            tsConfigFile: webpackHelper.root('conf/tsconfig.json'),
            configuration: require(webpackHelper.root('conf/eslint-dev-rules.js'))
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
          test: /\.js$/,
          exclude: /node_modules/,
          loaders: ['ng-annotate-loader', {
            loader: 'ts-loader',
            options: {
              configFile: webpackHelper.root('conf/tsconfig.json'),
              entryFileIsJs: true
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
            use: 'css-loader?minimize!sass-loader!postcss-loader'
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
      new CleanWebpackPlugin([BUILD_DIR], {
        root: webpackHelper.root(),
        verbose: true
      }),
      new DefinePlugin({
        '__DEVELOPMENT__': false,
        '__MOCK__': false
      }),
      new LoaderOptionsPlugin({
        options: {
          postcss: [autoprefixer]
        }
      }),
      new StyleLintPlugin({
        configFile: webpackHelper.root('.stylelintrc')
      }),
      new ExtractTextPlugin('css/[name].css'),
      new HtmlWebpackPlugin({
        template: 'styleguide/index.html',
        filename: 'index.html',
        favicon: webpackHelper.root('assets/favicon/favicon.ico'),
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
      }
    }
  };

  return config;
};
