import {WorkbenchPageComponent} from './components/workbench-page/workbench-page.component';
import {DatasetsComponent} from './components/datasets-view/datasets-page.component';
import {CreateDatasetsComponent} from './components/create-datasets/create-datasets.component';
import {SqlExecutorComponent} from './components/sql-executor/sql-executor.component';
import {DatasetDetailViewComponent} from './components/dataset-detailedView/dataset-detail-view.component';
import {CreateSemanticComponent} from './components/semantic-management/create/create-semantic.component';
import {ValidateSemanticComponent} from './components/semantic-management/validate/validate-semantic.component';
import {UpdateSemanticComponent} from './components/semantic-management/update/update-semantic.component';

export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
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
      url: '/dataset/add',
      component: CreateDatasetsComponent
    }, {
      name: 'workbench.sql',
      url: '/create/sql',
      component: SqlExecutorComponent
    }, {
      name: 'workbench.datasetDetails',
      url: '/dataset/details',
      component: DatasetDetailViewComponent
    }, {
      name: 'workbench.createSemantic',
      url: '/semantic/create',
      component: CreateSemanticComponent
    }, {
      name: 'workbench.validateSemantic',
      url: '/semantic/validate',
      component: ValidateSemanticComponent
    }, {
      name: 'workbench.updateSemantic',
      url: '/semantic/update',
      component: UpdateSemanticComponent
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
