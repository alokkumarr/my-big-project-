import {AnalyzeViewComponent} from './view';
import {ExecutedViewComponent} from './executed-view';

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
      component: ExecutedViewComponent,
      params: {
        analysis: null,
        awaitingExecution: false,
        loadLastExecution: false
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
