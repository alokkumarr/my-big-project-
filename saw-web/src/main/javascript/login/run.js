export function runConfig($q, $log, $rootScope, $state, $location, $window, JwtService, UserService, $transitions) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }

    return 'Synchronoss';
  };

  $transitions.onStart({}, () => {
    const ssoPromise = $q.defer();
    const loginToken = $location.search().jwt;
    if (loginToken) {
      UserService.exchangeLoginToken(loginToken).then(data => {
        if (data) {
          // SSO token has been exchanged successfully. Redirect to main app.
          $window.location.assign('./');
          ssoPromise.resolve(false);
        } else {
          ssoPromise.resolve(true);
        }
      }, error => {
        $log.error(error);
        ssoPromise.resolve(true);
      });
    } else {
      ssoPromise.resolve(true);
    }

    return ssoPromise.promise;
  });

  $rootScope.$on('$locationChangeSuccess', event => {
    const restrictedPage = ['/', '/changePwd'];

    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(JwtService.get())) {
      // todo
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path() !== '/login') {
      event.preventDefault();

      $state.go('login');
    }
  });

  $rootScope.$on('$stateChangeSuccess', event => {
    const restrictedPage = ['/', '/changePwd'];

    if ((restrictedPage.indexOf($location.path()) !== -1) && angular.isDefined(JwtService.get())) {
      // todo
    } else if ((restrictedPage.indexOf($location.path()) !== -1) && $location.path() !== '/login') {
      event.preventDefault();

      $state.go('login');
    }
  });
}
