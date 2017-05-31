import template from './string-filter.component.html';

export const StringFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class StringFilterController {

    $onInit() {
      this.keywords = this.model || '';
    }
  }
};
