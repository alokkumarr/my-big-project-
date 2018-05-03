import {AnalyzeViewComponent} from './view';

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
      component: AnalyzeViewComponent
    }, {
      name: 'analyze.executedDetail',
      url: '/analysis/:analysisId/executed?executionId',
      component: 'analyzeExecutedDetail',
      params: {
        analysis: null
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
