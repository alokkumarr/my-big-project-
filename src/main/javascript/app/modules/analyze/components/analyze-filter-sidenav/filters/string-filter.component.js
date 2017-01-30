import template from './string-filter.component.html';

export const StringFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class StringFilterController {

    $onInit() {
      if (!this.filter.model) {
        this.filter.model = {};
      }
    }

    onToggle(item) {
      this.filter.model[item] = !this.filter.model[item];
    }
  }
}
