import template from './date-filter.component.html';

export const DateFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class DateFilterController {}
};
