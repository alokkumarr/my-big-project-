import { Routes } from '@angular/router';
import { IsUserLoggedInGuard } from './common/guards';
import { MainPageComponent } from './layout';

export const routes: Routes = [
  {
    // name: 'root',
    path: '',
    canActivateChild: [IsUserLoggedInGuard],
    redirectTo: '/analyze',
    pathMatch: 'full'
  }
];
