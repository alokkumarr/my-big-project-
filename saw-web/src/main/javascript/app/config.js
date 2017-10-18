export function config($compileProvider, IdleProvider, KeepaliveProvider, localStorageServiceProvider) {
  'ngInect';
  // this is needed for md-timepicker to work with newer angular versions
  // https://github.com/angular/material/issues/10168
  $compileProvider.preAssignBindingsEnabled(true);

  // configure Idle settings
  IdleProvider.idle(1200); // in seconds
  IdleProvider.timeout(1200); // in seconds
  KeepaliveProvider.interval(2); // in seconds

  const appName = 'symmetra';
  localStorageServiceProvider.setPrefix(appName);
}
