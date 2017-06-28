import forEach from 'lodash/forEach';
import assign from 'lodash/assign';
import values from 'lodash/values';
import mapValues from 'lodash/mapValues';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import fpMap from 'lodash/fp/map';
import fpToPairs from 'lodash/fp/toPairs';

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
    constructor($timeout, $translate, FilterService, $compile, $scope) {
      'ngInject';
      this._$translate = $translate;
      this._$timeout = $timeout;
      this._$compile = $compile;
      this._$scope = $scope;
      this._FilterService = FilterService;
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
      updates.field && this.updateField(updates.field);
      updates.filters && this.updateFilters(updates.filters);
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

    updateField(field) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      pivotGridDataSource.field(field.dataField, field.modifierObj);
      pivotGridDataSource.load();
    }

    updateFilters(filters) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      forEach(filters, filter => {
        if (this._FilterService.isFilterModelNonEmpty(filter.model)) {
          const filterValues = fpPipe(
            fpToPairs,
            fpFilter(pair => pair[1]),
            fpMap(pair => pair[0])
          )(filter.model);
          pivotGridDataSource.field(filter.name, {
            filterType: 'include',
            filterValues
          });
        } else {
          pivotGridDataSource.field(filter.name, {
            filterType: null,
            filterValues: null
          });
        }
      });
      pivotGridDataSource.load();
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
          allowFieldDragging: true
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
