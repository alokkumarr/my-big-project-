const webpackHelper = require('./webpack.helper');
const DefinePlugin = require('webpack/lib/DefinePlugin');

const MODULE_DIR = 'node_modules';

module.exports = function (env) {
  const isDevelopment = env === 'development';
  const isProduction = env === 'production';
  const enableMock = true;

  const conf = {
    resolve: {
      extensions: [".ts", ".js"],
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
          test: /\.[jt]s$/,
          exclude: /node_modules/,
          loader: 'tslint-loader',
          options: {
            fix: false,
            typeCheck: false, // tslint-loader is way too slow with this enabled. Use pre-push hook for typechecking
            tsConfigFile: webpackHelper.root('conf/tsconfig.json'),
            configFile: webpackHelper.root('conf/tslint-dev.json')
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
