const gulp = require('gulp');
const browserSync = require('browser-sync');
const spa = require('browser-sync-spa');

const browserSyncConf = require('../conf/browsersync.conf');
const browserSyncDistConf = require('../conf/browsersync-dist.conf');
const browserSyncStyleguideConf = require('../conf/browsersync-styleguide.conf');

browserSync.use(spa());

gulp.task('browsersync', browserSyncServe);
gulp.task('browsersync:dist', browserSyncDist);
gulp.task('browsersync:styleguide', browserSyncStyleguide);

function browserSyncServe(done) {
  browserSync.init(browserSyncConf());
  done();
}

function browserSyncDist(done) {
  browserSync.init(browserSyncDistConf());
  done();
}

function browserSyncStyleguide(done) {
  browserSync.init(browserSyncStyleguideConf());
  done();
}
