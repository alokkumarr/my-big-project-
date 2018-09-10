import { Routes }  from '@angular/router';
import {AnalyzeViewComponent} from './view';
import {ExecutedViewComponent} from './executed-view';
import { AnalyzePageComponent } from './page';

export const routes: Routes = [{
  // name: 'analyze',
  path: 'analyze',
  component: AnalyzePageComponent,
  children: [
    {
      // name: 'analyze.view',
      path: ':id',
      component: AnalyzeViewComponent
    }, {
      // name: 'analyze.executedDetail',
      path: 'analysis/:analysisId/executed?executionId&awaitingExecution&loadLastExecution',
      component: ExecutedViewComponent
      // params: {
      //   analysis: null,
      //   awaitingExecution: false,
      //   loadLastExecution: false,
      //   executionId: null
      // }
    }
  ]
}];
