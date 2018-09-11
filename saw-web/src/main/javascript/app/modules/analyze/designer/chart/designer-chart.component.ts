import { Component, OnInit, Input } from '@angular/core';
const template = require('./designer-chart.component.html');
require('./designer-chart.component.scss');

import { DesignerStates, CHART_TYPES_OBJ } from '../consts';
import { SqlBuilderChart, Sort } from '../types';
import { ChartService } from '../../services/chart.service';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import * as get from 'lodash/get';
import * as set from 'lodash/set';
import * as clone from 'lodash/clone';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';

@Component({
  selector: 'designer-chart',
  template
})
export class DesignerChartComponent implements OnInit {
  _sqlBuilder: SqlBuilderChart;
  _data: Array<any>;
  _auxSettings: any = {};
  DesignerStates = DesignerStates;
  CHART_TYPES_OBJ = CHART_TYPES_OBJ;

  settings: { xaxis: any; yaxis: Array<any>; zaxis: any; groupBy: any };
  chartOptions: any;
  updateChart = new BehaviorSubject([]);
  chartHgt = {
    height: 500
  };
  isStockChart: boolean;

  @Input() designerState: DesignerStates;

  @Input() chartType: string;

  @Input() sorts: Array<Sort> = [];

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
  set auxSettings(settings) {
    this._auxSettings = settings;

    if (this._data && this._data.length) {
      this.reloadChart(this._data, [...this.getLegendConfig()]);
    }
  }

  @Input()
  set data(executionData) {
    this._data = executionData;
    if (executionData && executionData.length) {
      this.reloadChart(executionData, [...this.getLegendConfig()]);
    }
  }

  constructor(private _chartService: ChartService) {}

  ngOnInit() {
    this.isStockChart = this.chartType.substring(0, 2) === 'ts';
    this.chartOptions = this._chartService.getChartConfigFor(this.chartType, {
      chart: this.chartHgt,
      legend: this._chartService.initLegend({
        ...({ legend: this._auxSettings.legend } || {}),
        chartType: this.chartType
      })
    });
  }

  getLegendConfig() {
    const align = this._chartService.LEGEND_POSITIONING[
      get(this._auxSettings, 'legend.align')
    ];
    const layout = this._chartService.LAYOUT_POSITIONS[
      get(this._auxSettings, 'legend.layout')
    ];

    if (!align || !layout) {
      return [];
    }

    return [
      {
        path: 'legend.align',
        data: align.align
      },
      {
        path: 'legend.verticalAlign',
        data: align.verticalAlign
      },
      {
        path: 'legend.layout',
        data: layout.layout
      }
    ];
  }

  reloadChart(data, changes = []) {
    changes = [
      ...changes,
      ...this._chartService.dataToChangeConfig(
        this.chartType,
        this.settings,
        this._sqlBuilder,
        map(data || [], clone),
        {
          labels: {},
          labelOptions: get(this._auxSettings, 'labelOptions', {}),
          sorts: this.sorts
        }
      )
    ];

    changes.push({
      path: 'chart.inverted',
      data: Boolean(this._auxSettings.isInverted)
    });
    this.updateChart.next(changes);
  }
}
