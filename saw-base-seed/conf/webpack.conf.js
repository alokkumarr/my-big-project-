const webpack = require('webpack');
const conf = require('./gulp.conf');
const path = require('path');

const HtmlWebpackPlugin = require('html-webpack-plugin');
const autoprefixer = require('autoprefixer');

module.exports = {
  entry: {
    index: `./${conf.path.app('index')}`,
    login: `./${conf.path.login('index')}`
  },
  output: {
    path: path.join(process.cwd(), conf.paths.tmp),
    filename: '[name].js'
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
        loaders: ['json']
      },
      {
        test: /\.(css|scss)$/,
        loaders: [
          'style',
          'css',
          'sass',
          'postcss'
        ]
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: [
          'ng-annotate',
          'babel'
        ]
      },
      {
        test: /.html$/,
        loaders: [
          'html'
        ]
      },
      // commented out because the image loader corrupts png images
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
  resolve: {
    modules: [
      'src/',
      'node_modules/'
    ]
  },
  plugins: [
    new webpack.optimize.OccurrenceOrderPlugin(),
    new webpack.NoErrorsPlugin(),
    new HtmlWebpackPlugin({
      template: conf.path.app('index.html'),
      chunks: ['index', 'login']
    }),
    new HtmlWebpackPlugin({
      filename: 'login.html',
      chunks: ['login'],
      template: conf.path.login('index.html')
    }),
    new webpack.DefinePlugin({
      '__DEVELOPMENT__': process.env.NODE_ENV !== 'production'
    })
  ],
  postcss: () => [autoprefixer],
  debug: true,
  devtool: 'eval-source-map',
  eslint: {
    configFile: conf.paths.eslintDevConfig
  }
};
