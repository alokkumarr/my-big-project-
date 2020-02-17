import { Routes } from '@angular/router';
import { AnalyzeViewComponent } from './view';
import { ExecutedViewComponent } from './executed-view';
import { AnalyzePageComponent } from './page';
import { DesignerPageComponent } from './designer/designer.module';
import { IsUserLoggedInGuard } from '../../common/guards';
import { DefaultAnalyzeCategoryGuard } from './guards';
import { AnalysesResolver } from './view/analyses.resolver';

export const routes: Routes = [
  {
    // name: 'analyze',
    path: '',
    canActivate: [IsUserLoggedInGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: AnalyzePageComponent,
    children: [
      {
        // name: 'analyze.executedDetail',
        path: 'analysis/:analysisId/executed',
        component: ExecutedViewComponent
        // params: {
        //   analysis: null,
        //   awaitingExecution: false,
        //   loadLastExecution: false,
        //   executionId: null
        // }
      },
      {
        path: 'designer',
        component: DesignerPageComponent
      },
      {
        // name: 'analyze.view',
        path: ':id',
        resolve: {
          analyses: AnalysesResolver
        },
        component: AnalyzeViewComponent
      }
      {
        path: '**',
        canActivate: [DefaultAnalyzeCategoryGuard]
      }
    ]
  }
];
