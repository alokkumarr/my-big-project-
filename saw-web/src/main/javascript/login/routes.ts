import { Routes }  from '@angular/router';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';
import {
  IsUserNotLoggedInGuard
} from './guards/not-logged-in-guard.service';

export const routes : Routes = [{

  // name: 'changePassword',
  path: 'changePwd',
  component: PasswordChangeComponent,
  data: {
    title: 'Change Password'
  }
}, {
  // name: 'preResetPassword',
  path: 'preResetPwd',
  component: PasswordPreResetComponent,
  data: {
    title: 'Reset Password'
  }
}, {
  // name: 'resetPassword',
  path: 'resetPassword',
  component: PasswordResetComponent,
  data: {
    title: 'Reset Password'
  }
}, {
  // name: 'login',
  path: '',
  component: LoginComponent,
  canActivate: [IsUserNotLoggedInGuard],
  data: {
    title: 'Login'
  }
}];
