import map from 'lodash/map';
import take from 'lodash/take';

import template from './report-grid-display.component.html';

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
        return {
          caption: column.aliasName || column.displayName,
          dataField: column.columnName,
          dataType: column.type,
          width: COLUMN_WIDTH
        };
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
