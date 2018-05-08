import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { downgradeComponent } from '@angular/upgrade/static';
import { UIRouterModule } from '@uirouter/angular';
import '@uirouter/angular-hybrid';
import * as angular from 'angular';
import 'angular-local-storage';
import 'angular-material';
import 'angular-sanitize';
import 'angular-translate';
import 'angular-translate/dist/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat';
import 'angular-translate/dist/angular-translate-loader-partial/angular-translate-loader-partial';
import {
  DxPivotGridModule,
  DxPivotGridComponent,
  DxDataGridModule,
  DxDataGridComponent,
  DxTemplateModule
} from 'devextreme-angular';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';
import 'devextreme/integration/angular';
import 'devextreme/integration/jquery';
import 'devextreme/localization';
import 'devextreme/localization/messages/en.json';
import 'devextreme/ui/data_grid';
import 'jquery';
import 'mottle';
import 'ng-idle';
import AppConfig from '../../../../../appConfig';
// import from login module
import { SearchBoxComponent } from './components/search-box';
import 'devextreme/integration/jquery';
import 'devextreme/integration/angular';

import { MaterialModule } from '../material.module';

import { ChartService } from './components/charts/chart.service';
import {CommonComponentModule} from './components';
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
import {UserService} from '../../login/services/user.service';
import {JwtService} from '../../login/services/jwt.service';
import { ClickToCopyDirective } from './directives/clickToCopy.directive';
import { E2eDirective } from './directives/e2e.directive';
import { DndModule } from './dnd';
import { CommonFilterModule } from './filters';
import { CommonPipesModule } from './pipes/common-pipes.module';
import { CommonServiceModule } from './services';
import { toastProvider } from './services/ajs-common-providers';
import { ErrorDetailDialogService } from './services/error-detail-dialog.service';
import { ErrorDetailService } from './services/error-detail.service';
import ComponentHandler from './utils/componentHandler';
import EventEmitter from './utils/eventEmitter';

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
  .directive('searchBox', downgradeComponent({component: SearchBoxComponent}))
  .factory('AuthService', AuthServiceFactory)
  .service('UserService', UserService)
  .service('JwtService', JwtService);

@NgModule({
  imports: [
    CommonModuleAngular4,
    UIRouterModule,
    BrowserModule,
    DxDataGridModule,
    DxTemplateModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule,
    DndModule,
    DxPivotGridModule,
    CommonPipesModule,
    DxDataGridModule
  ],
  declarations: [
    PivotGridComponent,
    ReportGridComponent,
    ClickToCopyDirective,
    ErrorDetailComponent,
    E2eDirective,
    DataFormatDialogComponent,
    DateFormatDialogComponent,
    SearchBoxComponent,
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
    DateFormatDialogComponent,
    SearchBoxComponent,
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
    FlexLayoutModule,
    CommonModuleAngular4,
    UIRouterModule,
    BrowserModule,
    DxDataGridModule,
    DxTemplateModule,
    FormsModule,
    MaterialModule,
    CommonPipesModule,
    DxDataGridModule,
    DxTemplateModule,
    CommonPipesModule,
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
    E2eDirective,
    SearchBoxComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent,
    E2eDirective
  ],
  providers: [
    ErrorDetailService,
    ErrorDetailDialogService,
    toastProvider,
    ChartService
  ]
})
export class CommonModuleTs {}
