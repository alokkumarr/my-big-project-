import angular from 'angular';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../../fonts/style.css';

import {themeConfig} from '../theme';
import {loginComponent} from './login.component';

import '../index.scss';

export const loginModule = 'login';

angular
  .module(loginModule, [
    'ngMaterial'
  ])
  .config(themeConfig)
  .component('loginComponent', loginComponent);
