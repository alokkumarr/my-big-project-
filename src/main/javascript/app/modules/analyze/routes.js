export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'analyze',
      url: '/analyze',
      component: 'analyzePage',
      redirectTo: {
        state: 'analyze.view',
        params: {id: 1}
      }
    }, {
      name: 'analyze.view',
      url: '/:id',
      component: 'analyzeView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
