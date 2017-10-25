import * as angular from 'angular';
import 'angular-ui-router';

import 'angular-material';
import 'angular-material/angular-material.css';

import 'fonts/icomoon.css';

import 'zone.js';
import 'reflect-metadata';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import AppConfig from '../../../../appConfig';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {runConfig} from './run';

import {AuthServiceFactory} from './services/auth.service';
import {UserServiceFactory} from './services/user.service';
import {JwtServiceFactory} from './services/jwt.service';

import {LayoutContentComponent, LayoutFooterComponent} from './layout';

import {LoginComponent} from './components/login/login.component';
import {PasswordChangeComponent} from './components/password-change/password-change.component';
import {PasswordPreResetComponent} from './components/password-pre-reset/password-pre-reset.component';
import {PasswordResetComponent} from './components/password-reset/password-reset.component';

export const LoginModule = 'login';

angular
  .module(LoginModule, [
    'ui.router',
    'ngMaterial'
  ])
  .config(routesConfig)
  .config(themeConfig)
  .run(runConfig)
  .value('AppConfig', AppConfig)
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory)
  .component('layoutContent', LayoutContentComponent)
  .component('layoutFooter', LayoutFooterComponent)
  .component('passwordChangeComponent', PasswordChangeComponent)
  .component('passwordPreResetComponent', PasswordPreResetComponent)
  .component('passwordResetComponent', PasswordResetComponent)
  .component('loginComponent', LoginComponent);

@NgModule({
  imports: [
    BrowserModule,
    UpgradeModule
  ]
})
export class NewLoginModule {
  constructor() { }
  ngDoBootstrap() {
  }
}

platformBrowserDynamic().bootstrapModule(NewLoginModule).then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, [LoginModule]);
});