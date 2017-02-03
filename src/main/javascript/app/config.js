export function config($compileProvider) {
  'ngInect';
  // this is needed for md-timepicker to work with newer angular versions
  // https://github.com/angular/material/issues/10168
  $compileProvider.preAssignBindingsEnabled(true);
}
