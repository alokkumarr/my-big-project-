export function i18nConfig($translateProvider) {
  'ngInject';
  $translateProvider.useStaticFilesLoader({
    prefix: 'assets/i18n/common/',
    suffix: '.json'
  });
  $translateProvider.preferredLanguage('en');
}
