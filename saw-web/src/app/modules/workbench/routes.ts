import { Routes } from '@angular/router';
import { WorkbenchPageComponent } from './components/workbench-page/workbench-page.component';
import { DataobjectsComponent } from './components/data-objects-view/data-objects-page.component';
import { CreateDatasetsComponent } from './components/create-datasets/create-datasets.component';
import { SqlExecutorComponent } from './components/sql-executor/sql-executor.component';
import { DatasetDetailViewComponent } from './components/dataset-detailedView/dataset-detail-view.component';
import { CreateSemanticComponent } from './components/semantic-management/create/create-semantic.component';
import { ValidateSemanticComponent } from './components/semantic-management/validate/validate-semantic.component';
import { UpdateSemanticComponent } from './components/semantic-management/update/update-semantic.component';
import { DefaultWorkbenchPageGuard } from './guards';
import { IsUserLoggedInGuard } from '../../common/guards';

export const routes: Routes = [
  {
    // name: 'workbench',
    path: '',
    canActivate: [IsUserLoggedInGuard, DefaultWorkbenchPageGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: WorkbenchPageComponent,
    children: [
      {
        // name: 'workbench.dataobjects',
        path: 'dataobjects',
        component: DataobjectsComponent
      },
      {
        // name: 'workbench.add',
        path: 'dataset/add',
        component: CreateDatasetsComponent
      },
      {
        // name: 'workbench.sql',
        path: 'create/sql',
        component: SqlExecutorComponent
      },
      {
        // name: 'workbench.datasetDetails',
        path: 'dataset/details',
        component: DatasetDetailViewComponent
      },
      {
        // name: 'workbench.createSemantic',
        path: 'semantic/create',
        component: CreateSemanticComponent
      },
      {
        // name: 'workbench.validateSemantic',
        path: 'semantic/validate',
        component: ValidateSemanticComponent
      },
      {
        // name: 'workbench.updateSemantic',
        path: 'semantic/update',
        component: UpdateSemanticComponent
      }
    ]
  }
];
