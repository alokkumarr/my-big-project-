import * as angular from 'angular';
import { downgradeComponent } from '@angular/upgrade/static';
import { NgModule } from '@angular/core';
import { CommonModule as AngularCommonModule } from '@angular/common';
import { Ng2DragDropModule } from 'ng2-drag-drop';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

import {MaterialModule} from '../../material.module';

import {ObserveService} from './services/observe.service';

import {ObservePageComponent} from './components/observe-page/observe-page.component';
import {CreateDashboardComponent} from './components/create-dashboard/create-dashboard.component';
import {NewDashboardComponent} from './components/new-dashboard/new-dashboard.component';
import {FilterSidenavComponent} from './components/filter-sidenav/filter-sidenav.component';
import {CheckboxFilterComponent} from './components/checkbox-filter/checkbox-filter.component';
import {PriceRangeFilterComponent} from './components/price-range-filter/price-range-filter.component';
import {RadioFilterComponent} from './components/radio-filter/radio-filter.component';
import {TimeRangeFilterComponent} from './components/time-range-filter/time-range-filter.component';
import {FilterGroupComponent} from './components/filter-group/filter-group.component';
import {CommonModule} from '../../common';

export const ObserveModule = 'ObserveModule';

@NgModule({
  imports: [ AngularCommonModule, MaterialModule, Ng2DragDropModule.forRoot() ],
  declarations: [
    ObservePageComponent,
    NewDashboardComponent,
    CreateDashboardComponent
  ],
  entryComponents: [
    ObservePageComponent,
    NewDashboardComponent
  ]
})
export class ObserveUpgradeModule {}

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
