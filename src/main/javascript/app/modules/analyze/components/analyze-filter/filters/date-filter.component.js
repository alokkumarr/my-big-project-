import template from './date-filter.component.html';

export const DateFilterComponent = {
  template,
  bindings: {
    model: '<'
  },
  controller: class DateFilterController {
    $onInit() {
      this.tempModel = this.model || {
        to: '',
        from: ''
      };
    }

    onModelChange() {
      this.onChange({model: this.tempModel});
    }

  }
};
