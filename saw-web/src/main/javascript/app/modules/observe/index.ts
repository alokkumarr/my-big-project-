import * as angular from 'angular';
import { downgradeComponent } from '@angular/upgrade/static';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GridsterModule } from 'angular-gridster2';

import { routesConfig } from './routes';
import { i18nConfig } from './i18n';

import { MaterialModule } from '../../material.module';

import { jwtServiceProvider } from '../../../login/services/ajs-login-providers';
import {
  analyzeServiceProvider,
  chartServiceProvider,
  sortServiceProvider,
  filterServiceProvider
} from '../analyze/services/ajs-analyze-providers';
import {
  menuServiceProvider,
  stateParamsProvider,
  stateProvider,
  componentHandlerProvider,
  headerProgressProvider
} from '../../common/services/ajs-common-providers';
import { ObserveService } from './services/observe.service';

import { ChartComponent } from '../../common/components/charts/chart.component';

import { ObservePageComponent } from './components/observe-page/observe-page.component';
import { ObserveChartComponent } from './components/observe-chart/observe-chart.component';
import { DashboardGridComponent } from './components/dashboard-grid/dashboard-grid.component';
import { AnalysisChoiceComponent } from './components/analysis-choice/analysis-choice.component';
import { SaveDashboardComponent } from './components/save-dashboard/save-dashboard.component';
import { CreateDashboardComponent } from './components/create-dashboard/create-dashboard.component';
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
  .config(i18nConfig)
  .factory('ObserveService', ObserveService)
  .directive(
    'observePage',
    downgradeComponent({component: ObservePageComponent})
  )
  .component('filterSidenav', FilterSidenavComponent)
  .component('checkboxFilter', CheckboxFilterComponent)
  .component('priceRangeFilter', PriceRangeFilterComponent)
  .component('radioFilter', RadioFilterComponent)
  .component('timeRangeFilter', TimeRangeFilterComponent)
  .component('filterGroup', FilterGroupComponent);

const components = [
  ObservePageComponent,
  DashboardGridComponent,
  CreateDashboardComponent,
  AnalysisChoiceComponent,
  ObserveChartComponent,
  SaveDashboardComponent,
  ChartComponent
];

@NgModule({
  imports: [ AngularCommonModule, FormsModule, MaterialModule, GridsterModule, HttpClientModule ],
  declarations: components,
  entryComponents: components,
  providers: [
    ObserveService,
    jwtServiceProvider,
    analyzeServiceProvider,
    menuServiceProvider,
    stateParamsProvider,
    stateProvider,
    componentHandlerProvider,
    headerProgressProvider,
    chartServiceProvider,
    sortServiceProvider,
    filterServiceProvider
  ]
})
export class ObserveUpgradeModule {}
