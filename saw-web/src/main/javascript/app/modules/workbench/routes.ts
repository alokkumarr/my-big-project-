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
    component: WorkbenchPageComponent
  }, {
    // name: 'workbench.datasets',
    path: 'workbench/datasets',
    component: DatasetsComponent
  }, {
    // name: 'workbench.add',
    path: 'workbench/add',
    component: CreateDatasetsComponent
  }, {
    // name: 'workbench.sql',
    path: 'workbench/create/sql',
    component: SqlExecutorComponent
  }, {
    // name: 'workbench.datasetDetails',
    path: 'workbench/dataset/details',
    component: DatasetDetailViewComponent
  }
];
