import {WorkbenchPageComponent} from './components/workbench-page/workbench-page.component';
import {DatasetsComponent} from './components/datasets-view/datasets-page.component';
import {CreateDatasetsComponent} from './components/create-datasets/create-datasets.component';

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
      url: '/add',
      component: CreateDatasetsComponent
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
