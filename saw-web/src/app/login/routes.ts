import { Routes } from '@angular/router';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';
import { IsUserNotLoggedInGuard } from './guards';
import { LoginPageComponent } from './page';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginPageComponent,
    children: [
      {
        path: '',
        component: LoginComponent,
        canActivate: [IsUserNotLoggedInGuard],
        pathMatch: 'full'
      },
      {
        // name: 'changePassword',
        path: 'changePwd',
        component: PasswordChangeComponent
      },
      {
        // name: 'preResetPassword',
        path: 'preResetPwd',
        canActivate: [IsUserNotLoggedInGuard],
        component: PasswordPreResetComponent
      },
      {
        // name: 'resetPassword',
        path: 'resetPassword',
        canActivate: [IsUserNotLoggedInGuard],
        component: PasswordResetComponent
      }
    ]
  }
];
