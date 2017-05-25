import find from 'lodash/find';

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
    }

    addSort() {
      const sort = {
        order: 'asc'
      };

      this.model.sorts.push(sort);

      return sort;
    }

    deleteSort(sort, id) {
      this.model.sorts.splice(id, 1);
    }

    canAddSort() {
      return this.model.sorts.length < this.model.fields.length;
    }

    filterSortOption(sort, item) {
      switch (item.type) {
        case 'string':
        case 'int':
        case 'double':
        case 'long':
          return sort.field === item || !this.isColumnSorted(item);
        default:
          return false;
      }
    }

    isColumnSorted(column) {
      const sort = find(this.model.sorts, sort => {
        return sort.field === column;
      });

      return Boolean(sort);
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    apply() {
      this._$mdDialog.hide(this.model.sorts);
    }
  }
};
