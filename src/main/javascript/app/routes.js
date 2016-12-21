export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'index',
      url: '/'
    }, {
      name: 'observe',
      url: '/observe',
      component: 'observePage'
    }, {
      name: 'analyze',
      url: '/analyze',
      component: 'analyzePage'
    }, {
      name: 'analyze.view',
      url: '/:id',
      component: 'analyzeView'
    }, {
      name: 'alerts',
      url: '/alerts'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
