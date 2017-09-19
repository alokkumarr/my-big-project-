import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';

import * as template from './analyze-sort-dialog.component.html';
import style from './analyze-sort-dialog.component.scss';
import {DATE_TYPES, NUMBER_TYPES} from '../../consts';

export const AnalyzeSortDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeSortDialogController {
    constructor($mdDialog) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this.NUMBER_TYPES = NUMBER_TYPES;
      this.DATE_TYPES = DATE_TYPES;
    }

    $onInit() {
      // the field objects have to be reset in the sorts array
      // because the object references are not the same as those in the fields array
      // and md-select doesn't work, if the analysis with existing sorts is being edited.
      forEach(this.model.sorts, (sort, key) => {
        const target = find(this.model.fields, ({dataField}) => dataField === sort.field.dataField);
        if (target) {
          this.model.sorts[key].field = target;
        }
      });
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
      if (item.type === 'string' ||
          NUMBER_TYPES.includes(item.type) ||
          DATE_TYPES.includes(item.type)) {
        return sort.field === item || !this.isColumnSorted(item);
      }
      return false;
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
