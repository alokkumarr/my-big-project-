import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import * as template from './analyze-chart-preview.component.html';
import style from './analyze-chart-preview.component.scss';

export const AnalyzeChartPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeChartPreviewController {
    constructor($componentHandler, $mdDialog, $timeout, AnalyzeService, ChartService) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;

      this.MORE_ROWS_COUNT = 500;
      this.data = [];
      this.chartUpdater = new BehaviorSubject({});
    }

    $onInit() {
      this.chart = {
        height: 680
      };
      this.chartOptions = this._ChartService.getChartConfigFor(this.model.chart.chartType, {chart: this.chart, legend: this.model.legend});
      this._AnalyzeService.previewExecution(this.model.chart).then(({data}) => {
        const parsedData = this._ChartService.parseData(data, this.model.chart.sqlBuilder);
        const changes = this._ChartService.dataToChangeConfig(
          this.model.chart.chartType,
          this.model.settings,
          parsedData,
          {labels: this.model.labels}
        );
        this.chartUpdater.next(changes);
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
