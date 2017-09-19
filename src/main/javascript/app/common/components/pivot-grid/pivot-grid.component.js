import * as forEach from 'lodash/forEach';
import * as assign from 'lodash/assign';
import * as values from 'lodash/values';
import * as mapValues from 'lodash/mapValues';

import * as template from './pivot-grid.component.html';
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
    constructor($timeout, $translate, PivotService) {
      'ngInject';
      this._$translate = $translate;
      this._$timeout = $timeout;
      this._PivotService = PivotService;
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
      const parsedFields = this._PivotService.trimSuffixFromPivotFields(dataSource.fields());
      dataSource.fields(parsedFields);
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
        const dataField = sort.field.type === 'string' ?
          sort.field.dataField.split('.')[0] :
          sort.field.dataField;
        pivotGridDataSource.field(dataField, {
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
