const webpackHelper = require('./webpack.helper');

const MODULE_DIR = 'node_modules';

module.exports = {
  resolve: {
    modules: [
      MODULE_DIR,
      webpackHelper.root('src/main/javascript')
    ],
    extensions: ['', '.js']
  },

  module: {
    preLoaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'eslint'
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
      }
    ]
  }
};
