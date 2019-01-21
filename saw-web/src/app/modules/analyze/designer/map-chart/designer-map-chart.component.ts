import { Component, Input, OnInit } from '@angular/core';
import * as debounce from 'lodash/debounce';
import * as map from 'lodash/map';
import * as split from 'lodash/split';
import * as clone from 'lodash/clone';
import * as omitBy from 'lodash/omitBy';
import * as isNil from 'lodash/isNil';
import * as isArray from 'lodash/isArray';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMapValues from 'lodash/fp/mapValues';
import { Subject, Observable, isObservable } from 'rxjs';

import { SqlBuilderChart } from '../types';
import { ChartService } from '../../../../common/services/chart.service';
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
  _mapData: Observable<any>;
  _rawSeries: any;
  public MapChartStates = MapChartStates;
  public currentState: MapChartStates = MapChartStates.NO_MAP_SELECTED;
  public chartOptions = {};
  public chartUpdater = new Subject();

  @Input() actionBus;

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
      if (oldState === MapChartStates.NO_MAP_SELECTED) {
        this._mapData = this._mapDataService.getMapData(xField.region);
        this.setSeries();
      }
    }
  }

  @Input()
  set auxSettings(settings) {
    this._auxSettings = settings;
    this.updateSettings(settings);
  }

  @Input()
  set data(executionData) {
    if (!executionData) {
      return;
    }
    const series = this._chartService.splitToSeries(
      map(executionData, clone),
      this._fields,
      'geo'
    );
    series[0].data = map(series[0].data, ({ x, y }) => ({ value: y, x }));
    this._data = executionData;
    this._rawSeries = series;
    this.setSeries();
  }

  @Input() chartType: String;

  constructor(
    private _chartService: ChartService,
    private _mapDataService: MapDataService
  ) {
    this.setSeries = debounce(this.setSeries, 50);
  }

  ngOnInit() {
    const legend = this._chartService.analysisLegend2ChartLegend(this._auxSettings.legend);
    this.setChartConfig(legend);
  }

  setSeries() {
    const mapData$ = this._mapData;
    const rawSeries = this._rawSeries;
    const fields = this._fields;

    if (
      isObservable(this._mapData) &&
      isArray(rawSeries) &&
      fields &&
      fields.x
    ) {
      const xField = fields.x;
      const [, identifier] = split(xField.geoType, ':');
      rawSeries[0].joinBy = [identifier, 'x'];
      mapData$.subscribe(mapData => {
        rawSeries[0].mapData = mapData;
        const updateObj = {
          series: rawSeries
        };
        this.chartUpdater.next(updateObj);
      });
    }
  }

  setChartConfig(legend) {
    this.chartOptions = omitBy({
      mapNavigation: {
        enabled: true
      },
      legend
    }, isNil);
  }

  updateSettings(auxSettings) {
    if (!this.chartUpdater) {
      return;
    }

    const legend = this._chartService.analysisLegend2ChartLegend(auxSettings.legend);

    if (!legend) {
      return;
    }
    const updateObj = omitBy({ legend }, isNil);
    this.chartUpdater.next(updateObj);
  }
}
