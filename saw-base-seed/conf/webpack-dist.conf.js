const indexOf = require('lodash/indexOf');
const gte = require('lodash/gte');
const webpack = require('webpack');
const conf = require('./gulp.conf');
const path = require('path');

const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const pkg = require('../package.json');
const autoprefixer = require('autoprefixer');

const vendorKeys = Object.keys(pkg.dependencies).map(key => {
  // devextreme has no index.js or a main set in package.json, so we have to manually select the main file
  if (key === 'devextreme') {
    return path.join(key, 'client_exporter');
  }
  return key;
});
// a common chunk plugin used in app and in login as well
const commonVendorKeys = ['angular', 'angular-material'];
// vendor libraries used only in app, without the libs that are in the common chunk plugin
const appOnlyVendorKeys = vendorKeys.filter(key => gte(0, indexOf(commonVendorKeys, key)));


module.exports = {
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
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: [
          'ng-annotate',
          'babel'
        ]
      },
      {
        test: /.json$/,
        loaders: [
          'json'
        ]
      },
      {
        test: /\.(css|scss)$/,
        loaders: ExtractTextPlugin.extract({
          fallbackLoader: 'style',
          loader: 'css?minimize!sass!postcss'
        })
      },
      {
        test: /.html$/,
        loaders: [
          'html'
        ]
      },
      // {
      //   test: /\.(jpe?g|png|gif|svg)$/i,
      //   loaders: [
      //     'file?hash=sha512&digest=hex&name=[hash].[ext]',
      //     'image-webpack?bypassOnDebug&optimizationLevel=7&interlaced=false'
      //   ]
      // },
      {
        test: /\.(eot|woff|woff2|ttf|svg|png|jpg)$/,
        loader: 'url-loader?limit=30000&name=[name]-[hash].[ext]'
      }
    ]
  },
  plugins: [
    new webpack.optimize.OccurrenceOrderPlugin(),
    new webpack.NoErrorsPlugin(),
    new HtmlWebpackPlugin({
      template: conf.path.app('index.html'),
      chunks: ['commonVendor' ,'vendor', 'app']
    }),
    new HtmlWebpackPlugin({
      filename: 'login.html',
      chunks: ['commonVendor', 'login'],
      template: conf.path.login('index.html')
    }),
    // uglifyjs introduces some wierd bugs
    // new webpack.optimize.UglifyJsPlugin({
    //   compress: {unused: true, dead_code: true, warnings: false} // eslint-disable-line camelcase
    // }),
    new ExtractTextPlugin('index-[contenthash].css'),
    new webpack.optimize.CommonsChunkPlugin({
      name: 'commonVendor',
      filename: 'commonVendor.js',
      minChunks: Infinity
    })
  ],
  postcss: () => [autoprefixer],
  output: {
    path: path.join(process.cwd(), conf.paths.dist),
    filename: '[name]-[hash].js'
  },
  entry: {
    app: `./${conf.path.app('index')}`,
    vendor: appOnlyVendorKeys,
    commonVendor: commonVendorKeys,
    login: `./${conf.path.login('index')}`
  }
};
