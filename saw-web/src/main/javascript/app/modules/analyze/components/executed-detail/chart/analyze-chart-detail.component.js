import * as get from 'lodash/get';
import * as clone from 'lodash/clone';
import * as values from 'lodash/values';
import * as orderBy from 'lodash/orderBy';
import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as flatMap from 'lodash/flatMap';
import * as forEach from 'lodash/forEach';

import * as template from './analyze-chart-detail.component.html';

const DEFAULT_PAGE_SIZE = 25;

export const AnalyzeChartDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzeChartDetailController {
    constructor(FilterService, $timeout, SortService, $injector) {
      'ngInject';

      this._$injector = $injector;
      this._FilterService = FilterService;
      this._SortService = SortService;
      this._$timeout = $timeout;
      this.chartUpdater = new BehaviorSubject({});
      this.labels = {
        y: '', x: ''
      };

    }

    $onInit() {
      this._ChartService = this._$injector.get('ChartService');
      this.initAnalysis();
      this.subscription = this.requester.subscribe(options => this.request(options));

      this._$timeout(() => {
        this.updateChart();
      });

      this.toggleToGrid = false;
    }

    initAnalysis() {
      this.settings = this._ChartService.fillSettings(this.analysis.artifacts, this.analysis);
      this.sortFields = this._SortService.getArtifactColumns2SortFieldMapper()(this.analysis.artifacts[0].columns);
      this.sorts = this.analysis.sqlBuilder.sorts ?
        this._SortService.mapBackend2FrontendSort(this.analysis.sqlBuilder.sorts, this.sortFields) : [];
      this.labels.x = get(this.analysis, 'xAxis.title', null);
      this.labels.y = get(this.analysis, 'yAxis.title', null);
      this.legend = {
        align: get(this.analysis, 'legend.align', 'right'),
        layout: get(this.analysis, 'legend.layout', 'vertical'),
        options: {
          align: values(this._ChartService.LEGEND_POSITIONING),
          layout: values(this._ChartService.LAYOUT_POSITIONS)
        }
      };
      this.chart = {
        height: 580
      };
      this.chartOptions = this._ChartService.getChartConfigFor(this.analysis.chartType, {chart: this.chart, legend: this.legend});
      this.isStockChart = this.analysis.isStockChart;

      this.columns = this.analysis.edit ? null : this._getColumns(this.analysis);
    }

    $onDestroy() {
      this.subscription.unsubscribe();
    }

    $onChanges(data) {
      if (isEmpty(get(data, 'analysis.previousValue'))) {
        return;
      }

      this.initAnalysis();
    }

    _getColumns(analysis) {
      const columns = flatMap(analysis.artifacts, table => {
        return table.columns;
      });

      /* Add aggregate to columns. Helps in calculating conditional
       * formatting based on aggregates */
      forEach(get(analysis, 'sqlBuilder.dataFields') || [], aggregates => {
        forEach(columns, column => {
          if (aggregates.columnName === column.columnName) {
            column.aggregate = aggregates.aggregate;
          }
          column.reportType = analysis.type;
        });
      });
      return columns;
    }

    updateChart() {
      if (!isEmpty(this.sorts)) {
        this.filteredData = orderBy(
          this.filteredData,
          map(this.sorts, 'field.dataField'),
          map(this.sorts, 'order')
        );
      }

      let changes = this._ChartService.dataToChangeConfig(
        this.analysis.chartType,
        this.settings,
        [...map(this.filteredData, clone)],
        {labels: this.labels, labelOptions: this.analysis.labelOptions, sorts: this.sorts}
      );

      changes = changes.concat([
        {path: 'title.text', data: this.analysis.name},
        {path: 'chart.inverted', data: this.analysis.isInverted}
      ]);
      this.chartUpdater.next(changes);
    }

    request({data}) {
      /* eslint-disable no-unused-expressions */
      if (!data) {
        return;
      }
      this.filteredData = this._ChartService.parseData(data, this.analysis.sqlBuilder);
      const gridData = this.filteredData;
      this.updateChart();

      this.loadGridData = {
        columnMinWidth: 150,
        columnAutoWidth: false,
        columnResizingMode: 'widget',
        allowColumnReordering: true,
        allowColumnResizing: true,
        showColumnHeaders: true,
        showColumnLines: false,
        showRowLines: false,
        showBorders: false,
        rowAlternationEnabled: true,
        hoverStateEnabled: true,
        wordWrapEnabled: false,
        customizeColumns: this.customizeColumns(),
        gridWidth: '100%',
        paging: {
          pageSize: DEFAULT_PAGE_SIZE
        },
        pager: {
          showNavigationButtons: true,
          allowedPageSizes: [DEFAULT_PAGE_SIZE, 50, 75, 100],
          showPageSizeSelector: true
        },
        dataSource: this.trimKeyword(gridData)

      };
      /* eslint-disable no-unused-expressions */
    }

    customizeColumns(columns) {
      forEach(columns, col => {
        col.allowSorting = false;
        col.alignment = 'left';
      });
    }

    onExport() {
      this.chartUpdater.next({
        export: true
      });
    }

    isFloat(n) {
      return Number(n) === n && n % 1 !== 0;
    }

    fetchColumnData(axisName, value) {
      let aliasName = axisName;
      forEach(this.columns, column => {
        if (axisName === column.name) {
          aliasName = column.aliasName || column.displayName;
        }
        if (axisName === column.name && (column.aggregate === 'percentage' || column.aggregate === 'avg')) {
          value = value.toFixed(2) + (column.aggregate === 'percentage' ? '%' : '');
        }
      });
      return {aliasName, value};
    }

    trimKeyword(data) {
      const trimData = data.map(row => {
        const obj = {};
        for (const key in row) {
          if (key) {
            const trimKey = this.fetchColumnData(key.split('.')[0], row[key]);
            obj[trimKey.aliasName] = trimKey.value;
          }
        }
        return obj;
      });
      return trimData;
    }
  }
};
