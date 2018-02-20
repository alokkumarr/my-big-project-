export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  // $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise('/analyze');

  const states = [{
    name: 'root',
    url: '/'
  }, {
    // Dummy route to serve as auth endpoint
    name: 'authenticate',
    url: '/authenticate'
  }];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
