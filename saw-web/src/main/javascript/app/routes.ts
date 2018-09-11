import { Routes }  from '@angular/router';
import { IsUserLoggedInGuard } from './common/guards'
import { MainPageComponent } from './layout';
import { route as observeRoute } from './modules/observe/routes';

export const routes: Routes = [{
  // name: 'root',
  path: '',
  canActivateChild: [IsUserLoggedInGuard],
  component: MainPageComponent,
  pathMatch: 'full'
}];
