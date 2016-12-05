import template from './radio-filter.component.html';

export const RadioFilterComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    filter: '<',
    ngModel: '<'
  }
};
