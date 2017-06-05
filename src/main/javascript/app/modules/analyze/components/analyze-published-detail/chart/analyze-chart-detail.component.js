import get from 'lodash/get';
import map from 'lodash/map';
import values from 'lodash/values';
import filter from 'lodash/filter';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-chart-detail.component.html';

export const AnalyzeChartDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzeChartDetailController {
    constructor(ChartService, FilterService, $timeout) {
      'ngInject';

      this._ChartService = ChartService;
      this._FilterService = FilterService;
      this._$timeout = $timeout;
      this.chartUpdater = new BehaviorSubject({});
      this.filters = [];
    }

    $onInit() {
      this.requester.subscribe(requests => this.request(requests));

      const artifacts = this.analysis.chart.artifacts;
      const yaxis = filter(artifacts, 'y-axis');
      const xaxis = filter(artifacts, 'x-axis');
      const groupBy = filter(artifacts, 'z-axis');

      this.settings = {
        yaxis,
        xaxis,
        groupBy
      };

      this.filters = map(this.analysis.chart.filters, this._FilterService.backend2FrontendFilter(artifacts));
      this.data = this.analysis.chart.data;
      this.filterGridData();

      this.setFilterItems(this.filters.possible, this.data);

      this.legend = {
        align: get(this.analysis, 'chart.legend.align', 'right'),
        layout: get(this.analysis, 'chart.legend.layout', 'vertical'),
        options: {
          align: values(this._ChartService.LEGEND_POSITIONING),
          layout: values(this._ChartService.LAYOUT_POSITIONS)
        }
      };
      this.chartOptions = this._ChartService.getChartConfigFor(this.analysis.chartType, {legend: this.legend});

      this._$timeout(() => {
        this.updateChart();
      });
      this.openFilterSidenav();
    }

    updateChart() {
      const changes = this._ChartService.dataToChangeConfig(
      this.analysis.chartType,
      this.settings,
      this.filteredData,
      {labels: this.labels}
      );

      this.chartUpdater.next(changes);
    }

    request(requests) {
      /* eslint-disable no-unused-expressions */
      requests.export && this.onExport();
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this.chartUpdater.next({
        export: true
      });
    }
  }
};
