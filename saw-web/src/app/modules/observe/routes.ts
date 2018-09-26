import { Routes, Route } from '@angular/router';
import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';
import { IsUserLoggedInGuard } from '../../common/guards';

export const route: Route = {
  // name: 'observe',
  path: 'observe',
  component: ObservePageComponent,
  canActivate: [IsUserLoggedInGuard],
  canActivateChild: [IsUserLoggedInGuard],
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
