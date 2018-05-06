import * as angular from 'angular';

import 'angular-material/angular-material.css';

import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js/dist/zone';
import 'hammerjs';
import 'reflect-metadata';
import { NgModule, StaticProvider, LOCALE_ID, Injector } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { UrlService } from '@uirouter/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule, downgradeModule, downgradeComponent } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import {MaterialModule} from './material.module';

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
import { WorkbenchModule, WorkbenchUpgradeModule } from './modules/workbench';

import { LayoutContentComponent } from './layout';

import { LayoutHeaderComponent } from './layout/components/header/header.component';
import { LayoutFooterComponent } from './layout/components/footer/footer.component';
import { ServiceBootstrapComponent } from './service-bootstrap.component';

declare global {
  const require: any;
}

@NgModule({
  imports: [
    BrowserModule,
    UpgradeModule,
    UIRouterUpgradeModule,
    CommonModuleTs,
    AnalyzeModuleTs,
    ObserveUpgradeModule,
    FlexLayoutModule,
    WorkbenchUpgradeModule,
    MaterialModule
  ],
  exports: [FlexLayoutModule],
  providers: [
    {provide: LOCALE_ID, useValue: 'en'}
  ],
  declarations: [ServiceBootstrapComponent, LayoutHeaderComponent, LayoutFooterComponent],
  entryComponents: [ServiceBootstrapComponent, LayoutHeaderComponent, LayoutFooterComponent]
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
    AdminModule,
    WorkbenchModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .config(i18nConfig)
  .config(config)
  .config(interceptor)
  .run(runConfig)
  .directive(
    'serviceBootstrap',
    downgradeComponent({ component: ServiceBootstrapComponent }) as angular.IDirectiveFactory
  )
  .directive(
    'layoutHeader',
    downgradeComponent({component: LayoutHeaderComponent}) as angular.IDirectiveFactory
  )
  .component('layoutContent', LayoutContentComponent)
  // .directive(
  //   'layoutContent',
  //   downgradeComponent({component: LayoutContentComponent}) as angular.IDirectiveFactory
  // )
  .directive(
    'layoutFooter',
    downgradeComponent({component: LayoutFooterComponent}) as angular.IDirectiveFactory
  );

angular.bootstrap(document, [AppModule]);
