import 'fonts/icomoon.css';
import '../../../../assets/additional-icons.css';

import 'zone.js/dist/zone';
import 'hammerjs';
import 'reflect-metadata';
import '../../../../themes/_triton.scss';
import { NgModule, LOCALE_ID } from '@angular/core';
import { NgIdleModule } from '@ng-idle/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserModule } from '@angular/platform-browser';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { RouterModule }  from '@angular/router';
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
  LayoutFooterComponent,
  MainPageComponent
 } from './layout';

import { ServiceBootstrapComponent } from './service-bootstrap.component';

declare global {
  const require: any;
}

const COMPONENTS = [
  ServiceBootstrapComponent,
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent,
  MainPageComponent
];
const SERVICES = [{ provide: LOCALE_ID, useValue: 'en' }];

@NgModule({
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes, {useHash: true}),
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
  providers: [
    ...SERVICES
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent]
})
export class NewAppModule {
  constructor() {}
}


platformBrowserDynamic().bootstrapModule(NewAppModule);
