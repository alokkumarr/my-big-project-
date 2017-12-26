import {ObservePageComponent} from './components/observe-page/observe-page.component';
import {ObserveViewComponent} from './components/observe-view/observe-view.component';

export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'observe',
      url: '/observe',
      component: ObservePageComponent
    },
    {
      name: 'observe.dashboard',
      url: '/:dashboardId',
      component: ObserveViewComponent
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
