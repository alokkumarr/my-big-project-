import 'fonts/icomoon.css';

import 'zone.js';
import 'reflect-metadata';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
// import {
//   downgradeInjectable,
//   downgradeComponent
// } from '@angular/upgrade/static';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UIRouterModule } from '@uirouter/angular';
import { routes } from './routes';
import { MaterialModule } from '../app/material.module';

import { JwtService } from './services/jwt.service';
import { UserService } from './services/user.service';

import {
  LayoutContentComponent,
  LayoutFooterComponent
} from './layout';

import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';

// export const LoginModule = 'login';

// angular
//   .module(LoginModule, ['ui.router', 'ui.router.upgrade', 'ngMaterial'])
//   .config(routesConfig)
//   .config(themeConfig)
//   .run(runConfig)
//   .value('AppConfig', AppConfig)
//   .service('UserService', UserService)
//   .component('layoutContent', LayoutContentComponent)
//   .directive('layoutFooter', downgradeComponent({
//     component: LayoutFooterComponent
//   }) as angular.IDirectiveFactory)
//   .directive('loginComponent', downgradeComponent({
//     component: LoginComponent
//   }) as angular.IDirectiveFactory)
//   .directive('passwordChangeComponent', downgradeComponent({
//     component: PasswordChangeComponent
//   }) as angular.IDirectiveFactory)
//   .directive('passwordPreResetComponent', downgradeComponent({
//     component: PasswordPreResetComponent
//   }) as angular.IDirectiveFactory)
//   .directive('passwordResetComponent', downgradeComponent({
//     component: PasswordResetComponent
//   }) as angular.IDirectiveFactory);

const COMPONENTS = [
  LayoutContentComponent,
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent,
  LayoutFooterComponent
];
@NgModule({
  imports: [
    BrowserModule,
    UIRouterModule.forRoot({states: routes, useHash: true}),
    UpgradeModule,
    HttpClientModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  providers: [JwtService, UserService]
})
export class NewLoginModule {}

platformBrowserDynamic().bootstrapModule(NewLoginModule);
