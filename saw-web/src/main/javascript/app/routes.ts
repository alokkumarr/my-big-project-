import { Routes } from '@angular/router';
import { IsUserLoggedInGuard, DefaultModuleGuard } from './common/guards';
import { MainPageComponent } from './layout';

export const routes: Routes = [
  {
    // name: 'root',
    path: '',
    canActivate: [IsUserLoggedInGuard, DefaultModuleGuard],
    canActivateChild: [IsUserLoggedInGuard],
    // redirectTo: 'analyze',
    component: MainPageComponent,
    pathMatch: 'full'
  }
];
