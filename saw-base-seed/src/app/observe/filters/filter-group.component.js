import cloneDeep from 'lodash/cloneDeep';
import isEmpty from 'lodash/isEmpty';

import template from './filter-group.component.html';

export const FilterGroupComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    filters: '<',
    ngModel: '<'
  },
  controller: class FilterGroupController {
    $onChanges() {
      if (!this.ngModel || isEmpty(this.ngModel)) {
        this.ngModel = cloneDeep(this.filters);
      }
    }

    onFilterSelected() {
      this.ngModelController.$setViewValue(this.ngModel);
    }
  }
};
