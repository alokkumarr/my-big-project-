import * as angular from 'angular';

import 'angular-material/angular-material.css';

import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js';
import 'reflect-metadata';
import { NgModule, LOCALE_ID, Injector } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { UrlService } from '@uirouter/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import 'hammerjs';

import { routesConfig } from './routes';
import { themeConfig } from './theme';
import { i18nConfig } from './i18n';
import { config } from './config';
import { interceptor } from './http-interceptor';
import { runConfig } from './run';

import { ObserveModule, ObserveUpgradeModule } from './modules/observe';
import { CommonModuleTs } from './common';
import { AnalyzeModule, AnalyzeModuleTs } from './modules/analyze';
import { AlertsModule } from './modules/alerts';
import { AdminModule } from './modules/admin';
import {WorkbenchModule, WorkbenchUpgradeModule} from './modules/workbench';

import { LayoutHeaderComponent, LayoutContentComponent, LayoutFooterComponent } from './layout';

export const AppModule = 'app';

angular
  .module(AppModule, [
    ObserveModule,
    AnalyzeModule,
    AlertsModule,
    AdminModule,
    WorkbenchModule
  ])
  .config(['$urlServiceProvider', ($urlService: UrlService) => $urlService.deferIntercept()])
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
    UIRouterUpgradeModule,
    CommonModuleTs,
    AnalyzeModuleTs,
    ObserveUpgradeModule,
    FlexLayoutModule,
    WorkbenchUpgradeModule
  ],
  exports: [FlexLayoutModule],
  providers: [
    {provide: LOCALE_ID, useValue: 'en'}
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
  const injector: Injector = platformRef.injector;
  upgrade.bootstrap(document.documentElement, [AppModule]);

  // Instruct UIRouter to listen to URL changes
  const url: UrlService = injector.get(UrlService);
  url.listen();
  url.sync();

  /* Workaround to fix performance - Turns off propagation of changes from
     angular to angularjs. Remove this once upgradation of components start.
     */
  setTimeout(() => {
    upgrade.ngZone.onMicrotaskEmpty.observers.splice(1, 1);
  }, 100);
});
