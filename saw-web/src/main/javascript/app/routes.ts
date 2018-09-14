import { Routes } from '@angular/router';
import { MainPageComponent } from './layout/components/main-page';
import { DefaultHomePageGuard, IsUserLoggedInGuard } from './common/guards';

export const routes: Routes = [
  {
    // name: 'root',
    path: '',
    canActivate: [DefaultHomePageGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: MainPageComponent,
    pathMatch: 'full'
  }
];
