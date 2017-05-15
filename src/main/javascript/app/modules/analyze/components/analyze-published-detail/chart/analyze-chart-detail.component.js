import get from 'lodash/get';
import map from 'lodash/map';
import uniq from 'lodash/uniq';
import forEach from 'lodash/forEach';
import values from 'lodash/values';
import filter from 'lodash/filter';
import isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-chart-detail.component.html';
import style from './analyze-chart-detail.component.scss';
import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter/analyze-filter-sidenav.component';

export const AnalyzeChartDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  styles: [style],
  controller: class AnalyzeChartDetailController {
    constructor(ChartService, FilterService, $timeout) {
      'ngInject';

      this._ChartService = ChartService;
      this._FilterService = FilterService;
      this._$timeout = $timeout;
      this.chartUpdater = new BehaviorSubject({});
      this.filters = {};
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());
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

      this.filters.possible = map(this.analysis.chart.filters, this._FilterService.getBackEnd2FrontEndFilterMapper());
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
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

    openFilterSidenav() {
      if (!isEmpty(this.filters.possible)) {
        this._FilterService.openFilterSidenav(this.filters.possible, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
      }
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.filterGridData();
      this.updateChart();
    }

    onClearAllFilters() {
      this.filters.possible = this._FilterService.getFilterClearer()(this.filters.possible);
      this.filters.selected = [];
      this.filteredData = this.data;
      this.updateChart();
    }

    onFilterRemoved(filter) {
      filter.model = null;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.filterGridData();
      this.updateChart();
    }

    filterGridData() {
      this.filteredData = this._FilterService.getGridDataFilter(this.filters.selected)(this.data);
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

    setFilterItems(filters, data) {
      forEach(filters, filter => {
        if (filter.type === 'string' || filter.type === 'String') {
          filter.items = uniq(map(data, filter.name));
        }
      });
    }
  }
};
