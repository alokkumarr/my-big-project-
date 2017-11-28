import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as keys from 'lodash/keys';
import * as fpGet from 'lodash/fp/get';
import * as reduce from 'lodash/reduce';
import * as moment from 'moment';

import {FieldModel} from '../../jsPlumb/models/fieldModel';
import DataSource from 'devextreme/data/data_source';
import 'moment-timezone';

import * as template from './report-grid-display.component.html';

import {NUMBER_TYPES, DATE_TYPES, BACKEND_TIMEZONE} from '../../../consts.js';

const COLUMN_WIDTH = 175;
const DEFAULT_PAGE_SIZE = 10;

export const ReportGridDisplayComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<',
    source: '&'
  },
  controller: class ReportGridDisplayController {
    constructor(dxDataGridService, FilterService, $timeout) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._FilterService = FilterService;
      this._gridInstance = null;
      this._$timeout = $timeout;
      this.pageSize = DEFAULT_PAGE_SIZE;
      this.$window = window;
    }

    $onInit() {
      const columns = this._getDxColumns(this.columns, this.data);

      const gridSelector = '.report-dx-grid.report-dx-grid-display';
      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columns: isEmpty(columns) ? null : columns,
        customizeColumns: columns => {
          forEach(columns, col => {
            col.alignment = 'left';
            col.width = COLUMN_WIDTH;
          });
        },
        remoteOperations: {
          paging: true
        },
        dataSource: this._createCustomStore(),
        scrolling: {
          mode: 'standard'
        },
        paging: {
          pageSize: this.pageSize
        },
        pager: {
          showNavigationButtons: true,
          allowedPageSizes: [DEFAULT_PAGE_SIZE, 25, 50, 100],
          showPageSizeSelector: true
        },
        loadPanel: {
          position: {
            of: gridSelector,
            at: 'center',
            my: 'center'
          },
          onShowing: () => {
            if (this._gridInstance) {
              this.pageSize = this._gridInstance.pageSize();
            }
          }
        },
        bindingOptions: {
          'loadPanel.position.of': `$ctrl.pageSize > ${DEFAULT_PAGE_SIZE} ? window : "${gridSelector}"`
        },
        onInitialized: this.onGridInitialized.bind(this),
        height: 'auto',
        width: 'auto'
      });
    }

    _createCustomStore() {
      const store = new DataSource({
        load: options => {
          return this.source({options})
            .then(({data, count}) => {
              return {data: this.formatDates(data), totalCount: count};
            });
        }
      });
      return store;
    }

    formatDates(data) {
      if (isEmpty(data)) {
        return data;
      }
      const ks = keys(data[0] || {});
      const formats = [
        moment.ISO_8601,
        'YYYY-MM-DD hh:mm:ss',
        'YYYY-MM-DD',
        'MM/DD/YYYY  :)  HH*mm*ss'
      ];
      forEach(data, row => {
        forEach(ks, key => {
          const date = moment.tz(row[key], formats, true, BACKEND_TIMEZONE);
          if (date.isValid()) {
            row[key] = date.toDate();
          }
        });
      });
      return data;
    }

    fillColumns(fields, data = []) {
      let table = null;
      const columnNames = keys(fpGet('[0]', data) || {});

      const columns = reduce(fields, (col, field) => {
        table = table || field.table;
        const index = columnNames.indexOf(field.name);
        if (index >= 0) {
          col.splice(index, 1, field);
        }
        return col;
      }, columnNames);

      return map(columns, col => {
        if (angular.isString(col)) {
          const customColumn = new FieldModel(table, col);
          customColumn.checked = true;
          return customColumn;
        }
        col.checked = true;
        return col;
      });
    }

    _getDxColumns(columns = [], data = []) {
      const allColumns = this.fillColumns(columns, data);
      return map(allColumns, column => {
        if (column.type === 'string-date') {
          column.type = 'date';
        }
        const field = {
          alignment: 'left',
          caption: column.aliasName || column.displayName || column.name,
          dataField: column.columnName || column.name,
          visibleIndex: column.visibleIndex,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          width: COLUMN_WIDTH
        };
        if (DATE_TYPES.includes(column.type)) {
          field.format = {
            type: 'shortDate'
          };
        }
        if (NUMBER_TYPES.includes(column.type)) {
          field.format = {
            type: 'fixedPoint',
            precision: 2
          };
        }
        return field;
      });
    }

    $onChanges() {
      if (this._gridInstance) {
        const columns = this._getDxColumns(this.columns, this.data);
        forEach(columns, column => {
          if (column.dataType === 'date') {
            column.dataType = 'string';
          }
        });
        this._gridInstance.option('columns', columns);
        // this._gridInstance.refresh();
      }
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }
  }
};
