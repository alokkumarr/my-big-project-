import map from 'lodash/map';
import DataSource from 'devextreme/data/data_source';

import template from './report-grid-display.component.html';

import {NUMBER_TYPES} from '../../../consts.js';

const COLUMN_WIDTH = 175;

export const ReportGridDisplayComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<',
    source: '&'
  },
  controller: class ReportGridDisplayController {
    constructor(dxDataGridService, FilterService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._FilterService = FilterService;
      this._gridInstance = null;
    }

    $onInit() {
      const columns = this._getDxColumns(this.columns);

      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columns,
        remoteOperations: {
          paging: true
        },
        dataSource: this._createCustomStore(),
        scrolling: {
          mode: 'standard'
        },
        paging: {
          pageSize: 10
        },
        pager: {
          showNavigationButtons: true,
          showPageSizeSelector: true
        },
        onInitialized: this.onGridInitialized.bind(this)
      });
    }

    _createCustomStore() {
      const store = new DataSource({
        load: options => {
          return this.source({options})
            .then(({data, count}) => ({data, totalCount: count}));
        }
      });
      return store;
    }

    _getDxColumns(columns) {
      return map(columns, column => {
        const field = {
          alignment: 'left',
          caption: column.aliasName || column.displayName,
          dataField: column.columnName || column.name,
          visibleIndex: column.visibleIndex,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          width: COLUMN_WIDTH
        };
        if (NUMBER_TYPES.includes(column.type)) {
          field.format = {
            type: 'decimal',
            precision: 2
          };
        }
        return field;
      });
    }

    $onChanges() {
      if (this._gridInstance) {
        const columns = this._getDxColumns(this.columns);
        this._gridInstance.option('columns', columns);
        // this._gridInstance.refresh();
      }
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }
  }
};
