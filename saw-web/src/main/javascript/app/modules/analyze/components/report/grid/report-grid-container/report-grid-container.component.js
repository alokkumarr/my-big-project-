import * as groupBy from 'lodash/groupBy';
import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as omit from 'lodash/fp/omit';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';
import * as isArray from 'lodash/isArray';
import * as filter from 'lodash/filter';
import * as assign from 'lodash/assign';
import * as isUndefined from 'lodash/isUndefined';

import * as template from './report-grid-container.component.html';
import style from './report-grid-container.component.scss';

export const LAYOUT_MODE = {
  DETAIL: 'detail',
  SUMMARY: 'summary'
};

export const ReportGridContainerComponent = {
  template,
  bindings: {
    id: '@',
    eventEmitter: '<',
    source: '<',
    columns: '<',
    settings: '<',
    showGroupPanel: '@',
    showViewMore: '@'
  },
  styles: [style],
  controller: class ReportGridContainerController {
    constructor($componentHandler, $timeout) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$timeout = $timeout;

      this.gridNodeComponent = null;

      this.LAYOUT_MODE = LAYOUT_MODE;

      this.columns = [];
      this.sorts = [];
      this.grouped = {
        source: [],
        by: [],
        byText: () => {
          return map(this.grouped.by, item => item.getDisplayName()).join(', ');
        }
      };
    }

    $onInit() {
      this._unregister = this._$componentHandler.register(this.id, this);

      this.settings = assign(this.settings || {}, {
        layoutMode: LAYOUT_MODE.DETAIL
      });

      if (!isEmpty(this.columns)) {
        this.updateColumns(this.columns);
      }

      this.applyGrouping();
    }

    $onDestroy() {
      this._unregister();
    }

    onColumnReorder(gridColumns) {
      this.columns = map(this.columns, column => {
        const target = find(gridColumns, ({dataField}) => {
          return dataField === column.name;
        });
        if (target) {
          column.visibleIndex = target.visibleIndex;
        }
        return column;
      });
      this.updateColumns(this.columns);
      this.refreshGrid();
    }

    setGridNodeComponent(gridNodeComponent) {
      this.gridNodeComponent = gridNodeComponent;

      if (this.gridNodeComponent) {
        this.gridNodeComponent.updateSettings({
          layoutMode: this.settings.layoutMode
        });

        this.updateColumns();
        this.updateSorts();
        this.applyGrouping();
      }
    }

    refreshGrid() {
      if (this.gridNodeComponent) {
        this.gridNodeComponent.refreshGrid();
      }
    }

    updateColumns(columns) {
      if (!isUndefined(columns)) {
        this.columns = columns;
      }
      if (this.gridNodeComponent) {
        columns = filter(this.columns, column => {
          return column.checked && !this.isGroupedBy(column.name);
        });

        this.gridNodeComponent.updateColumns(columns);
      }
    }

    updateSorts(sorts) {
      if (!isUndefined(sorts)) {
        this.sorts = sorts;
      }

      if (this.gridNodeComponent) {
        this.gridNodeComponent.updateSorts(this.sorts);
      }
    }

    updateSource(source) {
      this.source = isArray(source) ? source : [];

      this.applyGrouping();

      if (this.gridNodeComponent) {
        this._$timeout(() => {
          this.gridNodeComponent.onSourceUpdate();
        });
      }
    }

    onLayoutModeUpdate() {
      if (this.gridNodeComponent) {
        this.gridNodeComponent.updateSettings({
          layoutMode: this.settings.layoutMode
        });
      }
    }

    applyGrouping() {
      this.grouped.source = this.source;

      forEach(this.grouped.by, column => {
        this.grouped.source = this.groupRecursive(this.grouped.source, column.name);
      });
    }

    getColumnByName(columnName) {
      return find(this.columns, column => column.name === columnName);
    }

    isGroupedBy(columnName) {
      return Boolean(find(this.grouped.by, column => column.name === columnName));
    }

    groupByColumn(columnName, emit = true) {
      if (!this.isGroupedBy(columnName)) {
        const column = this.getColumnByName(columnName);

        if (column && column.checked) {
          this.grouped.source = this.groupRecursive(this.grouped.source, columnName);
          this.grouped.by.push(column);

          this.updateColumns();
        }

        if (emit) {
          this.eventEmitter.emit('groupingChanged', this.grouped.by);
        }
      }
    }

    undoGrouping() {
      if (!isEmpty(this.grouped.by)) {
        this.grouped.by.pop();

        this.settings.layoutMode = this.LAYOUT_MODE.DETAIL;
        this.onLayoutModeUpdate();

        this.updateColumns();
        this.applyGrouping();
        this.eventEmitter.emit('groupingChanged', this.grouped.by);
      }
    }

    groupRecursive(data, columnName) {
      // if it is a node
      let groupedData;

      if (data.isGroup) {
        forEach(data.groupNodes, groupNode => {
          groupNode.source = this.groupRecursive(groupNode.source, columnName);
        });

        groupedData = data;
      } else {
        // if it is a leaf
        groupedData = this.groupArray(data, columnName);
      }

      return groupedData;
    }

    groupArray(array, columnName) {
      const groupedObj = groupBy(array, columnName);
      const groupNodes = map(groupedObj, (val, key) => {
        return {
          groupValue: key,
          itemCount: val.length,
          source: map(val, omit(columnName))
        };
      });

      return {
        isGroup: true,
        groupBy: columnName,
        groupNodes
      };
    }

    renameColumn(columnName, alias) {
      const column = find(this.columns, column => column.name === columnName);

      if (column) {
        column.alias = alias;

        this.updateColumns();
      }
    }

    formatColumn(columnName, datatype, format) {
      for (let i = 0; i < this.columns.length; i++) {
        if (this.columns[i].name === columnName) {
          switch (format.type) {
            case 'date':
              this.columns[i].type = 'date';
              this.columns[i].format = format.dateFormat;
              break;
            case 'timestamp':
              this.columns[i].type = 'date';
              this.columns[i].format = format.dateFormat;
              break;
            case 'number':
              this.columns[i].format = {
                precision: 0,
                type: 'decimal',
                currency: undefined,
                currencySymbol: undefined
              };
              if (format.NumberDecimal > -1) {
                this.columns[i].format.precision = format.NumberDecimal;
              }
              if (format.CommaSeparator) {
                this.columns[i].format.type = 'fixedPoint';
              } else {
                this.columns[i].format.type = 'decimal';
              }
              if (format.CurrencyFlag) {
                this.columns[i].format.currency = format.CurrencyCode;
                this.columns[i].format.currencySymbol = format.CurrencySymbol;
              }
              break;
            case 'default':
              break;
          }
        }
      }
    }

    hideColumn(columnName) {
      const column = find(this.columns, column => column.name === columnName);

      if (column && column.checked) {
        column.checked = false;

        this.updateColumns();
      }
    }

    viewMore() {
      return;
    }
  }
};
