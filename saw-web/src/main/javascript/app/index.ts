import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js/dist/zone';
import 'hammerjs';
import 'reflect-metadata';
import '../../../../themes/_triton.scss';
import { NgModule, LOCALE_ID, enableProdMode } from '@angular/core';
import { NgIdleModule } from '@ng-idle/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserModule } from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { RouterModule } from '@angular/router';
import { MaterialModule } from './material.module';

import { routes } from './routes';

import { ObserveUpgradeModule } from './modules/observe';
import { CommonModuleTs } from './common';
import { AnalyzeModuleTs } from './modules/analyze';
import { AdminModule } from './modules/admin';
import { WorkbenchUpgradeModule } from './modules/workbench';
import { DefaultHomePageGuard } from './common/guards';

import {
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent,
  MainPageComponent
} from './layout';

import { ServiceBootstrapComponent } from './service-bootstrap.component';

declare global {
  const require: any;
}

declare const __PRODUCTION__: any;

const COMPONENTS = [
  ServiceBootstrapComponent,
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent,
  MainPageComponent
];
const SERVICES = [{ provide: LOCALE_ID, useValue: 'en' }];
const GUARDS = [DefaultHomePageGuard];

@NgModule({
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes, {
      useHash: true,
      onSameUrlNavigation: 'reload'
    }),
    NgIdleModule.forRoot(),
    CommonModuleTs,
    AnalyzeModuleTs,
    ObserveUpgradeModule,
    FlexLayoutModule,
    WorkbenchUpgradeModule,
    MaterialModule,
    AdminModule
  ],
  exports: [FlexLayoutModule],
  providers: [...SERVICES, ...GUARDS],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent]
})
export class NewAppModule {
  constructor() {}
}

if (__PRODUCTION__) {
  enableProdMode();
}
platformBrowserDynamic().bootstrapModule(NewAppModule);
