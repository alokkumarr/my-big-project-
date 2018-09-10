import { Routes }  from '@angular/router';
import {WorkbenchPageComponent} from './components/workbench-page/workbench-page.component';
import {DatasetsComponent} from './components/datasets-view/datasets-page.component';
import {CreateDatasetsComponent} from './components/create-datasets/create-datasets.component';
import {SqlExecutorComponent} from './components/sql-executor/sql-executor.component';
import {DatasetDetailViewComponent} from './components/dataset-detailedView/dataset-detail-view.component';

export const routes: Routes = [
  {
    // name: 'workbench',
    path: 'workbench',
    component: WorkbenchPageComponent,
    children: [
      {
        // name: 'workbench.datasets',
        path: 'datasets',
        component: DatasetsComponent
      }, {
        // name: 'workbench.add',
        path: 'add',
        component: CreateDatasetsComponent
      }, {
        // name: 'workbench.sql',
        path: 'create/sql',
        component: SqlExecutorComponent
      }, {
        // name: 'workbench.datasetDetails',
        path: 'dataset/details',
        component: DatasetDetailViewComponent
      }
    ]
  }
];
