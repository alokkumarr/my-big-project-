import * as assign from 'lodash/assign';
import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as isUndefined from 'lodash/isUndefined';
import * as $ from 'jquery';
import 'moment-timezone';

import * as template from './report-grid.component.html';
import style from './report-grid.component.scss';
import {NUMBER_TYPES, DATE_TYPES, FLOAT_TYPES} from '../../../../consts';
import {getFormatter} from '../../../../../../common/utils/numberFormatter';

// const MIN_ROWS_TO_SHOW = 5;
const COLUMN_WIDTH = 175;
const DEFAULT_PRECISION = 2;

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
    constructor($mdDialog, dxDataGridService, $timeout, AnalyzeDialogService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._dxDataGridService = dxDataGridService;
      this._$timeout = $timeout;
      this._AnalyzeDialogService = AnalyzeDialogService;

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

        if (['number', 'timestamp', 'date', 'string-date'].includes(e.column.dataType)) {
          e.items.push({
            text: 'Format Data',
            icon: 'grid-menu-item icon-filter',
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
        const columns = this.prepareGridColumns(this.columns);
        this._gridInstance.option('columns', columns);
      }
    }

    prepareGridColumns(columns) {
      return map(columns, column => {
        const isNumberType = NUMBER_TYPES.includes(column.type);
        if (column.type === 'timestamp') {
          column.type = 'date';
        }
        if (FLOAT_TYPES.includes(column.type)) {
          if (!column.format) {
            column.format = {};
          }
          if (!column.format.precision) {
            column.format.precision = DEFAULT_PRECISION;
          }
        }
        const field = {
          caption: column.getDisplayName(),
          dataField: column.name,
          dataType: NUMBER_TYPES.includes(column.type) ? 'number' : column.type,
          type: column.type,
          visibleIndex: column.visibleIndex,
          allowSorting: false,
          alignment: 'left',
          width: COLUMN_WIDTH,
          format: isNumberType ? {formatter: getFormatter(column.format)} : column.format
        };

        if (DATE_TYPES.includes(column.type) && isUndefined(column.format)) {
          field.format = 'yyyy-MM-dd';
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

    checkColumndatatype(columnList, columnName) {
      let datatype = '';
      forEach(columnList, column => {
        if (!isEmpty(column.meta) && column.meta.columnName === columnName) {
          datatype = column.meta.type;
        }
      });
      return datatype;
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
      switch (gridColumn.dataType) {
      case 'number':
        this.formatNumberColumn(gridColumn);
        break;
      case 'date':
      case 'string-date':
      case 'timestamp':
        this.formatDateColumn(gridColumn);
        break;
      default:
      }
    }

    formatDateColumn(gridColumn) {
      this.openFormatModal(gridColumn).then(newFormat => {
        if (this._gridInstance) {
          this._gridInstance.columnOption(gridColumn.dataField, 'format', newFormat.dateFormat);
        }
        this.reportGridContainer.formatColumn(gridColumn.dataField, newFormat.dateFormat);
      });
    }

    formatNumberColumn(gridColumn) {
      const column = find(this.columns, ({name}) => name === gridColumn.dataField);
      this._AnalyzeDialogService.openDataFormatDialog(column.format, column.type)
        .afterClosed().subscribe(format => {
          if (format) {
            if (this._gridInstance) {
              this._gridInstance.columnOption(column.name, 'format', {formatter: getFormatter(format)});
            }
            this.reportGridContainer.formatColumn(column.name, format);
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
