import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GridsterModule } from 'angular-gridster2';
import { CountoModule } from 'angular2-counto';
import { UIRouterModule } from '@uirouter/angular';
import { routes } from './routes';

import {
  DxDataGridModule,
  DxDataGridComponent,
  DxTemplateModule
} from 'devextreme-angular';

import { MaterialModule } from '../../material.module';
import { UserService } from '../../../login/services/user.service';
import { JwtService } from '../../../login/services/jwt.service';
import { AnalyzeService } from '../analyze/services/analyze.service';
import { FilterService } from '../analyze/services/filter.service';
import {
  MenuService,
  ComponentHandler,
  HeaderProgressService,
  ToastService,
  SideNavService
} from '../../common/services';
import { ObserveService } from './services/observe.service';

import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from '../../common/interceptor';

import { UChartModule } from '../../common/components/charts';

import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';
import { ObserveChartComponent } from './components/observe-chart/observe-chart.component';
import { ObserveReportComponent } from './components/observe-report/observe-report.component';
import { ObservePivotComponent } from './components/observe-pivot/observe-pivot.component';
import { ObserveKPIComponent } from './components/observe-kpi/observe-kpi.component';
import { ObserveKPIBulletComponent } from './components/observe-kpi-bullet/observe-kpi-bullet.component';
import { KPIFilter } from './components/kpi-filter/kpi-filter.component';
import { AddWidgetModule } from './components/add-widget/add-widget.module';
import { EditWidgetModule } from './components/edit-widget/edit-widget.module';
import { DashboardGridComponent } from './components/dashboard-grid/dashboard-grid.component';
import { SaveDashboardComponent } from './components/save-dashboard/save-dashboard.component';
import { ConfirmDialogComponent } from './components/dialogs/confirm-dialog/confirm-dialog.component';
import { CreateDashboardComponent } from './components/create-dashboard/create-dashboard.component';
import {
  GlobalFilterComponent,
  GlobalDateFilterComponent,
  GlobalNumberFilterComponent,
  GlobalStringFilterComponent
} from './components/global-filter';
import { DashboardService } from './services/dashboard.service';
import { CommonModuleTs } from '../../common';

const components = [
  ObservePageComponent,
  ObserveViewComponent,
  DashboardGridComponent,
  GlobalFilterComponent,
  GlobalNumberFilterComponent,
  GlobalDateFilterComponent,
  GlobalStringFilterComponent,
  CreateDashboardComponent,
  ObserveChartComponent,
  ObserveReportComponent,
  ObservePivotComponent,
  ObserveKPIComponent,
  ObserveKPIBulletComponent,
  KPIFilter,
  SaveDashboardComponent,
  ConfirmDialogComponent
];

@NgModule({
  imports: [
    AngularCommonModule,
    UIRouterModule.forChild({states: routes}),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    GridsterModule,
    HttpClientModule,
    CommonModuleTs,
    AddWidgetModule,
    EditWidgetModule,
    UChartModule,
    CountoModule,
    DxDataGridModule,
    DxTemplateModule
  ],
  declarations: components,
  entryComponents: components,
  exports: [
    DxDataGridModule,
    DxDataGridComponent,
    DxTemplateModule
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
    DashboardService,
    ObserveService,
    JwtService,
    UserService,
    AnalyzeService,
    MenuService,
    ComponentHandler,
    HeaderProgressService,
    ToastService,
    SideNavService,
    FilterService
  ]
})
export class ObserveUpgradeModule {}
