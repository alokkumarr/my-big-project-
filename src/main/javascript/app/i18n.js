export function i18nConfig($translateProvider, $translatePartialLoaderProvider) {
  'ngInject';

  $translateProvider.useLoader('$translatePartialLoader', {
    urlTemplate: 'assets/i18n/{part}/{lang}.json'
  });

  $translatePartialLoaderProvider.addPart('common');

  // this is used for for more complex dynamic variables like making word singular or plural
  // making ordinals dynamic, like: 1st, 2nd, 3rd, 4th
  // you have to specify, the 'messageformat' interpolator when using it, for example:
  // <label translate="NTH_WEEK" translate-values="{CAT: 2}" translate-interpolation="messageformat"></label>
  $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

  $translateProvider.preferredLanguage('en');
}
