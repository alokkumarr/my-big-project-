import {
  Component,
  AfterViewInit,
  OnInit,
  Input,
  ViewChild,
  ElementRef
} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import * as get from 'lodash/get';
import * as clone from 'lodash/clone';
import * as map from 'lodash/map';

import { CHART_TYPES_OBJ } from '../consts';
import { SqlBuilderChart, Sort } from '../types';
import { ChartService } from '../../../../common/services/chart.service';
import { QueryDSL } from 'src/app/models';

@Component({
  selector: 'designer-chart',
  templateUrl: './designer-chart.component.html',
  styleUrls: ['./designer-chart.component.scss']
})
export class DesignerChartComponent implements AfterViewInit, OnInit {
  _data: Array<any>;
  chartType: string;
  _auxSettings: any = {};
  CHART_TYPES_OBJ = CHART_TYPES_OBJ;

  chartOptions: any;
  @Input() updater;

  @ViewChild('chartContainer') chartContainer: ElementRef;
  chartHgt = {
    height: 500
  };

  @Input('chartType') public set setChartType(chartType: string) {
    this.chartType = chartType;
    if (this._data && this._data.length) {
      this.reloadChart(this._data, [...this.getLegendConfig()]);
    }
  }

  @Input() sorts: Array<Sort> = [];

  @Input() sipQuery: QueryDSL;

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
    this.chartOptions = this._chartService.getChartConfigFor(this.chartType, {
      chart: this.chartHgt,
      legend: this._chartService.initLegend({
        ...({ legend: this._auxSettings.legend } || {}),
        chartType: this.chartType
      })
    });

    if (!this.updater) {
      this.updater = new BehaviorSubject([]);
    }
  }

  ngAfterViewInit() {
    this.chartHgt.height = this.getChartHeight();
  }

  /**
   * Converts sipQuery to sqlBuilder like object for use in chart service.
   * This is a non-ideal work-around made until we can locate all the places
   * we need to change.
   *
   * @param {*} queryOrBuilder
   * @returns {SqlBuilderChart}
   * @memberof DesignerChartComponent
   */
  sipQueryToSQLBuilderFields(queryOrBuilder): SqlBuilderChart {
    if (queryOrBuilder.nodeFields || queryOrBuilder.dataFields) {
      return queryOrBuilder;
    }

    const builderLike: SqlBuilderChart = {
      dataFields: [],
      nodeFields: [],
      filters: queryOrBuilder.filters,
      booleanCriteria: queryOrBuilder.booleanCriteria
    };

    (queryOrBuilder.artifacts || []).forEach(table => {
      (table.fields || []).forEach(column => {
        if (['y', 'z'].includes(column.area)) {
          builderLike.dataFields.push(column);
        } else {
          builderLike.nodeFields.push(column);
        }
      });
    });

    return builderLike;
  }

  /**
   * getChartHeight
   * Highcharts doesn't work well with height adjustment from CSS.
   * This method calculates the height of container available,
   * so that it can be provided to chart as option during initialisation.
   *
   * @returns {number}
   */
  getChartHeight(): number {
    return Math.min(
      this.chartHgt.height,
      this.chartContainer.nativeElement.offsetHeight
    );
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
    const changeConfig = this._chartService.dataToChangeConfig(
      this.chartType,
      this.sipQuery,
      map(data || [], clone),
      {
        labels: {},
        labelOptions: get(this._auxSettings, 'labelOptions', {}),
        sorts: this.sorts
      }
    );
    changes = [...changes, ...changeConfig];

    changes.push({
      path: 'chart.inverted',
      data: Boolean(this._auxSettings.isInverted)
    });
    this.updater.next(changes);
  }
}
