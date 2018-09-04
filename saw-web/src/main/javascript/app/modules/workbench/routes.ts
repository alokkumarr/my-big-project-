import {WorkbenchPageComponent} from './components/workbench-page/workbench-page.component';
import {DatasetsComponent} from './components/datasets-view/datasets-page.component';
import {CreateDatasetsComponent} from './components/create-datasets/create-datasets.component';
import {SqlExecutorComponent} from './components/sql-executor/sql-executor.component';
import {DatasetDetailViewComponent} from './components/dataset-detailedView/dataset-detail-view.component';

export const routes = [
  {
    name: 'workbench',
    url: '/workbench',
    component: WorkbenchPageComponent
  }, {
    name: 'workbench.datasets',
    url: '/datasets',
    component: DatasetsComponent
  }, {
    name: 'workbench.add',
    url: '/add',
    component: CreateDatasetsComponent
  }, {
    name: 'workbench.sql',
    url: '/create/sql',
    component: SqlExecutorComponent
  }, {
    name: 'workbench.datasetDetails',
    url: '/dataset/details',
    component: DatasetDetailViewComponent
  }
];
