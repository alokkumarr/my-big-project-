import { Component, Input } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as orderBy from 'lodash/orderBy';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';
import * as map from 'lodash/map';
import * as get from 'lodash/get';

import { ChartService } from '../../services/chart.service';
import { AnalysisChart, Sort } from '../../types';

const template = require('./executed-chart-view.component.html');

@Component({
  selector: 'executed-chart-view',
  template
})
export class ExecutedChartViewComponent {
  @Input() updater: BehaviorSubject<Object[]>;
  @Input('analysis') set setAnalysis(analysis: AnalysisChart) {
    this.analysis = analysis;
    this.initChartOptions(analysis);
  };
  @Input('data') set setData(data: any[]) {
    this.updateChart(data, this.analysis);
  };

  analysis: AnalysisChart;
  isStockChart: boolean;
  chartOptions: Object;

  constructor(
    private _chartService: ChartService
  ) { }

  initChartOptions(analysis) {
    const { LEGEND_POSITIONING, LAYOUT_POSITIONS } = this._chartService;
    const legend = {
      align: get(analysis, 'legend.align', 'right'),
      layout: get(analysis, 'legend.layout', 'vertical'),
      options: {
        align: values(LEGEND_POSITIONING),
        layout: values(LAYOUT_POSITIONS)
      }
    };
    const chart = {
      height: 580
    };
    this.chartOptions = this._chartService.getChartConfigFor(analysis.chartType, {chart, legend});
    this.isStockChart = analysis.isStockChart;
  }

  updateChart(data, analysis) {
    const settings = this._chartService.fillSettings(analysis.artifacts, analysis);
    const sorts = analysis.sqlBuilder.sorts;
    const labels = {
      x: get(analysis, 'xAxis.title', null),
      y: get(analysis, 'yAxis.title', null)
    };
    let orderedData;
    if (!isEmpty(sorts)) {
      orderedData = orderBy(
        data,
        map(sorts, 'columnName'),
        map(sorts, 'order')
      );
    }

    let changes = [
      ...this._chartService.dataToChangeConfig(
        analysis.chartType,
        settings,
        orderedData || data,
        {labels, labelOptions: analysis.labelOptions, sorts}
      ),
      {path: 'title.text', data: analysis.name},
      {path: 'chart.inverted', data: analysis.isInverted}
    ];

    this.updater.next(changes);
  }
}
