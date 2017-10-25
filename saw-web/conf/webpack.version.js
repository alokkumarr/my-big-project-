const webpackHelper = require('./webpack.helper');

const async = require('async');
const git = require('git-rev');
const moment = require('moment');
const mapValues = require('lodash/mapValues');

const pkg = require(webpackHelper.root('package.json'));

class WebpackBuildVersion {
  constructor(fileName) {
    this.fileName = fileName || 'build.json';
  }

  apply(compiler) {
    compiler.plugin('emit', (compilation, callback) => {
      async.parallel({
        commit: cb => {
          git.long(commit => {
            cb(null, commit);
          });
        }
      }, (err, results) => {
        if (!err) {
          let build = {
            name: pkg.name,
            version: pkg.version,
            git_commit: results.commit,
            build_time: moment.utc().format('YYYY-MM-DD HH:mm:ss Z')
          };

          build = mapValues(build, value => {
            return value || 'N/A';
          });

          const data = JSON.stringify(build, null, '    ');

          compilation.assets[this.fileName] = {
            source: () => {
              return data
            },
            size: () => {
              return data.length
            }
          };
        }

        callback();
      });
    });
  }
}

module.exports = WebpackBuildVersion;