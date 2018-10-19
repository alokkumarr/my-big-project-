import { Routes } from '@angular/router';
import { WorkbenchPageComponent } from './components/workbench-page/workbench-page.component';
import { DataobjectsComponent } from './components/data-objects-view/data-objects-page.component';
import { CreateDatasetsComponent } from './components/create-datasets/create-datasets.component';
import { SqlExecutorComponent } from './components/sql-executor/sql-executor.component';
import { DatasetDetailViewComponent } from './components/dataset-detailedView/dataset-detail-view.component';
import { CreateSemanticComponent } from './components/semantic-management/create/create-semantic.component';
import { ValidateSemanticComponent } from './components/semantic-management/validate/validate-semantic.component';
import { UpdateSemanticComponent } from './components/semantic-management/update/update-semantic.component';
import { DatasourceComponent } from './components/datasource-management/datasource-page.component';

import { IsUserLoggedInGuard } from '../../common/guards';

export const routes: Routes = [
  {
    path: 'workbench',
    canActivate: [IsUserLoggedInGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: WorkbenchPageComponent,
    runGuardsAndResolvers: 'paramsOrQueryParamsChange',
    children: [
      {
        path: 'dataobjects',
        component: DataobjectsComponent
      },
      {
        path: 'dataset/add',
        component: CreateDatasetsComponent
      },
      {
        path: 'create/sql',
        component: SqlExecutorComponent
      },
      {
        path: 'dataset/details',
        component: DatasetDetailViewComponent
      },
      {
        path: 'semantic/create',
        component: CreateSemanticComponent
      },
      {
        path: 'semantic/validate',
        component: ValidateSemanticComponent
      },
      {
        path: 'semantic/update',
        component: UpdateSemanticComponent
      },
      {
        path: 'datasource/create',
        component: DatasourceComponent
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dataobjects'
      }
    ]
  }
];
