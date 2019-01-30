import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GridsterModule } from 'angular-gridster2';
import { CountoModule } from 'angular2-counto';
import { RouterModule } from '@angular/router';
import { routes } from './routes';

import { DxTemplateModule } from 'devextreme-angular/core/template';
import {
  DxDataGridComponent,
  DxDataGridModule
} from 'devextreme-angular/ui/data-grid';

import { MaterialModule } from '../../material.module';
import { JwtService, UserService } from '../../common/services';
import { AnalyzeService } from '../analyze/services/analyze.service';
import { FilterService } from '../analyze/services/filter.service';
import {
  MenuService,
  ToastService,
  SideNavService
} from '../../common/services';
import { ObserveService } from './services/observe.service';

import { UChartModule } from '../../common/components/charts';

import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';
import { ObserveChartComponent } from './components/observe-chart/observe-chart.component';
import { ObserveMapChartComponent } from './components/observe-map-chart/observe-map-chart.component';
import { ObserveReportComponent } from './components/observe-report/observe-report.component';
import { ObservePivotComponent } from './components/observe-pivot/observe-pivot.component';
import { ObserveKPIComponent } from './components/observe-kpi/observe-kpi.component';
import { ObserveKPIBulletComponent } from './components/observe-kpi-bullet/observe-kpi-bullet.component';
import { KPIFilterComponent } from './components/kpi-filter/kpi-filter.component';
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
import { FirstDashboardGuard } from './guards';
import { ZoomAnalysisComponent } from './components/zoom-analysis/zoom-analysis.component';

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
  ObserveMapChartComponent,
  ObserveReportComponent,
  ObservePivotComponent,
  ObserveKPIComponent,
  ObserveKPIBulletComponent,
  KPIFilterComponent,
  SaveDashboardComponent,
  ConfirmDialogComponent,
  ZoomAnalysisComponent
];

const GUARDS = [FirstDashboardGuard];

@NgModule({
  imports: [
    AngularCommonModule,
    RouterModule.forChild(routes),
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
  exports: [DxDataGridModule, DxDataGridComponent, DxTemplateModule],
  providers: [
    DashboardService,
    ObserveService,
    JwtService,
    UserService,
    AnalyzeService,
    MenuService,
    ToastService,
    SideNavService,
    FilterService,
    ...GUARDS
  ]
})
export class ObserveUpgradeModule {}
