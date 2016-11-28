/** @ngInject */
export function routesConfig($stateProvider, $urlRouterProvider, $locationProvider) {
  $locationProvider.html5Mode(true).hashPrefix('!');
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'home',
      url: '/',
      component: 'home'
    }, {
      name: 'observe',
      url: '/observe',
      component: 'observePage'
    }, {
      name: 'analyze',
      url: '/analyze',
      component: 'analyzePage'
    }, {
      name: 'alerts',
      url: '/alerts',
      component: 'alertsPage'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
