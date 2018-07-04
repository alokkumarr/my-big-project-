import { Component, Input } from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as orderBy from 'lodash/orderBy';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as moment from 'moment';

import { ChartService } from '../../services/chart.service';
import { AnalysisChart, Sort } from '../../types';

const template = require('./executed-chart-view.component.html');

const DEFAULT_PAGE_SIZE = 25;

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
    this.toggleToGrid = false;
    const updates = this.getChartUpdates(data, this.analysis);
    setTimeout(() => {
      // defer updating the chart so that the chart has time to initialize
      this.updater.next(updates);
    });
  };

  analysis: AnalysisChart;
  isStockChart: boolean;
  chartOptions: Object;
  toggleToGrid: false;
  chartToggleData: any;

  constructor(
    private _chartService: ChartService
  ) { }

  initChartOptions(analysis) {
    this.toggleToGrid = false;
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

  isFloat(n){
    return Number(n) === n && n % 1 !== 0;
  }

  fetchColumnData(axisName, value) {
    let aliasName = axisName;
    forEach(this.analysis.artifacts[0].columns, column => {
      if(axisName === column.name) {
        aliasName = column.aliasName || column.displayName;
        value = column.type === 'date' ? moment.utc(value).format(column.dateFormat === 'MMM d YYYY' ? 'MMM DD YYYY' : column.dateFormat ) : value;
        if((value) && (column.aggregate === 'percentage' || column.aggregate === 'avg')) {
          value = value.toFixed(2) + (column.aggregate === 'percentage' ? '%' : '');
        }
      }
    })
    return {aliasName, value};
  }

  trimKeyword(data) {
    let trimData = data.map(row => {
      let obj = {};
      for(var key in row) {
        let trimKey = this.fetchColumnData(key.split('.')[0], row[key]);
        obj[trimKey.aliasName] = trimKey.value;
      }
      return obj;
    });
    return trimData;
  }

  getChartUpdates(data, analysis) {
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

    this.chartToggleData = this.trimKeyword(data);

    return [
      ...this._chartService.dataToChangeConfig(
        analysis.chartType,
        settings,
        orderedData || data,
        {labels, labelOptions: analysis.labelOptions, sorts}
      ),
      {path: 'title.text', data: analysis.name},
      {path: 'chart.inverted', data: analysis.isInverted}
    ];
  }
}
