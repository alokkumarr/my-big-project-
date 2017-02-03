import assign from 'lodash/assign';
import map from 'lodash/map';
import find from 'lodash/find';
import forEach from 'lodash/forEach';
import remove from 'lodash/remove';
import isUndefined from 'lodash/isUndefined';

import template from './report-grid.component.html';
import style from './report-grid.component.scss';

// const MIN_ROWS_TO_SHOW = 5;
const COLUMN_WIDTH = 175;

export const ReportGridComponent = {
  template,
  style: [style],
  bindings: {
    reportGridContainer: '<',
    reportGridNode: '<',
    source: '<'
  },
  controller: class ReportGridController {
    constructor($mdDialog) {
      'ngInject';
      this._$mdDialog = $mdDialog;

      this.settings = {};
      this.columns = [];
      this.sorts = [];
    }

    $onInit() {
      this.reportGridNode.setGridComponent(this);

      this.settings = assign(this.settings, {
        gridConfig: {
          onInitialized: this.onGridInitialized.bind(this),
          onContextMenuPreparing: this.onContextMenuPreparing.bind(this),
          columns: this.prepareGridColumns(this.columns),
          dataSource: this.source || [],
          columnAutoWidth: true,
          allowColumnReordering: true,
          allowColumnResizing: true,
          showColumnHeaders: true,
          showColumnLines: false,
          showRowLines: false,
          showBorders: false,
          rowAlternationEnabled: true,
          hoverStateEnabled: true,
          scrolling: {
            mode: 'virtual'
          },
          sorting: {
            mode: 'multiple'
          }
        }
      });
    }

    onGridInitialized(e) {
      this._gridInstance = e.component;
    }

    onContextMenuPreparing(e) {
      if (e.target === 'header') {
        e.items = [];

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

        if (e.column.dataType === 'int' || e.column.dataType === 'double') {
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
        return {
          caption: column.getDisplayName(),
          dataField: column.name,
          dataType: column.type,
          allowSorting: false,
          alignment: 'left',
          width: COLUMN_WIDTH
        };
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
          skipHide: true,
          clickOutsideToClose: true
        });
    }
  }
};
