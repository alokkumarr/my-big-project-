import { Routes } from '@angular/router';
import {AnalyzeViewComponent} from './view';
import {ExecutedViewComponent} from './executed-view';
import { AnalyzePageComponent } from './page';
import { IsUserLoggedInGuard } from '../../common/guards';
import { DefaultAnalyzeCategoryGuard } from './guards';

export const routes: Routes = [{
  // name: 'analyze',
  path: 'analyze',
  canActivate: [IsUserLoggedInGuard, DefaultAnalyzeCategoryGuard],
  canActivateChild: [IsUserLoggedInGuard],
  component: AnalyzePageComponent,
  children: [
    {
      // name: 'analyze.view',
      path: ':id',
      component: AnalyzeViewComponent
    }, {
      // name: 'analyze.executedDetail',
      path: 'analysis/:analysisId/executed',
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
