import forEach from 'lodash/forEach';
import clone from 'lodash/clone';

import template from './analyze-report-sort.component.html';
import style from './analyze-report-sort.component.scss';

export const AnalyzeReportSortComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeReportSortController {
    constructor($mdDialog) {
      'ngInject';

      this._$mdDialog = $mdDialog;

      this.sorts = [];
    }

    $onInit() {
      forEach(this.model.sorts, sort => {
        this.sorts.push(clone(sort));
      });
    }

    addSort() {
      const sort = {
        order: 'asc'
      };

      this.sorts.push(sort);

      return sort;
    }

    filterSortOption(item) {
      switch (item.type) {
        case 'string':
        case 'int':
          return true;
        default:
          return false;
      }
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide();
      this.model.sorts.length = 0;

      forEach(this.sorts, sort => {
        this.model.sorts.push(sort);
      });
    }
  }
};
