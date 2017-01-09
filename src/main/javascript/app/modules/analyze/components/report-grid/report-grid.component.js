import map from 'lodash/fp/map';
import pipe from 'lodash/fp/pipe';
import filter from 'lodash/fp/filter';
import concat from 'lodash/concat';
import head from 'lodash/head';
import has from 'lodash/has';
import template from './report-grid.component.html';
import style from './report-grid.component.scss';

const MIN_ROWS_TO_SHOW = 5;
const COLUMN_WIDTH = 175;

export const ReportGridComponent = {
  template,
  style: [style],
  require: {
    reporGridContainerCtrl: '^reportGridContainer'
  },
  bindings: {
    data: '<'
  },
  controller: class ReportGridController {
    constructor(uiGridConstants) {
      'ngInject';
      this._uiGridConstants = uiGridConstants;
      // get this data from the canvas model: checked fields
      /* eslint-disable */
      this.columns = [{
        name: 'CustomerName',
        display: 'Costumer name',
        alias: '',
        type: 'sring'
      }, {
        name: 'ShipperName',
        display: 'Shipper name',
        alias: '',
        type: 'sring'
      }, {
        name: 'WarehouseName',
        display: 'Warehouse name',
        alias: '',
        type: 'sring'
      }, {
        name: 'TotalPrice',
        display: 'Total Price',
        alias: '',
        type: 'int'
      }];
      /* eslint-enable */
    }

    $onInit() {
      this.gridStyle = {
        width: `${this.columns.length * COLUMN_WIDTH}px`
      };
      this.config = {
        data: this.data,
        columnDefs: this.getCulomnDefs(this.columns),
        minRowsToShow: MIN_ROWS_TO_SHOW,
        enableHorizontalScrollbar: this._uiGridConstants.scrollbars.NEVER
      };
    }

    resize() {
      this.$interval(() => {
        this.gridApi.core.handleWindowResize();
      }, 10, 100);
    }
    getCulomnDefs(columns) {
      return pipe(
        filter(column => has(head(this.data), column.name)),
        map(column => {
          const displayName = column.alias || column.display;
          const columnName = column.name;
          return {
            width: COLUMN_WIDTH,
            name: columnName,
            displayName,
            menuItems: this.getMenuItems(column.type, displayName, columnName)
          };
        })
      )(columns);
    }

    getMenuItems(type, displayName, columnName) {
      let menuItems = [{
        title: `Group By ${displayName}`,
        icon: 'icon-group-by-column',
        action: () => {
          this.reporGridContainerCtrl.groupData(columnName);

        }
      }, {
        title: 'Rename',
        icon: 'icon-edit'
      }, {
        title: `Hide ${displayName}`,
        icon: 'icon-eye-disabled'
      }];

      if (type === 'int' | type === 'double') {
        menuItems = concat(menuItems, [{
          title: 'Show Sum',
          icon: 'icon-Sum'
        }, {
          title: `Show Average`,
          icon: 'icon-AVG'
        }, {
          title: `Show Min`,
          icon: 'icon-MIN'
        }, {
          title: `Show Max`,
          icon: 'icon-MAX'
          }]);
      }

      return menuItems;
    }
  }
};
