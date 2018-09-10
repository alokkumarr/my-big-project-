import { Routes }  from '@angular/router';
import {AnalyzeViewComponent} from './view';
import {ExecutedViewComponent} from './executed-view';

export const routes: Routes = [{
  // name: 'analyze',
  path: 'analyze',
  redirectTo: 'analyze/4'
}, {
  // name: 'analyze.view',
  path: 'analyze/:id',
  component: AnalyzeViewComponent
}, {
  // name: 'analyze.executedDetail',
  path: 'analyze/analysis/:analysisId/executed?executionId&awaitingExecution&loadLastExecution',
  component: ExecutedViewComponent
  // params: {
  //   analysis: null,
  //   awaitingExecution: false,
  //   loadLastExecution: false,
  //   executionId: null
  // }
}];
