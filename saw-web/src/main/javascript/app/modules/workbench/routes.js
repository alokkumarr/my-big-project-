import {WorkbenchPageComponent} from './components/workbench-page/workbench-page.component';
import {DatasetsComponent} from './components/datasets-view/datasets-page.component';

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
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
