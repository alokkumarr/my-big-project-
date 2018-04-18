import * as get from 'lodash/get';
import * as values from 'lodash/values';
import * as orderBy from 'lodash/orderBy';
import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import * as template from './analyze-chart-detail.component.html';

export const AnalyzeChartDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzeChartDetailController {
    constructor(ChartService, FilterService, $timeout, SortService) {
      'ngInject';

      this._ChartService = ChartService;
      this._FilterService = FilterService;
      this._SortService = SortService;
      this._$timeout = $timeout;
      this.chartUpdater = new BehaviorSubject({});
      this.labels = {
        y: '', x: ''
      };

    }

    $onInit() {
      this.initAnalysis();
      this.subscription = this.requester.subscribe(options => this.request(options));

      this._$timeout(() => {
        this.updateChart();
      });
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
        this.filteredData,
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
      this.updateChart();
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this.chartUpdater.next({
        export: true
      });
    }
  }
};
