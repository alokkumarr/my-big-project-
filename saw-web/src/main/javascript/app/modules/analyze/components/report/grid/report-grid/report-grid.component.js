import * as assign from 'lodash/assign';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as isUndefined from 'lodash/isUndefined';
import * as $ from 'jquery';

import * as template from './report-grid.component.html';
import style from './report-grid.component.scss';
import {NUMBER_TYPES} from '../../../../consts';

// const MIN_ROWS_TO_SHOW = 5;
const COLUMN_WIDTH = 175;

export const ReportGridComponent = {
  template,
  style: [style],
  bindings: {
    reportGridContainer: '<',
    reportGridNode: '<',
    source: '<',
    gridIdentifier: '@'
  },
  controller: class ReportGridController {
    constructor($mdDialog, dxDataGridService, $timeout) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._dxDataGridService = dxDataGridService;
      this._$timeout = $timeout;

      this.settings = {};
      this.columns = [];
      this.sorts = [];
    }

    $onInit() {
      this.reportGridNode.setGridComponent(this);

      this.settings = assign(this.settings, {
        gridConfig: this._dxDataGridService.mergeWithDefaultConfig({
          onInitialized: this.onGridInitialized.bind(this),
          onContextMenuPreparing: this.onContextMenuPreparing.bind(this),
          columns: this.prepareGridColumns(this.columns),
          dataSource: this.source || []
        })
      });
    }

    setListenersOnColumnHeaders() {
      /* eslint-disable angular/angularelement */
      // the devextreme data-grid, has no public events to lsiten for the reordering of columns
      // so the listeners have to be manually put in
      this._$timeout(() => {
        const cssSelector = `div.report-dx-grid[data-grid-identifier="${this.gridIdentifier}"] td[role="columnheader"]`;
        const columnHeaders = Array.from($(cssSelector));
        forEach(columnHeaders, header => {
          const $elem = $(header);
          $elem.on('dxdragend', () => {
            this.onColumnReorder();
          });
        });
      });
      /* eslint-enable angular/angularelement */
    }

    onColumnReorder() {
      const columns = this._gridInstance.getVisibleColumns();
      this.reportGridContainer.onColumnReorder(columns);
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }

    onContextMenuPreparing(e) {
      if (e.target === 'header') {
        e.items = [];
        console.log(e.column.dataType);
        if (e.column.dataType === 'number' || e.column.dataType === 'timestamp' || e.column.dataType === 'date') {
          e.items.push({
            text: 'Format Data',
            icon: 'grid-menu-item icon-edit',
            onItemClick: () => {
              this.formatColumn(e.column);
            }
          });
        }
        e.items.push({
          text: 'Rename',
          icon: 'grid-menu-item icon-edit',
          onItemClick: () => {
            this.renameColumn(e.column);
          }
        });

        e.items.push({
          text: `Group by ${e.column.caption}`,
          icon: 'grid-menu-item icon-group-by-column',
          onItemClick: () => {
            this.groupByColumn(e.column);
          }
        });

        e.items.push({
          text: `Hide ${e.column.caption}`,
          icon: 'grid-menu-item icon-eye-disabled',
          onItemClick: () => {
            this.hideColumn(e.column);
          }
        });

        if (NUMBER_TYPES.includes(e.column.dataType)) {
          e.items.push({
            beginGroup: true,
            text: `Show Sum`,
            icon: 'grid-menu-item icon-Sum m-small',
            selected: this.isColumnAggregatedBy(e.column, 'sum'),
            onItemClick: () => {
              this.aggregateColumn(e.column, 'sum');
            }
          });

          e.items.push({
            text: `Show Average`,
            icon: 'grid-menu-item icon-AVG m-small',
            selected: this.isColumnAggregatedBy(e.column, 'avg'),
            onItemClick: () => {
              this.aggregateColumn(e.column, 'avg');
            }
          });

          e.items.push({
            text: `Show Mininum`,
            icon: 'grid-menu-item icon-MIN m-small',
            selected: this.isColumnAggregatedBy(e.column, 'min'),
            onItemClick: () => {
              this.aggregateColumn(e.column, 'min');
            }
          });

          e.items.push({
            text: `Show Maximum`,
            icon: 'grid-menu-item icon-MAX m-small',
            selected: this.isColumnAggregatedBy(e.column, 'max'),
            onItemClick: () => {
              this.aggregateColumn(e.column, 'max');
            }
          });
        }
      }
    }

    updateSettings(settings) {
      this.settings = assign(this.settings, settings);
    }

    updateColumns(columns) {
      this.columns = columns;

      if (this._gridInstance) {
        this._gridInstance.option('columns', this.prepareGridColumns(this.columns));
      }
    }

    prepareGridColumns(columns) {
      return map(columns, column => {
        const field = {
          caption: column.getDisplayName(),
          dataField: column.name,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          visibleIndex: column.visibleIndex,
          allowSorting: false,
          alignment: 'left',
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

    updateSorts(sorts) {
      this.sorts = sorts;

      if (this._gridInstance) {
        const columns = this._gridInstance.option('columns');

        let index = 0;

        forEach(sorts, sort => {
          const column = this.getColumnByName(sort.column);

          if (column) {
            column.sortIndex = index++;
            column.sortOrder = sort.direction;
          }
        });

        this._gridInstance.option('columns', columns);
      }
    }

    onSourceUpdate() {
      if (this._gridInstance) {
        this._gridInstance.option('dataSource', this.source);
      }
    }

    refreshGrid() {
      if (this._gridInstance) {
        this._gridInstance.refresh();
      }
      this.setListenersOnColumnHeaders();
    }

    $onDestroy() {
      this.reportGridNode.setGridComponent(null);
    }

    getColumnByName(columnName) {
      const columns = this._gridInstance.option('columns');

      return find(columns, column => {
        return column.dataField === columnName;
      });
    }

    formatColumn(gridColumn) {
      this.openFormatModal(gridColumn).then(newFormat => {
        if (this._gridInstance) {
          const columns = this._gridInstance.option('columns');
          const column = this.getColumnByName(newFormat.column);
          let typeValue = '';
          if (column) {
            if (newFormat.type === 'date') {
              console.log('inside if')
              column.format = newFormat.dateFormat;
            } else {
              if (newFormat.CommaSeparator) {
                typeValue = 'fixedpoint';
              } else {
                typeValue = 'decimal';
              }
              column.format = {
                type: typeValue,
                precision: newFormat.NumberDecimal
              };
            }
          }
          this._gridInstance.option('columns', columns);
        }
      });

    }

    renameColumn(gridColumn) {
      this.openRenameModal()
        .then(newName => {
          this.reportGridContainer.renameColumn(gridColumn.dataField, newName);
        });
    }

    groupByColumn(gridColumn) {
      this.reportGridContainer.groupByColumn(gridColumn.dataField);
    }

    hideColumn(gridColumn) {
      this.reportGridContainer.hideColumn(gridColumn.dataField);
    }

    aggregateColumn(gridColumn, aggregatorType) {
      const totalItems = this._gridInstance.option('summary.totalItems') || [];

      if (!this.isColumnAggregatedBy(gridColumn, aggregatorType)) {
        // remove previous aggregation on column
        remove(totalItems, item => {
          return item.column === gridColumn.dataField;
        });

        // add new aggregation to column
        totalItems.push({
          column: gridColumn.dataField,
          summaryType: aggregatorType
        });
      } else {
        // toggle specific aggregation on column
        remove(totalItems, item => {
          return item.column === gridColumn.dataField && item.summaryType === aggregatorType;
        });
      }

      this._gridInstance.option('summary.totalItems', totalItems);
    }

    isColumnAggregatedBy(gridColumn, aggregatorType) {
      const totalItems = this._gridInstance.option('summary.totalItems');

      const aggregatedItem = find(totalItems, item => {
        return item.column === gridColumn.dataField && (isUndefined(aggregatorType) || item.summaryType === aggregatorType);
      });

      return Boolean(aggregatedItem);
    }

    openRenameModal() {
      return this._$mdDialog
        .show({
          template: '<report-rename-dialog></report-rename-dialog>',
          fullscreen: false,
          multiple: true,
          clickOutsideToClose: true
        });
    }

    openFormatModal(model) {
      return this._$mdDialog
        .show({
          controller: scope => {
            scope.model = model;
          },
          template: '<report-format-dialog model-data=model> </report-format-dialog>',
          fullscreen: false,
          multiple: true,
          clickOutsideToClose: true
        });
    }
  }
};
