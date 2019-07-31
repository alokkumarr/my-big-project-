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
import { AnalyzeModuleGlobal } from './modules/analyze/analyze.global.module';
import { MaterialModule } from './material.module';
import { AppRoutingModule } from './app-routing.module';
import { NgxsModule } from '@ngxs/store';
import { NgxsReduxDevtoolsPluginModule } from '@ngxs/devtools-plugin';
import { environment } from '../environments/environment';

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
    // disabled development mode until all the designer is refactored to use ngxs
    // because when only partially implemented, freeing objects introduces some conflicts
    // NgxsModule.forRoot([], { developmentMode: !environment.production }),
    NgxsModule.forRoot([], { developmentMode: false }),
    NgxsReduxDevtoolsPluginModule.forRoot({ disabled: environment.production }),
    AppRoutingModule,
    NgIdleModule.forRoot(),
    CommonModuleTs,
    CommonModuleGlobal.forRoot(),
    AnalyzeModuleGlobal.forRoot(),
    FlexLayoutModule,
    MaterialModule
  ],
  exports: [FlexLayoutModule],
  providers: [...SERVICES],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AppModule {}
