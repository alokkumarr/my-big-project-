import { Routes, Route } from '@angular/router';
import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';

export const route: Route = {
  // name: 'observe',
  path: 'observe',
  component: ObservePageComponent,
  children: [
    {
      // name: 'observe.dashboard',
      path: ':subCategory',
      component: ObserveViewComponent,
      runGuardsAndResolvers: 'always'
    }
  ],
  runGuardsAndResolvers: 'always'
};

export const routes: Routes = [route];
