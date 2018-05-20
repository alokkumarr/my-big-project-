import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import '@uirouter/angular-hybrid';
import * as angular from 'angular';
import 'angular-local-storage';
import 'angular-material';
import 'angular-sanitize';
import 'angular-translate';
import 'angular-translate/dist/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat';
import 'angular-translate/dist/angular-translate-loader-partial/angular-translate-loader-partial';
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

import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DndModule } from './dnd';
import { MaterialModule } from '../material.module';
import { UIRouterModule } from '@uirouter/angular';
import { BrowserModule } from '@angular/platform-browser';
import {
  DxPivotGridModule,
  DxPivotGridComponent,
  DxDataGridModule,
  DxDataGridComponent,
  DxTemplateModule
} from 'devextreme-angular';
import {downgradeComponent} from '@angular/upgrade/static';
import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';

import { ChartService } from './components/charts/chart.service';
import { CommonPipesModule } from './pipes/common-pipes.module';
import { CommonComponentModule } from './components';
import { CommonFilterModule } from './filters';
import { CommonDirectiveModule } from './directives';
// import from login module
import { AuthServiceFactory } from '../../login/services/auth.service';
import { PivotGridComponent } from './components/pivot-grid/pivot-grid.component';
import { AccordionMenuLinkComponent } from './components/accordionMenu/accordionMenuLink.component';
import { AccordionMenuComponent } from './components/accordionMenu/accordionMenu.component';
import { SidenavComponent } from './components/sidenav/sidenav.component';
import { ErrorDetailComponent } from './components/error-detail';
import { DataFormatDialogComponent } from './components/data-format-dialog';
import { ConfirmDialogComponent } from './components/confirm-dialog';
import { ReportGridComponent } from './components/report-grid';
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
import { ChoiceGroupComponent } from './components/choice-group-u';
import { AggregateChooserComponent } from './components/aggregate-chooser';
import { E2eDirective } from './directives/e2e.directive';
import { UserService } from '../../login/services/user.service';
import { JwtService } from '../../login/services/jwt.service';
import { ErrorDetailService } from './services/error-detail.service';
import { ErrorDetailDialogService } from './services/error-detail-dialog.service';
import { ClickToCopyDirective } from './directives/clickToCopy.directive';
import {
  toastProvider,
  componentHandlerProvider
} from './services/ajs-common-providers';
import { CommonServiceModule } from './services';

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
    DxDataGridModule,
    CommonPipesModule
  ],
  declarations: [
    PivotGridComponent,
    ReportGridComponent,
    ClickToCopyDirective,
    ErrorDetailComponent,
    E2eDirective,
    DataFormatDialogComponent,
    DateFormatDialogComponent,
    SidenavComponent,
    AccordionMenuComponent,
    AccordionMenuLinkComponent,
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
    AggregateChooserComponent,
    ChoiceGroupComponent,
    SearchBoxComponent
  ],
  entryComponents: [
    PivotGridComponent,
    ReportGridComponent,
    ErrorDetailComponent,
    DataFormatDialogComponent,
    DateFormatDialogComponent,
    SidenavComponent,
    AccordionMenuComponent,
    AccordionMenuLinkComponent,
    SearchBoxComponent,
    ConfirmDialogComponent,
    JsPlumbCanvasComponent,
    JsPlumbTableComponent,
    JsPlumbJoinLabelComponent,
    JoinDialogComponent,
    DateFormatDialogComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent,
    ChoiceGroupComponent,
    SearchBoxComponent
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
    SidenavComponent,
    AccordionMenuComponent,
    AccordionMenuLinkComponent,
    SearchBoxComponent,
    AliasRenameDialogComponent,
    AggregateChooserComponent,
    E2eDirective,
    ChoiceGroupComponent,
    SearchBoxComponent
  ],
  providers: [
    ErrorDetailService,
    ErrorDetailDialogService,
    toastProvider,
    ChartService,
    componentHandlerProvider
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CommonModuleTs {}
