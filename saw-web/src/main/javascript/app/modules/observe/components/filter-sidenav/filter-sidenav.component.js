import * as cloneDeep from 'lodash/cloneDeep';
import * as map from 'lodash/map';
import * as omit from 'lodash/omit';

import * as template from './filter-sidenav.component.html';

export const OBSERVE_FILTER_SIDENAV_ID = 'observe-filter-sidenav';
export const FilterSidenavComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    ngModel: '<'
  },
  controller: class FilterSidenavController {
    constructor($mdSidenav) {
      'ngInject';
      this.$mdSidenav = $mdSidenav;
      this.id = OBSERVE_FILTER_SIDENAV_ID;
      this.affiliates = ['DIRECT TV', 'Red Ventures', 'ClearLink', 'All Connect', 'Q-ology', 'Acceller'];
      this.regions = ['Southeast', 'Southwest', 'Midwest', 'West', 'East', 'Unknown'];
      this.filters = [{
        label: 'Time range',
        type: 'time-range',
        items: []
      }, {
        label: 'Affiliates',
        type: 'radio',
        items: this.affiliates
      }, {
        label: 'Regions',
        type: 'checkbox',
        items: this.regions
      }, {
        label: 'Price range',
        type: 'price-range',
        items: []
      }];

      this.appliedFilters = [];
    }

    $onChanges() {
      if (this.ngModel) {
        this.appliedFilters = cloneDeep(this.ngModel);
      }
    }

    onFiltersApplied() {
      this.ngModelController.$setViewValue(cloneDeep(this.appliedFilters));
      this.$mdSidenav(OBSERVE_FILTER_SIDENAV_ID).toggle();
    }

    clearAllFilters() {
      this.appliedFilters = map(this.appliedFilters, filter => omit(filter, 'model'));
      this.ngModelController.$setViewValue(cloneDeep(this.appliedFilters));
      this.$mdSidenav(OBSERVE_FILTER_SIDENAV_ID).toggle();
    }
  }
};