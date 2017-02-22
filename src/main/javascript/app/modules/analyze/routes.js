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
      name: 'analyze.executedAnalises',
      url: '/analysis/:analysisId/executed',
      component: 'analyzeExecutedList'
    }, {
      name: 'analyze.executedAnalisis',
      url: '/analysis/executed/:executedInstanceId',
      component: 'analyzeExecutedDetail'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
