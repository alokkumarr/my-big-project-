import { Component, OnInit, Input } from '@angular/core';
const template = require('./designer-chart.component.html');
require('./designer-chart.component.scss');

import { SqlBuilderChart } from '../types';
import { ChartService } from '../../../services/chart.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';

@Component({
  selector: 'designer-chart',
  template
})
export class DesignerChartComponent implements OnInit {
  _sqlBuilder: SqlBuilderChart;
  _data: Array<any>;
  settings: { xaxis: any; yaxis: Array<any>; zaxis: any; groupBy: any };
  chartOptions: any;
  updateChart = new BehaviorSubject([]);
  chartHgt = {
    height: 500
  };

  @Input() chartType: string;

  @Input()
  set sqlBuilder(data: SqlBuilderChart) {
    this._sqlBuilder = data;
    this.settings = {
      xaxis: filter(
        get(data, 'nodeFields', []),
        f => f.area === 'x' || f.checked === 'x'
      ),
      yaxis: filter(
        get(data, 'dataFields', []),
        f => f.area === 'y' || f.checked === 'y'
      ),
      zaxis: filter(
        get(data, 'dataFields', []),
        f => f.area === 'z' || f.checked === 'z'
      ),
      groupBy: filter(
        get(data, 'nodeFields', []),
        f => f.area === 'g' || f.checked === 'g'
      )
    };
  }

  @Input()
  set data(executionData) {
    this._data = executionData;
    if (executionData && executionData.length) {
      this.reloadChart(executionData);
    }
  }

  constructor(private _chartService: ChartService) {}

  ngOnInit() {
    this.chartOptions = this._chartService.getChartConfigFor(this.chartType, {
      chart: this.chartHgt,
      legend: this._chartService.initLegend({ chartType: this.chartType })
    });
  }

  reloadChart(data) {
    const changes = this._chartService.dataToChangeConfig(
      this.chartType,
      this.settings,
      data,
      { labels: {}, sorts: [] }
    );

    changes.push({
      path: 'chart.inverted',
      data: false
    });
    this.updateChart.next(changes);
  }
}
