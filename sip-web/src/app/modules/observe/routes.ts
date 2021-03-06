import { Routes, Route } from '@angular/router';
import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';
import { CreateDashboardComponent } from './components/create-dashboard/create-dashboard.component';
import { IsUserLoggedInGuard } from '../../common/guards';
import { FirstDashboardGuard } from './guards';

export const route: Route = {
  // name: 'observe',
  path: '',
  component: ObservePageComponent,
  canActivate: [IsUserLoggedInGuard, FirstDashboardGuard],
  canActivateChild: [IsUserLoggedInGuard],
  children: [
    {
      // name: 'observe.dashboard',
      path: 'designer',
      component: CreateDashboardComponent,
      runGuardsAndResolvers: 'always'
    },
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
