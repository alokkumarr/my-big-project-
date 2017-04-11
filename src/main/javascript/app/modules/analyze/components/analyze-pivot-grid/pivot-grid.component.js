import forEach from 'lodash/forEach';
import assign from 'lodash/assign';
import values from 'lodash/values';
import mapValues from 'lodash/mapValues';
import keys from 'lodash/keys';
import compact from 'lodash/compact';

import template from './pivot-grid.component.html';

export const PivotGridComponent = {
  template,
  bindings: {
    updater: '<'
  },
  controller: class PivotGridController {
    constructor($timeout, $translate, FilterService) {
      'ngInject';
      this._$translate = $translate;
      this._$timeout = $timeout;
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
        }
      }, this.getDefaultOptions());

      this._$timeout(() => {
        // have to repaint the grid because of the animation of the modal
        // if it's not repainted it appears smaller
        this._gridInstance.repaint();
        this.updater.subscribe(updates => this.update(updates));
        this.replaceWarningLables();
      }, 500);
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
      /* eslint-disable no-unused-expressions */

      this.replaceWarningLables();
    }

    updateDataSource(dataSource) {
      this._gridInstance.option('dataSource', dataSource);
    }

    updateField(field) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      pivotGridDataSource.field(field.dataField, field.modifierObj);
      pivotGridDataSource.load();
      console.log('fields: ', pivotGridDataSource.fields());
    }

    updateFilters(filters) {
      const pivotGridDataSource = this._gridInstance.getDataSource();

      forEach(filters, filter => {
        if (this._FilterService.isFilterModelNonEmpty(filter.model)) {
          const filterValues = compact(keys(filter.model));
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
      forEach(sorts, sort => {
        pivotGridDataSource.field(sort.field.dataField, {
          sortOrder: sort.order
        });
      });
      pivotGridDataSource.load();
    }

    getDefaultOptions() {
      return {
        allowSortingBySummary: false,
        allowSorting: false,
        allowFiltering: false,
        allowExpandAll: true,
        fieldChooser: {
          enabled: false
        },
        fieldPanel: {
          visible: true,
          showColumnFields: true, // hides the column field area
          showRowFields: true, // hides the row field area
          showDataFields: true, // hides the data field area
          showFilterFields: false, // hides the filter field area
          allowFieldDragging: false
        },
        export: {
          enabled: false,
          fileName: 'Sales'
        },
        dataSource: {
          store: [],
          fields: []
        }
      };
    }
  }
};
