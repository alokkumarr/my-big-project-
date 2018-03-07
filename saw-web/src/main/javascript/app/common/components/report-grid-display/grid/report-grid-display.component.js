import * as map from 'lodash/map';
import * as isUndefined from 'lodash/isUndefined';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as keys from 'lodash/keys';
import * as split from 'lodash/split';
import * as filter from 'lodash/filter';
import * as fpGet from 'lodash/fp/get';
import * as reduce from 'lodash/reduce';

import {FieldModel} from '../../jsPlumb/models/fieldModel';
import DataSource from 'devextreme/data/data_source';

import * as template from './report-grid-display.component.html';

import {NUMBER_TYPES, DATE_TYPES} from '../../../consts.js';

const COLUMN_WIDTH = 175;
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
        customizeColumns: columns => {
          forEach(columns, col => {
            col.alignment = 'left';
            col.width = COLUMN_WIDTH;
          });
        },
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
        onContentReady: this.onContentReady.bind(this),
        height: 'auto',
        width: 'auto'
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
        const cols = this._getDxColumns(columns, data);
        forEach(cols, column => {
          if (column.dataType === 'date') {
            column.dataType = 'string-date';
          }
        });
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

    checkColumndatatype(columnList, columnName) {
      let datatype = '';
      forEach(columnList, column => {
        if (!isEmpty(column) && column.columnName === columnName) {
          datatype = column.type;
        }
      });
      return datatype;
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
      return map(allColumns, column => {
        if (column.type === 'timestamp' || column.type === 'string-date') {
          column.type = 'date';
        }
        const field = {
          alignment: 'left',
          caption: column.aliasName || column.alias || column.displayName || column.name,
          format: column.format,
          dataField: this.getDataField(column),
          visibleIndex: column.visibleIndex,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          width: COLUMN_WIDTH
        };

        if (DATE_TYPES.includes(column.type) && isUndefined(column.format)) {
          field.format = 'yyyy-MM-dd';
        }

        if (NUMBER_TYPES.includes(column.type) && isUndefined(column.format)) {
          field.format = {
            type: 'fixedPoint',
            comma: false,
            precision: 2
          };
          field.customizeText = (data => {
            const stringList = data.valueText.split(',');
            let finalString = '';
            forEach(stringList, value => {
              finalString = finalString.concat(value);
            });
            return finalString;
          });
        }
        if (NUMBER_TYPES.includes(column.type) && !isUndefined(column.format)) {
          if (!isUndefined(column.format.currency)) {
            field.customizeText = (data => {
              if (!column.format.comma) {
                const stringList = data.valueText.split(',');
                let finalString = '';
                forEach(stringList, value => {
                  finalString = finalString.concat(value);
                });
                data.valueText = finalString;
              }
              if (!isUndefined(column.format.currencySymbol) && !isEmpty(data.valueText)) {
                return column.format.currencySymbol + ' ' + data.valueText;
              }
              return data.valueText;
            });
          } else {
            field.customizeText = (data => {
              if (!column.format.comma) {
                const stringList = data.valueText.split(',');
                let finalString = '';
                forEach(stringList, value => {
                  finalString = finalString.concat(value);
                });
                data.valueText = finalString;
              }
              return data.valueText;
            });
          }
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
