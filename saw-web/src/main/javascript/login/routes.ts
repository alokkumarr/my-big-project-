import { RouterModule, Routes }  from '@angular/router';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';

export const routes = [{
  name: 'login',
  url: '/',
  component: LoginComponent,
  data: {
    title: 'Login'
  }
}, {
  name: 'changePassword',
  url: '/changePwd',
  component: PasswordChangeComponent,
  data: {
    title: 'Change Password'
  }
}, {
  name: 'preResetPassword',
  url: '/preResetPwd',
  component: PasswordPreResetComponent,
  data: {
    title: 'Reset Password'
  }
}, {
  name: 'resetPassword',
  url: '/resetPassword',
  component: PasswordResetComponent,
  data: {
    title: 'Reset Password'
  }
}];
