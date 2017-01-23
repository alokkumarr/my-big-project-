const webpackMerge = require('webpack-merge');
const webpackHelper = require('./webpack.helper');
const commonConfig = require('./webpack.common.js');

/**
 * Webpack configuration
 */
module.exports = webpackMerge(commonConfig, {
  entry: {
    app: './app/index',
    login: './login/index'
  },

  output: {
    path: webpackHelper.root('build/dist')
  },
});
