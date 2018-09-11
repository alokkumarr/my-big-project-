import * as angular from 'angular';
import '@uirouter/angular-hybrid';

import 'angular-material';
import 'angular-material/angular-material.css';

import 'fonts/icomoon.css';

import 'zone.js';
import 'reflect-metadata';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import {
  downgradeInjectable,
  downgradeComponent
} from '@angular/upgrade/static';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../app/material.module';

import AppConfig from '../../../../appConfig';

import { routesConfig } from './routes';
import { themeConfig } from './theme';
import { runConfig } from './run';

import { AddTokenInterceptor } from '../app/common/interceptor';
import { ConfigService } from '../app/common/services/configuration.service';
import { JwtService } from './services/jwt.service';
import { UserService } from './services/user.service';
import { AuthServiceFactory } from './services/auth.service';
import {
  jwtServiceProvider,
  userServiceProvider
} from './services/ajs-login-providers';

import { LayoutContentComponent, LayoutFooterComponent } from './layout';

import { LoginComponent } from './components/login/login.component';
import { PasswordChangeComponent } from './components/password-change/password-change.component';
import { PasswordPreResetComponent } from './components/password-pre-reset/password-pre-reset.component';
import { PasswordResetComponent } from './components/password-reset/password-reset.component';

export const LoginModule = 'login';

angular
  .module(LoginModule, ['ui.router', 'ui.router.upgrade', 'ngMaterial'])
  .config(routesConfig)
  .config(themeConfig)
  .run(runConfig)
  .value('AppConfig', AppConfig)
  .factory('AuthService', AuthServiceFactory)
  .service('JwtService', JwtService)
  .service('UserService', UserService)
  .component('layoutContent', LayoutContentComponent)
  .directive('layoutFooter', downgradeComponent({
    component: LayoutFooterComponent
  }) as angular.IDirectiveFactory)
  .directive('loginComponent', downgradeComponent({
    component: LoginComponent
  }) as angular.IDirectiveFactory)
  .directive('passwordChangeComponent', downgradeComponent({
    component: PasswordChangeComponent
  }) as angular.IDirectiveFactory)
  .directive('passwordPreResetComponent', downgradeComponent({
    component: PasswordPreResetComponent
  }) as angular.IDirectiveFactory)
  .directive('passwordResetComponent', downgradeComponent({
    component: PasswordResetComponent
  }) as angular.IDirectiveFactory);

@NgModule({
  imports: [
    BrowserModule,
    UpgradeModule,
    HttpClientModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: [
    LoginComponent,
    PasswordChangeComponent,
    PasswordPreResetComponent,
    PasswordResetComponent,
    LayoutFooterComponent
  ],
  entryComponents: [
    LoginComponent,
    PasswordChangeComponent,
    PasswordPreResetComponent,
    PasswordResetComponent,
    LayoutFooterComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AddTokenInterceptor, multi: true },
    jwtServiceProvider,
    userServiceProvider,
    ConfigService
  ]
})
export class NewLoginModule {
  constructor() {}
  ngDoBootstrap() {}
}

platformBrowserDynamic()
  .bootstrapModule(NewLoginModule)
  .then(platformRef => {
    const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
    upgrade.bootstrap(document.documentElement, [LoginModule]);
  });
