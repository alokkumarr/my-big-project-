function AuthConfig($stateProvider, $httpProvider) {
  'ngInject';

  // Define the routes
  $stateProvider

  .state('app.login', {
    controller: 'AuthCtrl as $ctrl',
    url: '/login',
    templateUrl: 'auth/auth.html',
    title: 'Sign in'
  })

  .state('app.register', {
    controller: 'AuthCtrl as $ctrl',
    url: '/register',
    templateUrl: 'auth/auth.html',
    title: 'Sign up'
  });

};

export default AuthConfig;