import forEach from 'lodash/forEach';

import template from './pivot-grid.component.html';

export const PivotGridComponent = {
  template,
  bindings: {
    options: '<'
  },
  controller: class PivotGridController {
    constructor($timeout, $element) {
      'ngInject';
      this._$element = $element;
      this._$timeout = $timeout;
      this.warnings = {
        'Drop Data Fields Here': 'SELECT_DATA_FIELDS',
        'Drop Column Fields Here': 'SELECT_COLUMN_FIELDS',
        'Drop Row Fields Here': 'SELECT_ROW_FIELDS'
      };
    }

    refresh() {
      /* eslint-disable angular/document-service */
      const headers = document.querySelectorAll('.dx-empty-area-text');
      forEach(headers, header => {
        header.innerHTML = this.warnings[header.innerHTML];
      });
    }
  }
};
