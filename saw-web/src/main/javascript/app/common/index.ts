import 'jquery';
import * as angular from 'angular';
import '@uirouter/angular-hybrid';

import 'angular-material';

import 'angular-local-storage';
import 'angular-sanitize';
import 'angular-translate';
import 'angular-translate/dist/angular-translate-loader-partial/angular-translate-loader-partial';
import 'angular-translate/dist/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat';

import 'ng-idle';

import 'mottle';

import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import 'devextreme/localization';

import 'devextreme/localization/messages/en.json';

import 'devextreme/ui/data_grid';
import 'devextreme/integration/jquery';
import 'devextreme/integration/angular';

import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DndModule } from './dnd';
import { MaterialModule } from '../material.module';
import {CommonModule as CommonModuleAngular4} from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import {downgradeInjectable} from '@angular/upgrade/static';

import {
  DxPivotGridModule,
  DxPivotGridComponent,
  DxDataGridModule,
  DxDataGridComponent
} from 'devextreme-angular';
import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';

import { ChartService } from './components/charts/chart.service';
import {CommonServiceModule} from './services';
import {CommonComponentModule} from './components';
import {CommonFilterModule} from './filters';
import {CommonDirectiveModule} from './directives';
// import from login module
import {AuthServiceFactory} from '../../login/services/auth.service';
import {PivotGridComponent} from './components/pivot-grid/pivot-grid.component';
import {ErrorDetailComponent} from './components/error-detail';
import {DataFormatDialogComponent} from './components/data-format-dialog';
import {ConfirmDialogComponent} from './components/confirm-dialog';
import {ReportGridComponent} from './components/report-grid';
import {
  JsPlumbConnectorComponent,
  JsPlumbCanvasComponent,
  JsPlumbTableComponent,
  JsPlumbJoinLabelComponent,
  JoinDialogComponent,
  JsPlumbEndpointComponent
} from './components/js-plumb';
import { AliasRenameDialogComponent } from './components/alias-rename-dialog';
import {DateFormatDialogComponent} from './components/date-format-dialog';
import { AggregateChooserComponent } from './components/aggregate-chooser';
import {E2eDirective} from './directives/e2e.directive';
import {UserService} from '../../login/services/user.service';
import {JwtService} from '../../login/services/jwt.service';
import {ErrorDetailService} from './services/error-detail.service';
import {ErrorDetailDialogService} from './services/error-detail-dialog.service';
import { ClickToCopyDirective } from './directives/clickToCopy.directive';
import {
  toastProvider
} from './services/ajs-common-providers';

import AppConfig from '../../../../../appConfig';

export const CommonModule = 'CommonModule';

const moduleDependencies = [
  'ui.router',
  'ui.router.upgrade',
  'LocalStorageModule',
  'ngSanitize',
  'ngMaterial',
  'ngIdle',
  'dx',
  'pascalprecht.translate',
  CommonServiceModule,
  CommonComponentModule,
  CommonFilterModule,
  CommonDirectiveModule
];

// if (__MOCK__) {
//   const {MockModule} = require('./mock');

//   moduleDependencies.push(MockModule);
// }

angular
  .module(CommonModule, moduleDependencies)
  .value('AppConfig', AppConfig)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  })
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', downgradeInjectable(UserService) as Function)
  //.service('UserService', UserService)
  //.service('JwtService', JwtService)
  .factory('JwtService', downgradeInjectable(JwtService) as Function);

@NgModule({
  imports: [
    CommonModuleAngular4,
    BrowserModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule,
    DndModule,
    DxPivotGridModule,
    DxDataGridModule
  ],
  declarations: [
    PivotGridComponent,
    ReportGridComponent,
    ClickToCopyDirective,
    ErrorDetailComponent,
    E2eDirective,
    DataFormatDialogComponent,
    ConfirmDialogComponent,
    JsPlumbCanvasComponent,
    JsPlumbEndpointComponent,
    JsPlumbTableComponent,
    JsPlumbConnectorComponent,
    JsPlumbJoinLabelComponent,
    JoinDialogComponent,
    DateFormatDialogComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent
  ],
  entryComponents: [
    PivotGridComponent,
    ReportGridComponent,
    ErrorDetailComponent,
    DataFormatDialogComponent,
    ConfirmDialogComponent,
    JsPlumbCanvasComponent,
    JsPlumbTableComponent,
    JsPlumbJoinLabelComponent,
    JoinDialogComponent,
    DateFormatDialogComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent
  ],
  exports: [
    DndModule,
    PivotGridComponent,
    ReportGridComponent,
    DxPivotGridComponent,
    DxDataGridComponent,
    ClickToCopyDirective,
    ErrorDetailComponent,
    DataFormatDialogComponent,
    ConfirmDialogComponent,
    JsPlumbCanvasComponent,
    JsPlumbEndpointComponent,
    JsPlumbTableComponent,
    JsPlumbConnectorComponent,
    JsPlumbJoinLabelComponent,
    JoinDialogComponent,
    DateFormatDialogComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent,
    E2eDirective
  ],
  providers: [
    ErrorDetailService,
    ErrorDetailDialogService,
    toastProvider,
    ChartService, UserService, JwtService
  ]
})
export class CommonModuleTs {}
