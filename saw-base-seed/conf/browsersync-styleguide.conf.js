const conf = require('./gulp.conf');

module.exports = function () {
  return {
    server: {
      baseDir: [
        conf.paths.styleguideDist,
        conf.paths.src
      ]
    },
    open: false
  };
};
