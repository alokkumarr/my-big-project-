import template from './date-filter.component.html';

export const DateFilterComponent = {
  template,
  bindings: {
    model: '<',
    onChange: '&'
  },
  controller: class DateFilterController {
    constructor($filter) {
      this._$filter = $filter;
    }
    $onInit() {
      /* this.model has string values. Change them to actual date objects
      to be able to work with md-datepicker first.
      */
      this.model = this.model || {};
      this.tempModel = {
        gte: this.model.gte ? new Date(this.model.gte) : '',
        lte: this.model.lte ? new Date(this.model.lte) : ''
      };
    }

    onModelChange() {
      this.updatedDate = this.model || {
        gte: '',
        lte: ''
      };
      this.dateFormat = 'yyyy-MM-dd HH:ss:mm';
      this.updatedDate.gte = this._$filter('date')(this.tempModel.gte, this.dateFormat);
      this.updatedDate.lte = this._$filter('date')(this.tempModel.lte, this.dateFormat);
      this.onChange({model: this.updatedDate});
    }
  }
};
