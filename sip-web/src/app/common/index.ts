import 'devextreme/localization';
import 'devextreme/localization/messages/en.json';
import 'devextreme/ui/data_grid';
import 'mottle';

import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ClickOutsideModule } from 'ng-click-outside';
import { AngularSplitModule } from 'angular-split';

import {
  NgModule,
  CUSTOM_ELEMENTS_SCHEMA,
  ModuleWithProviders
} from '@angular/core';
import { NgxPopperModule } from 'ngx-popper';
import { FlexLayoutModule } from '@angular/flex-layout';
import { NgxsModule } from '@ngxs/store';
import { CommonState } from './state/common.state';
import { RouterModule } from '@angular/router';
import {
  DxPivotGridComponent,
  DxPivotGridModule
} from 'devextreme-angular/ui/pivot-grid';
import {
  DxDataGridComponent,
  DxDataGridModule
} from 'devextreme-angular/ui/data-grid';
import {
  DxTextBoxModule,
  DxButtonModule,
  DxSliderModule,
  DxTooltipModule
} from 'devextreme-angular';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { OwlDateTimeModule, OwlNativeDateTimeModule } from 'ng-pick-datetime';

import { DndModule } from './dnd';
import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor,
  ProgressIndicatorInterceptor
} from './interceptor';
import { SearchBoxComponent } from './components/search-box';
import {
  IsUserLoggedInGuard,
  DefaultModuleGuard,
  SSOAuthGuard
} from './guards';
import { MaterialModule } from '../material.module';
import { ChartService } from './components/charts/chart.service';
import { CommonPipesModule } from './pipes/common-pipes.module';
import { PivotGridComponent } from './components/pivot-grid/pivot-grid.component';
import { FieldDetailsComponent } from './components/field-details/field-details.component';
import {
  AccordionMenuComponent,
  AccordionMenuLinkComponent
} from './components/accordionMenu';
import { SidenavComponent, SidenavMenuService } from './components/sidenav';
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
  JsPlumbEndpointDirective
} from './components/js-plumb';
import { AliasRenameDialogComponent } from './components/alias-rename-dialog';
import { DateFormatDialogComponent } from './components/date-format-dialog';
import { ChoiceGroupComponent } from './components/choice-group';
import { AggregateChooserComponent } from './components/aggregate-chooser';
import { PasswordToggleComponent } from './components/password-toggle';
import { ClickToCopyDirective, E2eDirective } from './directives';
import { CronJobSchedularComponent } from './components/cron-scheduler/cron-job-schedular';
import { CronDatePickerComponent } from './components/cron-scheduler/cron-date-picker';
import { ChartGridComponent } from './components/chart-grid';
import { SSOAuthComponent } from './components/sso-auth/sso-auth.component';

import { UChartModule } from './components/charts';
import { MapBoxModule } from './map-box/map-box.module';

import {
  RemoteFolderSelectorComponent,
  CreatefolderDialogComponent
} from './components/remote-folder-selector';

import {
  DxDataGridService,
  ErrorDetailService,
  ErrorDetailDialogService,
  MenuService,
  LocalSearchService,
  ToastService,
  UserService,
  JwtService,
  ConfigService,
  SideNavService,
  WindowService,
  HeaderProgressService,
  DynamicModuleService,
  CustomIconService,
  DndPubsubService,
  CommonSemanticService
} from './services';

const MODULES = [
  CommonModuleAngular4,
  DxDataGridModule,
  DxPivotGridModule,
  DxDataGridModule,
  DxTextBoxModule,
  DxButtonModule,
  DxSliderModule,
  DxTooltipModule,
  RouterModule,
  FormsModule,
  ReactiveFormsModule,
  DxTemplateModule,
  MaterialModule,
  NgxPopperModule,
  FlexLayoutModule,
  DndModule,
  DragDropModule,
  CommonPipesModule,
  HttpClientModule,
  OwlDateTimeModule,
  OwlNativeDateTimeModule,
  UChartModule,
  ClickOutsideModule,
  MapBoxModule
];

const COMPONENTS = [
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
  JsPlumbConnectorComponent,
  JsPlumbCanvasComponent,
  JsPlumbTableComponent,
  JsPlumbJoinLabelComponent,
  JoinDialogComponent,
  DateFormatDialogComponent,
  AliasRenameDialogComponent,
  AggregateChooserComponent,
  PasswordToggleComponent,
  ChoiceGroupComponent,
  SearchBoxComponent,
  FieldDetailsComponent,
  CronDatePickerComponent,
  CronJobSchedularComponent,
  ChartGridComponent,
  RemoteFolderSelectorComponent,
  CreatefolderDialogComponent,
  SSOAuthComponent
];

const THIRD_PARTY_COMPONENTS = [DxPivotGridComponent, DxDataGridComponent];

const DIRECTIVES = [
  ClickToCopyDirective,
  E2eDirective,
  JsPlumbEndpointDirective
];

const SERVICES = [
  ChartService,
  ConfigService,
  DxDataGridService,
  DynamicModuleService,
  ErrorDetailDialogService,
  ErrorDetailService,
  HeaderProgressService,
  JwtService,
  LocalSearchService,
  MenuService,
  SideNavService,
  SidenavMenuService,
  ToastService,
  UserService,
  WindowService,
  CustomIconService,
  DndPubsubService,
  CommonSemanticService
];

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
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: ProgressIndicatorInterceptor,
    multi: true
  }
];

const GUARDS = [IsUserLoggedInGuard, DefaultModuleGuard, SSOAuthGuard];
@NgModule({
  imports: [
    NgxsModule.forFeature([CommonState]),
    AngularSplitModule.forRoot(),
    ...MODULES
  ],
  declarations: [...COMPONENTS, ...DIRECTIVES],
  entryComponents: COMPONENTS,
  exports: [
    ...MODULES,
    ...THIRD_PARTY_COMPONENTS,
    ...COMPONENTS,
    ...DIRECTIVES
  ],
  providers: [...INTERCEPTORS, ...GUARDS, CustomIconService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CommonModuleTs {
  constructor(private _customIconService: CustomIconService) {
    this._customIconService.init();
  }
}

/* CommonModuleGlobal exposes services that are shared for lazy loaded components as well */
@NgModule({})
export class CommonModuleGlobal {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: CommonModuleGlobal,
      providers: [...SERVICES]
    };
  }
}
