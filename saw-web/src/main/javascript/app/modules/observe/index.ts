import * as angular from 'angular';
import { downgradeComponent } from '@angular/upgrade/static';
import { NgModule } from '@angular/core';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GridsterModule } from 'angular-gridster2';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';

import { MaterialModule } from '../../material.module';

import { jwtServiceProvider, userServiceProvider } from '../../../login/services/ajs-login-providers';
import {
  analyzeServiceProvider,
  chartServiceProvider,
  sortServiceProvider,
  filterServiceProvider
} from '../analyze/services/ajs-analyze-providers';
import {
  menuServiceProvider,
  componentHandlerProvider,
  headerProgressProvider,
  toastProvider,
  sidenavProvider
} from '../../common/services/ajs-common-providers';
import { ObserveService } from './services/observe.service';

import { AddTokenInterceptor } from './services/add-token.interceptor';
import { HandleErrorInterceptor } from './services/handle-error.interceptor';
import { RefreshTokenInterceptor } from './services/refresh-token.interceptor';

import { ChartComponent } from '../../common/components/charts/chart.component';

import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveViewComponent } from './components/observe-view/observe-view.component';
import { ObserveChartComponent } from './components/observe-chart/observe-chart.component';
import { AddWidgetModule } from './components/add-widget/add-widget.module';
import { DashboardGridComponent } from './components/dashboard-grid/dashboard-grid.component';
import { AnalysisChoiceComponent } from './components/analysis-choice/analysis-choice.component';
import { SaveDashboardComponent } from './components/save-dashboard/save-dashboard.component';
import { ConfirmDialogComponent } from './components/dialogs/confirm-dialog/confirm-dialog.component';
import { CreateDashboardComponent } from './components/create-dashboard/create-dashboard.component';
import {
  GlobalFilterComponent,
  GlobalDateFilterComponent,
  GlobalNumberFilterComponent,
  GlobalStringFilterComponent
} from './components/global-filter';
import { GlobalFilterService } from './services/global-filter.service';
import { FilterSidenavComponent } from './components/filter-sidenav/filter-sidenav.component';
import { CheckboxFilterComponent } from './components/checkbox-filter/checkbox-filter.component';
import { PriceRangeFilterComponent } from './components/price-range-filter/price-range-filter.component';
import { RadioFilterComponent } from './components/radio-filter/radio-filter.component';
import { TimeRangeFilterComponent } from './components/time-range-filter/time-range-filter.component';
import { FilterGroupComponent } from './components/filter-group/filter-group.component';
import { CommonModule } from '../../common';

export const ObserveModule = 'ObserveModule';

angular.module(ObserveModule, [
  CommonModule
])
  .config(routesConfig)
  .config(i18nConfig);

const components = [
  ObservePageComponent,
  ObserveViewComponent,
  DashboardGridComponent,
  GlobalFilterComponent,
  GlobalNumberFilterComponent,
  GlobalDateFilterComponent,
  GlobalStringFilterComponent,
  CreateDashboardComponent,
  AnalysisChoiceComponent,
  ObserveChartComponent,
  SaveDashboardComponent,
  ConfirmDialogComponent,
  ChartComponent
];

@NgModule({
  imports: [
    AngularCommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    GridsterModule,
    HttpClientModule,
    UIRouterUpgradeModule,
    AddWidgetModule
  ],
  declarations: components,
  entryComponents: components,
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AddTokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: HandleErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: RefreshTokenInterceptor, multi: true },
    GlobalFilterService,
    ObserveService,
    jwtServiceProvider,
    userServiceProvider,
    analyzeServiceProvider,
    menuServiceProvider,
    componentHandlerProvider,
    headerProgressProvider,
    toastProvider,
    sidenavProvider,
    chartServiceProvider,
    sortServiceProvider,
    filterServiceProvider
  ]
})
export class ObserveUpgradeModule {}
