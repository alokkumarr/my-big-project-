import { Routes }  from '@angular/router';
import {ObservePageComponent} from './components/observe-page/observe-page.component';
import {ObserveViewComponent} from './components/observe-view/observe-view.component';

export const routes: Routes = [
  {
    // name: 'observe',
    path: 'observe',
    component: ObservePageComponent
  },
  {
    // name: 'observe.dashboard',
    path: 'observe/:subCategory?dashboard',
    component: ObserveViewComponent
  }
];
