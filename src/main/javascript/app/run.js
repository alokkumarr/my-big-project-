export function runConfig($rootScope, $state, $location, $window, JwtService) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }

    return 'Synchronoss';
  };

  $rootScope.$on('$locationChangeSuccess', event => {
    const restrictedPage = ['/', '/login', '/observe', '/analyse', '/alerts'];

    if ((restrictedPage.indexOf($location.path()) !== -1) && JwtService.get() !== null) {
      // todo
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path() !== '/login') {
      event.preventDefault();
      $window.location.assign('./login');
    }
  });
}
