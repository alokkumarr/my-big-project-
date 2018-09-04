import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js/dist/zone';
import 'hammerjs';
import 'reflect-metadata';
import { NgModule, LOCALE_ID } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserModule } from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UIRouterModule } from '@uirouter/angular';
import { MaterialModule } from './material.module';

import { routes } from './routes';

import { ObserveUpgradeModule } from './modules/observe';
import { CommonModuleTs } from './common';
import { AnalyzeModuleTs } from './modules/analyze';
import { AdminModule } from './modules/admin';
import { WorkbenchUpgradeModule } from './modules/workbench';

import {
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent
 } from './layout';

import { ServiceBootstrapComponent } from './service-bootstrap.component';

declare global {
  const require: any;
}

@NgModule({
  imports: [
    BrowserModule,
    UIRouterModule.forRoot({states: routes, useHash: true}),
    CommonModuleTs,
    AnalyzeModuleTs,
    ObserveUpgradeModule,
    FlexLayoutModule,
    WorkbenchUpgradeModule,
    MaterialModule,
    AdminModule
  ],
  exports: [FlexLayoutModule],
  providers: [{ provide: LOCALE_ID, useValue: 'en' }],
  declarations: [
    ServiceBootstrapComponent,
    LayoutContentComponent,
    LayoutHeaderComponent,
    LayoutFooterComponent
  ],
  entryComponents: [
    ServiceBootstrapComponent,
    LayoutContentComponent,
    LayoutHeaderComponent,
    LayoutFooterComponent
  ],
  bootstrap: [LayoutContentComponent]
})
export class NewAppModule {
  constructor() {}
}


platformBrowserDynamic().bootstrapModule(NewAppModule);
