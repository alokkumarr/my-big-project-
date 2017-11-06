import * as angular from 'angular';

import 'angular-material/angular-material.css';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js';
import 'reflect-metadata';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {i18nConfig} from './i18n';
import {config} from './config';
import {interceptor} from './http-interceptor';
import {runConfig} from './run';

import {ObserveModule, ObserveUpgradeModule} from './modules/observe/index';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';
import {AdminModule} from './modules/admin';

import {LayoutHeaderComponent, LayoutContentComponent, LayoutFooterComponent} from './layout';

export const AppModule = 'app';

angular
  .module(AppModule, [
    ObserveModule,
    AnalyzeModule,
    AlertsModule,
    AdminModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .config(i18nConfig)
  .config(config)
  .config(interceptor)
  .run(runConfig)
  .component('layoutHeader', LayoutHeaderComponent)
  .component('layoutContent', LayoutContentComponent)
  .component('layoutFooter', LayoutFooterComponent);

// angular.bootstrap(document, [AppModule]);

@NgModule({
  imports: [
    BrowserModule,
    UpgradeModule,
    ObserveUpgradeModule
  ]
})
export class NewAppModule {
  constructor() { }
  ngDoBootstrap() {
  }
}

export const platformRefPromise = platformBrowserDynamic().bootstrapModule(NewAppModule);

platformRefPromise.then(platformRef => {
  const upgrade = platformRef.injector.get(UpgradeModule) as UpgradeModule;
  upgrade.bootstrap(document.documentElement, [AppModule]);

  /* Workaround to fix performance - Turns off propagation of changes from
     angular to angularjs. Remove this once upgradation of components start.
     */
  setTimeout(() => {
    upgrade.ngZone.onMicrotaskEmpty.observers.splice(1, 1);
  }, 100);
});
