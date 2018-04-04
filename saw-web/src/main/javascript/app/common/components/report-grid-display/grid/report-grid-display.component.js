import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as isUndefined from 'lodash/isUndefined';
import * as isEmpty from 'lodash/isEmpty';
import * as keys from 'lodash/keys';
import * as split from 'lodash/split';
import * as filter from 'lodash/filter';
import * as fpGet from 'lodash/fp/get';
import * as reduce from 'lodash/reduce';

import {FieldModel} from '../../jsPlumb/models/fieldModel';
import DataSource from 'devextreme/data/data_source';

import * as template from './report-grid-display.component.html';

import {NUMBER_TYPES, DATE_TYPES, FLOAT_TYPES} from '../../../consts.js';
import {getFormatter, DEFAULT_PRECISION} from '../../../utils/numberFormatter';

const DEFAULT_PAGE_SIZE = 10;

export const ReportGridDisplayComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<',
    showChecked: '<', // only show the checked columns. Discards extra columns present in data
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
      const gridSelector = '.report-dx-grid.report-dx-grid-display';
      this.gridConfig = this._dxDataGridService.mergeWithDefaultConfig({
        columnChooser: {
          enabled: true,
          mode: 'select'
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
        onContentReady: this.onContentReady.bind(this)
      });
    }

    $onDestroy() {
      if (this._gridInstance) {
        this._gridInstance.hideColumnChooser();
      }
    }

    _createCustomStore() {
      const store = new DataSource({
        load: options => {
          return this.source({options})
            .then(({data, count}) => {
              return {data, totalCount: count};
            });
        }
      });
      return store;
    }

    updateColumns(columns, data) {
      if (this._gridInstance) {
        if (!columns) {
          this._gridInstance.option('columns', null);
          return;
        }
        const cols = this._getDxColumns(columns, data);
        this._gridInstance.option('columns', cols);
        // this._gridInstance.refresh();
      }
    }

    fillColumns(fields, data = []) {
      let table = null;
      const columnNames = keys(fpGet('[0]', data) || {});

      const columns = reduce(fields, (col, field) => {
        table = table || field.table;
        const index = columnNames.indexOf(field.columnName || field.name);
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

    /**
     * Removes .keyword suffix from column names if it exists. This is required
     * because the grid data coming from backend doesn't have that suffix in
     * its datafields.
     *
     * Returns a new clone of columns array with each column cloned as well.
     *
     * @param {any} columns
     * @returns
     */
    checkColumnName(columns) {
      return map(columns, field => {
        const col = clone(field);
        col.name = this.getColumnName(col.name);
        col.columnName = this.getColumnName(col.columnName);

        if (field.meta) {
          const meta = clone(field.meta);
          col.meta = meta;

          col.meta.name = this.getColumnName(col.meta.name);
          col.meta.columnName = this.getColumnName(col.meta.columnName);
        }
        return col;
      });
    }

    getColumnName(columnName) {
      // take out the .keyword form the columnName
      // if there is one
      if (!isUndefined(columnName)) {
        const split = columnName.split('.');
        if (split[1]) {
          return split[0];
        }
        return columnName;
      }
    }

    getDataField(column) {
      const dataField = column.columnName || column.name;
      // trim the .keyword suffix from the column name if it is there
      return split(dataField, '.')[0];
    }

    _getDxColumns(columns = [], data = []) {
      let allColumns = [];
      if (isEmpty(data) || this.showChecked) {
        allColumns = filter(columns, column => column.checked);
      } else {
        allColumns = this.fillColumns(columns, data);
      }

      allColumns = this.checkColumnName(allColumns);

      return map(allColumns, column => {
        if (column.type === 'timestamp' || column.type === 'string-date') {
          column.type = 'date';
        }
        const isNumberType = NUMBER_TYPES.includes(column.type);

        const field = {
          caption: column.aliasName || column.alias || column.displayName || column.name,
          dataField: this.getDataField(column),
          format: isNumberType ? {
            formatter: getFormatter(column.format || (
              FLOAT_TYPES.includes(column.type) ? {precision: DEFAULT_PRECISION} : {precision: 0}
            ))
          } : column.format,
          visibleIndex: column.visibleIndex,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type
        };

        if (DATE_TYPES.includes(column.type) && isUndefined(column.format)) {
          field.format = 'yyyy-MM-dd';
        }

        return field;
      });
    }

    $onChanges() {
      this._$timeout(() => {
        this.updateColumns(this.columns, this.data);
      });
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }

    onContentReady(e) {
      // close the columnCHoser, when the user clicks outside of it
      const columnChooserView = e.component.getView('columnChooserView');
      if (!columnChooserView._popupContainer) {
        columnChooserView._initializePopupContainer();
        columnChooserView.render();
        const onBodyClick = e => {
          const content = columnChooserView._popupContainer._$content[0];
          const target = e.target;
          const clickOutside = !content.contains(target);
          if (clickOutside) {
            this._$timeout(() => {
              this._gridInstance.hideColumnChooser();
            });
          }
        };
        columnChooserView._popupContainer.on('showing', () => {
          /* eslint-disable */
          document.body.addEventListener('click', onBodyClick);
        });
        columnChooserView._popupContainer.on('hiding', () => {
          document.body.removeEventListener('click', onBodyClick);
          /* eslint-enable */
        });
      }
    }
  }
};
