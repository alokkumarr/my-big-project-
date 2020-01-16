import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  NgModule,
  LOCALE_ID,
  CUSTOM_ELEMENTS_SCHEMA,
  NO_ERRORS_SCHEMA
} from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgIdleModule } from '@ng-idle/core';
import { FlexLayoutModule } from '@angular/flex-layout';

import { CommonModuleTs, CommonModuleGlobal } from './common';

import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from './common/interceptor';
import { AnalyzeModuleGlobal } from './modules/analyze/analyze.global.module';
import { MaterialModule } from './material.module';
import { AppRoutingModule } from './app-routing.module';
import { NgxsModule } from '@ngxs/store';
import { NgxsReduxDevtoolsPluginModule } from '@ngxs/devtools-plugin';
import { environment } from '../environments/environment';
import { DeleteDialogComponent } from './common/components/delete-dialog/delete-dialog.component';
import { AnalyzeFilterModule } from './modules/analyze/designer/filter';

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
  MainPageComponent,
  DeleteDialogComponent
];
const SERVICES = [{ provide: LOCALE_ID, useValue: 'en' }];

const INTERCEPTORS = [
  { provide: HTTP_INTERCEPTORS, useClass: AddTokenInterceptor, multi: true },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: HandleErrorInterceptor,
    multi: true
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: RefreshTokenInterceptor,
    multi: true
  }
];

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
    AnalyzeFilterModule,
    MaterialModule
  ],
  exports: [FlexLayoutModule],
  providers: [...INTERCEPTORS, ...SERVICES],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class AppModule {}
