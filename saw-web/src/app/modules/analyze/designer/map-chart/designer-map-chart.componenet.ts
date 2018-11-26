import { Component, Input, OnInit } from '@angular/core';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpMapValues from 'lodash/fp/mapValues';
import { Subject } from 'rxjs';

import { SqlBuilderChart } from '../types';
import { ChartService } from '../../services/chart.service';
import { usAll } from '../../../../common/components/charts/map-data/us-all.geo';

@Component({
  selector: 'designer-map-chart',
  templateUrl: 'designer-map-chart.component.html',
  styleUrls: ['designer-map-chart.component.scss']
})

export class DesignerMapChartComponent implements OnInit {
  _sqlBuilder: SqlBuilderChart;
  _data: Array<any>;
  _auxSettings: any = {};
  public chartOptions = {};
  public chartUpdater = new Subject();

  @Input()
  set sqlBuilder(sqlBuilder: SqlBuilderChart) {
    this._sqlBuilder = sqlBuilder;
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

  constructor(private _chartService: ChartService) { }

  ngOnInit() {
    this.chartOptions = this.getConfig(this._auxSettings, this._sqlBuilder, this._data);
  }

  getConfig(auxSettings, sqlBuilder, data = []) {
    const labelOptions = get(this._auxSettings, 'labelOptions', {});
    const { dataFields, nodeFields } = sqlBuilder;
    const fields = fpPipe(
      fpFlatMap(x => x),
      fpGroupBy('checked'),
      fpMapValues(([field]) => field)
    )([dataFields, nodeFields]);

    const series = this.getSeries(
      'geo',
      auxSettings,
      fields,
      map(data, clone),
      { labels: {}, labelOptions }
    );

    return {
      mapNavigation: {
        enabled: true
      },
      series
    };
  }

  getSeries(type, settings, fields, gridData, opts) {
    console.log('type', type);
    console.log('settings', settings);
    console.log('fields', fields);
    console.log('opts', opts);
    const series = this._chartService.splitToSeries(
      gridData,
      fields,
      type
    );
    // const xField = fields.x;
    // const identifier = xField.regionIdentifier;
    const identifier = 'postal-code';
    series[0].joinBy = [identifier, 'x'];
    series[0].mapData = usAll;
    console.log('series', series);
    return series;
  }

  getLegendConfigChanges() {
    const align = this._chartService.LEGEND_POSITIONING[
      get(this._auxSettings, 'legend.align')
    ];
    const layout = this._chartService.LAYOUT_POSITIONS[
      get(this._auxSettings, 'legend.layout')
    ];

    if (!align || !layout) {
      return [];
    }

    return [{
      path: 'legend.align',
      data: align.align
    }, {
      path: 'legend.verticalAlign',
      data: align.verticalAlign
    }, {
      path: 'legend.layout',
      data: layout.layout
    }];
  }

}
