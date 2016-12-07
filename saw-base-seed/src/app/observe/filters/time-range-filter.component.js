import template from './time-range-filter.component.html';

export const TimeRangeFilterComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    filter: '<',
    ngModel: '<'
  },
  controller: class TimeRangeFilterController {

    $onChanges() {
      if (!this.ngModel) {
        this.ngModel = {};
      }
    }

    onDatesSelected(ngModel) {
      if (ngModel.toDate && ngModel.fromDate) {
        this.ngModelController.$setViewValue(ngModel);
      }
    }
  }
};
