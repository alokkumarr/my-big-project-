import * as angular from 'angular';

import 'angular-material/angular-material.css';

import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js/dist/zone';
import 'reflect-metadata';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { UrlService } from '@uirouter/core';
import { NgModule, Injector, StaticProvider } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule, downgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { routesConfig } from './routes';
import { themeConfig } from './theme';
import { i18nConfig } from './i18n';
import { config } from './config';
import { interceptor } from './http-interceptor';
import { runConfig } from './run';

import { ObserveModule, ObserveUpgradeModule } from './modules/observe/index';
import { AnalyzeModule, AnalyzeUpgradeModule } from './modules/analyze';
import { AlertsModule } from './modules/alerts';
import { AdminModule } from './modules/admin';

import { LayoutHeaderComponent, LayoutContentComponent, LayoutFooterComponent } from './layout';

@NgModule({
  imports: [
    BrowserModule,
    UpgradeModule,
    UIRouterUpgradeModule,
    ObserveUpgradeModule,
    AnalyzeUpgradeModule
  ]
})
export class NewAppModule {
  constructor() { }
  ngDoBootstrap() {
  }
}

const ng2BootstrapFn = (extraProviders: StaticProvider[]) => {
  return platformBrowserDynamic(extraProviders).bootstrapModule(NewAppModule).then(platformRef => {
    const injector: Injector = platformRef.injector;

    // Instruct UIRouter to listen to URL changes
    const url: UrlService = injector.get(UrlService);
    url.listen();
    url.sync();
    return platformRef;
  });
}

// This AngularJS module represents the AngularJS pieces of the application.
export const AppModule = 'app';

angular
  .module(AppModule, [
    downgradeModule(ng2BootstrapFn),
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

angular.bootstrap(document, [AppModule]);
