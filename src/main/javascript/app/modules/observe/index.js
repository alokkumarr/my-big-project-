import * as angular from 'angular';
import { downgradeComponent } from '@angular/upgrade/static';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

import {ObserveService} from './services/observe.service';

import {ObservePageComponent} from './components/observe-page/observe-page.component';
import {FilterSidenavComponent} from './components/filter-sidenav/filter-sidenav.component';
import {CheckboxFilterComponent} from './components/checkbox-filter/checkbox-filter.component';
import {PriceRangeFilterComponent} from './components/price-range-filter/price-range-filter.component';
import {RadioFilterComponent} from './components/radio-filter/radio-filter.component';
import {TimeRangeFilterComponent} from './components/time-range-filter/time-range-filter.component';
import {FilterGroupComponent} from './components/filter-group/filter-group.component';
import {CommonModule} from '../../common';

export const ObserveModule = 'ObserveModule';

@NgModule({
  declarations: [ ObservePageComponent ],
  entryComponents: [ ObservePageComponent ],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
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
