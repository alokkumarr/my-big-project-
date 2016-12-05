import '../../themes/_triton.scss';
const themeName = 'triton';

/** @ngInject */
export function themeConfig($mdThemingProvider, $mdAriaProvider) {
  $mdThemingProvider.theme(themeName)
    .primaryPalette('blue', {
      default: '700', // by default use shade from the palette for primary intentions
      'hue-1': '400', // use shade for the <code>md-hue-1</code> class
      'hue-2': '600', // use shade for the <code>md-hue-2</code> class
      'hue-3': 'A100' // use shade for the <code>md-hue-3</code> class
    });

  $mdThemingProvider.setDefaultTheme(themeName);
  $mdAriaProvider.disableWarnings();
}
