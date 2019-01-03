import 'devextreme/localization';
import 'devextreme/localization/messages/en.json';
import 'devextreme/ui/data_grid';
import 'mottle';

import { CommonModule as CommonModuleAngular4 } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  NgModule,
  CUSTOM_ELEMENTS_SCHEMA,
  ModuleWithProviders
} from '@angular/core';
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
import { IsUserLoggedInGuard, DefaultModuleGuard } from './guards';
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
import { ClickToCopyDirective, E2eDirective } from './directives';
import { CronJobSchedularComponent } from './components/cron-scheduler/cron-job-schedular';
import { CronDatePickerComponent } from './components/cron-scheduler/cron-date-picker';
import { ChartGridComponent } from './components/chart-grid';

import { UChartModule } from './components/charts';

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
  CustomIconService
} from './services';

const MODULES = [
  CommonModuleAngular4,
  RouterModule,
  FormsModule,
  ReactiveFormsModule,
  DxDataGridModule,
  DxTemplateModule,
  MaterialModule,
  FlexLayoutModule,
  DndModule,
  DxPivotGridModule,
  DxDataGridModule,
  CommonPipesModule,
  HttpClientModule,
  OwlDateTimeModule,
  OwlNativeDateTimeModule,
  UChartModule
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
  ChoiceGroupComponent,
  SearchBoxComponent,
  FieldDetailsComponent,
  CronDatePickerComponent,
  CronJobSchedularComponent,
  ChartGridComponent,
  RemoteFolderSelectorComponent,
  CreatefolderDialogComponent
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
  CustomIconService
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

const GUARDS = [IsUserLoggedInGuard, DefaultModuleGuard];
@NgModule({
  imports: [NgxsModule.forFeature([CommonState]), ...MODULES],
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
