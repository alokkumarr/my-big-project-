import {
  Component,
  ElementRef,
  Input,
  ViewChild
 } from '@angular/core';

import * as Highcharts from 'highcharts/highstock';
import * as defaultsDeep from 'lodash/defaultsDeep';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as isArray from 'lodash/isArray';
import {globalChartOptions, chartOptions, stockChartOptions} from './default-chart-options';
import * as isUndefined from 'lodash/isUndefined';

export const UPDATE_PATHS = {
  SERIES: 'series.0',
  X_AXIS: 'xAxis'
};

@Component({
  selector: 'chart',
  template: `<div #container></div>`
})
export class ChartComponent {
  @Input() updater: any;
  @Input() options: any;
  @Input() isstockchart: any;
  @ViewChild('container') container: ElementRef;

  private highcharts: any = Highcharts;
  private chart: any = null;
  private stockChart: any = null;
  private config: any = {};
  private stockConfig: any = {};
  private subscription: any;

  constructor() {
    this.highcharts.setOptions(globalChartOptions);
  }

  ngAfterViewInit() {
    this.config = defaultsDeep(this.options, chartOptions);
    this.stockConfig = defaultsDeep(this.options, stockChartOptions);
    if (this.isstockchart) {
      this.chart = this.highcharts.stockChart(this.container.nativeElement, this.stockConfig);
    } else {
      this.chart = this.highcharts.chart(this.container.nativeElement, this.config);
    }
    // if we have an updater$ observable, subscribe to it
    if (this.updater) {
      this.subscription = this.updater.subscribe({
        next: this.onOptionsChartUpdate.bind(this)
      });
    }
  }

  onOptionsChartUpdate(updates) {
    if (!isArray(updates)) {
      if (updates.export) {
        this.onExport();
      }
    } else {
      forEach(updates, updateObj => {
        set(this.config, updateObj.path, updateObj.data);
      });

      // Not using chart.update due to a bug with navigation
      // update and bar styles.
      if (this.isstockchart) {
        this.chart = this.highcharts.stockChart(this.container.nativeElement, this.stockConfig);
      } else {
        this.chart = this.highcharts.chart(this.container.nativeElement, this.config);
      }
      if (!isUndefined(this.config.xAxis)) {
        this.config.xAxis.categories = [];
      }
      
      const pieNegatives = this.pieHasNegatives();
      if (pieNegatives.all) {
        // do nothing
      } else if (pieNegatives.some) {
        // do nothing
      }
    }
  }

  /* Checks if the chart type is pie and whether the series has negative values.
       This is necessary because pie chart can't display negative values correctly,
       leading to all sorts of problems if not handled explicitly.

       Returns {all: Boolean, some: Boolean} depending on whether all values in all
       series are negative or only some of them.
    */
  pieHasNegatives() {
    const result = { all: true, some: false };
    if (get(this.config, 'chart.type') !== 'pie') {
      result.all = false;
      return result;
    }

    const series = get(this.config, 'series', []) || [];

    forEach(series, pie => {
      if (!isArray(pie.data)) {
        return;
      }

      const positives = filter(pie.data, slice => slice.y > 0);

      if (positives.length === pie.data.length) {
        result.all = false;
        result.some = result.some || false;
      } else if (positives.length === 0) {
        result.all = result.all && true;
        result.some = true;
      } else {
        result.all = false;
        result.some = true;
      }
    });

    return result;
  }

  onExport() {
    this.chart.exportChartLocal({
      type: 'application/pdf',
      filename: 'chart'
    });
  }
}
