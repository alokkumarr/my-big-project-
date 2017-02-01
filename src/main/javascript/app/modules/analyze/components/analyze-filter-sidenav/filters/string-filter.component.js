import template from './string-filter.component.html';

export const StringFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class StringFilterController {

    onToggle(item) {
      if (!this.filter.model) {
        this.filter.model = {};
      }
      this.filter.model[item] = !this.filter.model[item];
    }
  }
};
