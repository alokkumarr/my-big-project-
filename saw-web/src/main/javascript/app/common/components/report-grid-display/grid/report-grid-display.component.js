import * as map from 'lodash/map';
import DataSource from 'devextreme/data/data_source';

import * as template from './report-grid-display.component.html';

import {NUMBER_TYPES} from '../../../consts.js';

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
    constructor(dxDataGridService, FilterService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._FilterService = FilterService;
      this._gridInstance = null;
      this.pageSize = DEFAULT_PAGE_SIZE;
      this.$window = window;
    }

    $onInit() {
      const columns = this._getDxColumns(this.columns);

      const gridSelector = '.report-dx-grid.report-dx-grid-display';

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
            .then(({data, count}) => ({data, totalCount: count}));
        }
      });
      return store;
    }

    _getDxColumns(columns) {
      return map(columns, column => {
        if (column.type === 'date') {
          column.type = 'string-date';
        }
        const field = {
          alignment: 'left',
          caption: column.alias || column.displayName,
          format: column.format,
          dataField: column.columnName || column.name,
          visibleIndex: column.visibleIndex,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          width: COLUMN_WIDTH
        };
        if (angular.isUndefined(NUMBER_TYPES.includes(column.type) && column.format)) {
          field.format = {
            type: 'decimal',
            precision: 2
          };
        }
        if (angular.isDefined(NUMBER_TYPES.includes(column.type) && column.format)) {
          if (column.format.currency) {
            field.customizeText = (data => {
              return data.valueText + ' ' + column.format.currencySymbol;
            });
          }
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
