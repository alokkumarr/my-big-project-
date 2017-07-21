import template from './date-filter.component.html';

export const DateFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class DateFilterController {
    $onInit() {
      this.tempModel = this.model || {
        gte: '',
        lte: ''
      };
    }

    onModelChange() {
      this.onChange({model: this.tempModel});
    }

  }
};
