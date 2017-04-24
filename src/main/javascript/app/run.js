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
    const restrictedPage = ['/', '/login', '/observe', '/analyze', '/alerts'];
    const token = JwtService.get();

    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(token)) {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace('-', '+').replace('_', '/');
      const resp = angular.fromJson($window.atob(base64));
      let hideObserve = true;
      let hideAnalyze = true;
      for (let i = 0; i < resp.ticket.productModules.length; i++) {
        if (resp.ticket.productModules[i].productModCode === 'OBSRV00001') {
          hideObserve = false;
        } else if (resp.ticket.productModules[i].productModCode === 'ANLYS00001') {
          hideAnalyze = false;
        }
      }
      if ((hideObserve && ($location.path().indexOf('/observe') !== -1)) || (hideAnalyze && ($location.path().indexOf('/analyze') !== -1))) {
        event.preventDefault();
        $window.location.assign('./');
      }
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path().indexOf('/login') === -1) {
      event.preventDefault();
      $window.location.assign('./login.html');
    }
  });

  $rootScope.$on('$stateChangeStart', event => {
    const restrictedPage = ['/', '/login', '/observe', '/analyze', '/alerts'];
    const token = JwtService.get();
    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(token)) {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace('-', '+').replace('_', '/');
      const resp = angular.fromJson($window.atob(base64));
      let hideObserve = true;
      let hideAnalyze = true;
      for (let i = 0; i < resp.ticket.productModules.length; i++) {
        if (resp.ticket.productModules[i].productModCode === 'OBSRV00001') {
          hideObserve = false;
        } else if (resp.ticket.productModules[i].productModCode === 'ANLYS00001') {
          hideAnalyze = false;
        }
      }
      if ((hideObserve && ($location.path().indexOf('/observe') !== -1)) || (hideAnalyze && ($location.path().indexOf('/analyze') !== -1))) {
        event.preventDefault();
        $window.location.assign('./');
      }
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path().indexOf('/login') === -1) {
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
