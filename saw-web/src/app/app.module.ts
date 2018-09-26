import 'zone.js/dist/zone';
import 'hammerjs';
import 'reflect-metadata';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule, LOCALE_ID, CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { NgIdleModule } from '@ng-idle/core';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CommonModuleTs } from './common';
import { MaterialModule } from './material.module';
import { AppRoutingModule } from './app-routing.module';

// import { ObserveUpgradeModule } from './modules/observe';
// import { AnalyzeModuleTs } from './modules/analyze';
// import { AdminModule } from './modules/admin';
// import { WorkbenchUpgradeModule } from './modules/workbench';
import { LoginModule } from './login';

import {
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent,
  MainPageComponent
} from './layout';

const COMPONENTS = [
  LayoutContentComponent,
  LayoutHeaderComponent,
  LayoutFooterComponent,
  MainPageComponent
];
const SERVICES = [{ provide: LOCALE_ID, useValue: 'en' }];

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgIdleModule.forRoot(),
    CommonModuleTs,
    FlexLayoutModule,
    MaterialModule,
    LoginModule,
    // AnalyzeModuleTs,
    // ObserveUpgradeModule,
    // WorkbenchUpgradeModule,
    // AdminModule
  ],
  exports: [FlexLayoutModule],
  providers: [...SERVICES],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AppModule { }
