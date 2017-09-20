import * as template from './date-filter.component.html';

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
      this.tempModel = this.model || {
        gte: '',
        lte: ''
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
