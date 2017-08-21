const webpackHelper = require('./webpack.helper');
const DefinePlugin = require('webpack/lib/DefinePlugin');

const MODULE_DIR = 'node_modules';

module.exports = function (env) {
  const isDevelopment = env === 'development';
  const isProduction = env === 'production';
  const enableMock = true;

  const conf = {
    resolve: {
      modules: [
        MODULE_DIR,
        webpackHelper.root('src/main/javascript')
      ]
    },

    module: {
      rules: [
        // preloaders
        {
          enforce: 'pre',
          test: /\.js$/,
          exclude: /node_modules/,
          loader: 'eslint-loader',
          options: {
            fix: false,
            configFile: webpackHelper.root('conf/eslint-dev-rules.js')
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
          loader: 'ng-annotate-loader!babel-loader'
        },
        {
          test: /\.html$/,
          loader: 'html-loader'
        },
        {
          test: /\.(css|scss)$/,
          loader: 'null-loader'
        },
        {
          test: /\.(css|scss)$/,
          loader: 'raw-loader'
        },
        {
          test: /\.(eot|woff|woff2|ttf)$/,
          loader: 'null-loader'
        },
        {
          test: /\.(png|jpg|svg)$/,
          loader: 'null-loader'
        }
      ]
    },

    plugins: [
      new DefinePlugin({
        '__DEVELOPMENT__': JSON.stringify(isDevelopment),
        '__PRODUCTION__': JSON.stringify(isProduction),
        '__MOCK__': JSON.stringify(enableMock)
      })
    ]
  };

  return conf;
};
