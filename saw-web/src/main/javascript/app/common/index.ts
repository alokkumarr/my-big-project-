import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from './interceptor';
import '@uirouter/angular-hybrid';
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
// import from login module
import { SearchBoxComponent } from './components/search-box';
import 'devextreme/integration/jquery';
import 'devextreme/integration/angular';

import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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

import { ChartService } from './components/charts/chart.service';
import { CommonPipesModule } from './pipes/common-pipes.module';
// import from login module
import { PivotGridComponent } from './components/pivot-grid/pivot-grid.component';
import { FieldDetailsComponent } from './components/field-details/field-details.component';
import {
  AccordionMenuComponent,
  AccordionMenuLinkComponent
} from './components/accordionMenu';
import {
  SidenavComponent,
  SidenavMenuService
} from './components/sidenav';
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
import { ChoiceGroupComponent } from './components/choice-group';
import { AggregateChooserComponent } from './components/aggregate-chooser';
import {  } from './directives/e2e.directive';
import {
  ClickToCopyDirective,
  E2eDirective
} from './directives';
import { UserService } from '../../login/services/user.service';
import { JwtService } from '../../login/services/jwt.service';
import { ErrorDetailService } from './services/error-detail.service';
import { ErrorDetailDialogService } from './services/error-detail-dialog.service';
import { MenuService } from './services/menu.service';
import {
  ToastService,
  ComponentHandler
} from './services';


@NgModule({
  imports: [
    CommonModuleAngular4,
    UIRouterModule,
    BrowserModule,
    DxDataGridModule,
    DxTemplateModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    FlexLayoutModule,
    DndModule,
    DxPivotGridModule,
    DxDataGridModule,
    CommonPipesModule,
    HttpClientModule
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
    SearchBoxComponent,
    FieldDetailsComponent
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
    SearchBoxComponent,
    FieldDetailsComponent
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
    ReactiveFormsModule,
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
    },
    SidenavMenuService,
    ErrorDetailService,
    ErrorDetailDialogService,
    ToastService,
    ChartService,
    ComponentHandler,
    JwtService,
    UserService,
    MenuService
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CommonModuleTs {}
