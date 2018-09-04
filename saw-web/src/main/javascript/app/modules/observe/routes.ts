import {ObservePageComponent} from './components/observe-page/observe-page.component';
import {ObserveViewComponent} from './components/observe-view/observe-view.component';

export const routes = [
  {
    name: 'observe',
    url: '/observe',
    component: ObservePageComponent
  },
  {
    name: 'observe.dashboard',
    url: '/:subCategory?dashboard',
    component: ObserveViewComponent
  }
];
