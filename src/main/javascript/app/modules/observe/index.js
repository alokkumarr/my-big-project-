import angular from 'angular';

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

export const ObserveModule = 'ObserveModule';

angular.module(ObserveModule, [])
  .config(routesConfig)
  .config(i18nConfig)
  .factory('ObserveService', ObserveService)
  .component('observePage', ObservePageComponent)
  .component('filterSidenav', FilterSidenavComponent)
  .component('checkboxFilter', CheckboxFilterComponent)
  .component('priceRangeFilter', PriceRangeFilterComponent)
  .component('radioFilter', RadioFilterComponent)
  .component('timeRangeFilter', TimeRangeFilterComponent)
  .component('filterGroup', FilterGroupComponent);
