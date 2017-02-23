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
      name: 'analyze.publishedList',
      url: '/analysis/:analysisId/published',
      component: 'analyzePublishedList'
    }, {
      name: 'analyze.publishedDetail',
      url: '/analysis/published/:publishId',
      component: 'analyzePublishedDetail'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
