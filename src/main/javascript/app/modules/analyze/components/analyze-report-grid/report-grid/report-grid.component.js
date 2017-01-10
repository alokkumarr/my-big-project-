import map from 'lodash/fp/map';
import pipe from 'lodash/fp/pipe';
import filter from 'lodash/fp/filter';
import concat from 'lodash/concat';
import head from 'lodash/head';
import has from 'lodash/has';
import findIndex from 'lodash/findIndex';
import defaults from 'lodash/defaults';

import template from './report-grid.component.html';
import style from './report-grid.component.scss';
import renameTemplate from '../rename-dialog/rename-dialog.tmpl.html';
import {RenameDialogController} from '../rename-dialog/rename-dialog.controller';

const MIN_ROWS_TO_SHOW = 5;
const COLUMN_WIDTH = 175;

export const ReportGridComponent = {
  template,
  style: [style],
  require: {
    reporGridContainerCtrl: '^reportGridContainer'
  },
  bindings: {
    data: '<',
    columns: '<'
  },
  controller: class ReportGridController {
    constructor(uiGridConstants, $mdDialog) {
      'ngInject';
      this._uiGridConstants = uiGridConstants;
      this._$mdDialog = $mdDialog;
    }

    $onInit() {
      this.gridStyle = {
        width: `${this.getVisibleCulomsFilter()(this.columns).length * COLUMN_WIDTH}px`
      };
      this.config = {
        showColumnFooter: false,
        data: this.data,
        columnDefs: this.getCulomnDefs(this.columns),
        minRowsToShow: MIN_ROWS_TO_SHOW,
        enableHorizontalScrollbar: this._uiGridConstants.scrollbars.NEVER,
        onRegisterApi: (gridApi) => {
            this.gridApi = gridApi;
        }
      };
    }

    getCulomnDefs(columns) {
      return pipe(
        this.getVisibleCulomsFilter(),
        map(column => {
          const displayName = column.alias || column.display;
          const columnName = column.name;
          return {
            width: COLUMN_WIDTH,
            name: columnName,
            visible: true,
            displayName,
            menuItems: this.getMenuItems(column.type, displayName, columnName)
          };
        })
      )(columns);
    }

    getVisibleCulomsFilter() {
      return filter(column => has(head(this.data), column.name));
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
        icon: 'icon-edit',
        action: () => this.renameColumn(columnName)
      }, {
        title: `Hide ${displayName}`,
        icon: 'icon-eye-disabled',
        action: () => this.hideColumn(columnName)
      }];

      if (type === 'int' | type === 'double') {
        menuItems = concat(menuItems, [{
          title: 'Show Sum',
          icon: 'icon-Sum',
          action: () => this.showSum(columnName)
        }, {
          title: `Show Average`,
          icon: 'icon-AVG',
          action: () => this.showAvg(columnName)
        }, {
          title: `Show Min`,
          icon: 'icon-MIN',
          action: () => this.showMin(columnName)
        }, {
          title: `Show Max`,
          icon: 'icon-MAX',
          action: () => this.showMax(columnName)
          }]);
      }

      return menuItems;
    }

    renameColumn(columnName) {
      this.openRenameModal()
        .then(newName => {
          // rename on grid, and grid context menu
          const column = find(this.columns, column => column.name === columnName);

          this.modifyColumnDef(columnName, {
            displayName: newName,
            menuItems: this.getMenuItems(column.type, newName, columnName)
          });
          // rename in data
          this.reporGridContainerCtrl.rename(columnName, newName);
      });
    }

    hideColumn(columnName) {
      this.modifyColumnDef(columnName, {
        visible: false
      });
    }

    showMin(columnName) {
      this.showAggregatorOnColumn(columnName, 'min');
    }

    showMax(columnName) {
      this.showAggregatorOnColumn(columnName, 'max');
    }

    showAvg(columnName) {
      this.showAggregatorOnColumn(columnName, 'avg');
    }

    showSum(columnName) {
      this.showAggregatorOnColumn(columnName, 'sum');
    }

    showAggregatorOnColumn(columnName, aggregatorType) {
      this.showColumnFooterIfHidden();
      this.modifyColumnDef(columnName, {
        aggregationType: this._uiGridConstants.aggregationTypes[aggregatorType]
      });
    }

    showColumnFooterIfHidden() {
      if (!this.config.showColumnFooter) {
        this.config.showColumnFooter = true;
        this.gridApi.core.notifyDataChange(this._uiGridConstants.dataChange.OPTIONS);
      }
    };

    modifyColumnDef(columnName, modifierObj) {
      const index = findIndex(this.config.columnDefs, columnDef => columnDef.name === columnName);
      this.config.columnDefs[index] = defaults(modifierObj, this.config.columnDefs[index]);
    }

    openRenameModal() {
      return this._$mdDialog
        .show({
          controller: RenameDialogController,
          template: renameTemplate,
          fullscreen: false,
          skipHide: true,
          clickOutsideToClose:true
        });
    }
  }
};
