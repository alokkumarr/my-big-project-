import { Routes } from '@angular/router';
import { MainPageComponent } from './layout/components/main-page';
import { DefaultHomePageGuard } from './common/guards';


export const routes: Routes = [{
  // name: 'root',
  path: '',
  canActivate: [DefaultHomePageGuard],
  component: MainPageComponent,
  pathMatch: 'full'
}];
