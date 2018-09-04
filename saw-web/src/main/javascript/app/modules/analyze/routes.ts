import {AnalyzeViewComponent} from './view';
import {ExecutedViewComponent} from './executed-view';

export const routes = [{
  name: 'analyze',
  url: '/analyze',
  redirectTo: 'analyze.view'
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
    loadLastExecution: false,
    executionId: null
  }
}];
