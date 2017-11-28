import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as get from 'lodash/get';
import * as orderBy from 'lodash/orderBy';
import * as isEmpty from 'lodash/isEmpty';
import * as map from 'lodash/map';

import * as template from './analyze-chart-preview.component.html';
import style from './analyze-chart-preview.component.scss';

export const AnalyzeChartPreviewComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class AnalyzeChartPreviewController {
    constructor($componentHandler, $mdDialog, $timeout, AnalyzeService, ChartService, SortService) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;
      this._SortService = SortService;

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
        let parsedData = this._ChartService.parseData(data, this.model.chart.sqlBuilder);

        const sortFields = this._SortService.getArtifactColumns2SortFieldMapper()(
          get(this.model, 'chart.artifacts[0].columns')
        );
        const sorts = get(this.model, 'chart.sqlBuilder.sorts') ?
          this._SortService.mapBackend2FrontendSort(get(this.model, 'chart.sqlBuilder.sorts'), sortFields) : [];

        if (!isEmpty(sorts)) {
          parsedData = orderBy(
            parsedData,
            map(sorts, 'field.dataField'),
            map(sorts, 'order')
          );
        }

        const changes = this._ChartService.dataToChangeConfig(
          this.model.chart.chartType,
          this.model.settings,
          parsedData,
          {labels: this.model.labels, sorts}
        );
        this.chartUpdater.next(changes);
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }
  }
};
