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
      name: 'analyze.publishedDetailLast',
      url: '/analysis/:analysisId/LastPublished',
      component: 'analyzePublishedDetail'
    }, {
      name: 'analyze.publishedDetail',
      url: '/analysis/:analysisId/published/:publishId',
      component: 'analyzePublishedDetail'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
