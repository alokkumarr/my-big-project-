import { Component, Input } from '@angular/core';
import * as debounce from 'lodash/debounce';
import * as map from 'lodash/map';
import * as split from 'lodash/split';
import * as clone from 'lodash/clone';
import * as isArray from 'lodash/isArray';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMapValues from 'lodash/fp/mapValues';
import { Observable, isObservable, Subject } from 'rxjs';

import { AnalysisChart, SqlBuilderChart } from '../../../types';
import { ChartService } from '../../../../common/services/chart.service';
import { MapDataService } from '../../../../common/components/charts/map-data.service';

@Component({
  selector: 'map-chart-viewer',
  templateUrl: 'map-chart-viewer.component.html',
  styleUrls: ['map-chart-viewer.component.scss']
})
export class MapChartViewerComponent {
  _fields: any;
  _data: Array<any>;
  _auxSettings: any = {};
  _mapData: Observable<any>;
  _rawSeries: any;
  public chartOptions: any = {};

  @Input() actionBus;
  @Input() updater;

  @Input()
  set analysis(analysis: AnalysisChart) {
    const { dataFields, nodeFields } = analysis.sqlBuilder as SqlBuilderChart;
    this._fields = fpPipe(
      fpFlatMap(x => x),
      fpGroupBy('checked'),
      fpMapValues(([field]) => field)
    )([dataFields, nodeFields]);

    const xField = this._fields.x;
    if (xField.region) {
      this._mapData = this._mapDataService.getMapData(xField.region);
      this.setSeries();
    }

    this.setChartConfig(analysis.legend, analysis.chartTitle || analysis.name);
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
        if (!this.updater) {
          this.updater = new Subject();
        }
        this.updater.next(updateObj);
      });
    }
  }

  setChartConfig(analysisLegend, fileName) {
    const legend = this._chartService.analysisLegend2ChartLegend(
      analysisLegend
    );

    this.chartOptions = {
      mapNavigation: {
        enabled: true
      },
      fileName
    };

    if (legend) {
      this.chartOptions.legend = legend;
    }
  }
}
