import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  NgModule,
  LOCALE_ID,
  CUSTOM_ELEMENTS_SCHEMA,
  NO_ERRORS_SCHEMA
} from '@angular/core';
import { NgIdleModule } from '@ng-idle/core';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CommonModuleTs, CommonModuleGlobal } from './common';
import { MaterialModule } from './material.module';
import { AppRoutingModule } from './app-routing.module';

import { AdminModule } from './modules/admin';
import { WorkbenchUpgradeModule } from './modules/workbench';

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
    BrowserAnimationsModule,
    AppRoutingModule,
    NgIdleModule.forRoot(),
    CommonModuleTs,
    CommonModuleGlobal.forRoot(),
    FlexLayoutModule,
    MaterialModule,
    WorkbenchUpgradeModule,
    AdminModule
  ],
  exports: [FlexLayoutModule],
  providers: [...SERVICES],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AppModule {}
