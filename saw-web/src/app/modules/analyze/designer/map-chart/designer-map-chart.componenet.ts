import { Component, Input, OnInit } from '@angular/core';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as split from 'lodash/split';
import * as clone from 'lodash/clone';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMapValues from 'lodash/fp/mapValues';
import { Subject } from 'rxjs';

import { SqlBuilderChart } from '../types';
import { ChartService } from '../../services/chart.service';
import { MapDataService } from '../../../../common/components/charts/map-data.service';

export enum MapChartStates {
  NO_MAP_SELECTED,
  OK
}
@Component({
  selector: 'designer-map-chart',
  templateUrl: 'designer-map-chart.component.html',
  styleUrls: ['designer-map-chart.component.scss']
})
export class DesignerMapChartComponent implements OnInit {
  _fields: any;
  _data: Array<any>;
  _auxSettings: any = {};
  public MapChartStates = MapChartStates;
  public currentState: MapChartStates = MapChartStates.NO_MAP_SELECTED;
  public chartOptions = {};
  public chartUpdater = new Subject();

  @Input()
  set sqlBuilder(sqlBuilder: SqlBuilderChart) {
    const { dataFields, nodeFields } = sqlBuilder;
    this._fields = fpPipe(
      fpFlatMap(x => x),
      fpGroupBy('checked'),
      fpMapValues(([field]) => field)
    )([dataFields, nodeFields]);

    const xField = this._fields.x;
    if (xField.region) {
      const oldState = this.currentState;
      this.currentState = MapChartStates.OK;
      if (this.currentState !== oldState) {
        this.setChartConfig(this._fields, this._data);
      }
    }
  }

  @Input()
  set auxSettings(settings) {
    this._auxSettings = settings;
  }

  @Input()
  set data(executionData) {
    this._data = executionData;
  }

  @Input() chartType: String;

  constructor(
    private _chartService: ChartService,
    private _mapDataService: MapDataService
  ) {}

  ngOnInit() {
    this.setChartConfig(this._fields, this._data);
  }

  async setChartConfig(fields, data = []) {
    const series = await this.getSeries('geo', fields, map(data, clone));

    this.chartOptions = {
      legend: this.getLegendConfig(),
      mapNavigation: {
        enabled: true
      },
      series
    };
  }

  getSeries(type, fields, gridData) {
    const series = this._chartService.splitToSeries(gridData, fields, type);
    const xField = fields.x;
    const region = xField.region;
    const [, identifier] = split(xField.geoType, ':');
    series[0].joinBy = [identifier, 'x'];

    return new Promise(resolve => {
      // add mapData to the series
      this._mapDataService.getMapData(region).subscribe(mapData => {
        series[0].mapData = mapData;
        resolve(series);
      });
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
      return {};
    }

    return {
      align: align.align,
      verticalAlign: align.verticalAlign,
      layout: layout.layout
    };
  }
}
