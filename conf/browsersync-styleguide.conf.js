const conf = require('./gulp.conf');

module.exports = function () {
  return {
    server: {
      baseDir: [
        conf.paths.styleguideDist,
        conf.paths.app
      ]
    },
    open: false
  };
};
