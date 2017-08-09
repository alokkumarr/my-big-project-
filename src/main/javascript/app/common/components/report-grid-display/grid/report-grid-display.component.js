import map from 'lodash/map';
import take from 'lodash/take';

import template from './report-grid-display.component.html';

import {NUMBER_TYPES} from '../../../consts.js';

const COLUMN_WIDTH = 175;

export const ReportGridDisplayComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<'
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

      this.data = take(this.data, 30);

      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columns,
        dataSource: this.data,
        onInitialized: this.onGridInitialized.bind(this)
      });
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
        this._gridInstance.option('dataSource', this.data);
        this._gridInstance.option('columns', columns);
        this._gridInstance.refresh();
      }
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }
  }
};
