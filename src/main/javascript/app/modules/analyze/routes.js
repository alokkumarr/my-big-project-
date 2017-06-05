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
      name: 'analyze.publishedDetail',
      url: '/analysis/:analysisId/published',
      component: 'analyzePublishedDetail',
      params: {
        analysis: null
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
