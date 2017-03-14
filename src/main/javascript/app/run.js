export function runConfig($rootScope, $state, $location, $window, JwtService, Idle, UserService) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }

    return 'Synchronoss';
  };

  $rootScope.$on('$locationChangeStart', event => {
    const restrictedPage = ['/', '/login', '/observe', '/analyse', '/alerts'];

    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(JwtService.get())) {
      // todo
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path() !== '/login') {
      event.preventDefault();
      $window.location.assign('./login.html');
    }
  });

  $rootScope.$on('$stateChangeStart', event => {
    const restrictedPage = ['/', '/login', '/observe', '/analyse', '/alerts'];

    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(JwtService.get())) {
      // todo
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path() !== '/login') {
      event.preventDefault();
      $window.location.assign('./login.html');
    }
  });
  Idle.watch();
  $rootScope.$on('IdleTimeout', event => {
    event.preventDefault();
    UserService.logout('logout');
  });
}
