import forEach from 'lodash/forEach';
import assign from 'lodash/assign';
import values from 'lodash/values';
import mapValues from 'lodash/mapValues';

import template from './pivot-grid.component.html';
import style from './pivot-grid.component.scss';

export const PivotGridComponent = {
  template,
  styles: [style],
  bindings: {
    updater: '<',
    mode: '@',
    onContentReady: '&'
  },
  controller: class PivotGridController {
    constructor($timeout, $translate) {
      'ngInject';
      this._$translate = $translate;
      this._$timeout = $timeout;
      this.warnings = {
        'Drop Data Fields Here': 'SELECT_DATA_FIELDS',
        'Drop Column Fields Here': 'SELECT_COLUMN_FIELDS',
        'Drop Row Fields Here': 'SELECT_ROW_FIELDS'
      };

      $translate(values(this.warnings)).then(translations => {
        this.warnings = mapValues(this.warnings, warning => translations[warning]);
      });
    }

    $onInit() {
      this.pivotGridOptions = assign({
        onInitialized: e => {
          this._gridInstance = e.component;
        },
        onContentReady: () => {
          const fields = this._gridInstance.getDataSource().fields();
          this.onContentReady({fields});
        }
      }, this.getDefaultOptions());

      this._$timeout(() => {
        // have to repaint the grid because of the animation of the modal
        // if it's not repainted it appears smaller
        this._gridInstance.repaint();
        this.subscription = this.updater.subscribe(updates => this.update(updates));
        this.replaceWarningLables();
      }, 500);
    }

    $onDestroy() {
      this.subscription.unsubscribe();
    }

    replaceWarningLables() {
      this._$timeout(() => {
        /* eslint-disable angular/document-service */
        const headers = document.querySelectorAll('.dx-empty-area-text');
        forEach(headers, header => {
          if (this.warnings[header.innerHTML]) {
            header.innerHTML = this.warnings[header.innerHTML];
          }
        });
        /* eslint-enable angular/document-service */
      });
    }

    update(updates) {
      /* eslint-disable no-unused-expressions */
      updates.dataSource && this.updateDataSource(updates.dataSource);
      updates.sorts && this.updateSorts(updates.sorts);
      updates.export && this.exportToExcel();
      /* eslint-disable no-unused-expressions */

      this.replaceWarningLables();
    }

    exportToExcel() {
      this._gridInstance.exportToExcel();
    }

    updateDataSource(dataSource) {
      this._gridInstance.option('dataSource', dataSource);
    }

    updateSorts(sorts) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      // reset other sorts
      forEach(pivotGridDataSource.fields(), field => {
        if (field.sortOrder) {
          pivotGridDataSource.field(field.dataField, {
            sortOrder: null
          });
        }
      });

      forEach(sorts, sort => {
        pivotGridDataSource.field(sort.field.dataField, {
          sortOrder: sort.order
        });
      });
      pivotGridDataSource.load();
    }

    getDefaultOptions() {
      return {
        rowHeaderLayout: 'tree',
        allowSortingBySummary: false,
        showBorders: true,
        allowSorting: false,
        allowFiltering: false,
        allowExpandAll: false,
        fieldChooser: {
          enabled: false
        },
        fieldPanel: {
          visible: true,
          showColumnFields: true, // hides the column field area
          showRowFields: true, // hides the row field area
          showDataFields: true, // hides the data field area
          showFilterFields: false, // hides the filter field area
          allowFieldDragging: this.mode === 'designer'
        },
        export: {
          enabled: false,
          fileName: 'export'
        },
        dataSource: {
          store: [],
          fields: []
        }
      };
    }
  }
};
