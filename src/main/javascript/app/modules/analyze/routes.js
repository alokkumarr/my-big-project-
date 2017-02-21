export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'analyze',
      url: '/analyze',
      component: 'analyzePage'
    }, {
      name: 'analyze.view',
      url: '/:id',
      component: 'analyzeView'
    }, {
      name: 'analyze.detail',
      url: '/analysis/:id',
      component: 'analyzeDetailView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
