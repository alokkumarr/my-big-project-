import {
  Component,
  ElementRef,
  Input,
  ViewChild
} from '@angular/core';

import * as Highcharts from 'highcharts/highcharts';
import * as Highstock from 'highcharts/highstock';    // Had to import both highstocks & highcharts api since highstocks not supporting bubble chart.
import * as defaultsDeep from 'lodash/defaultsDeep';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as clone from 'lodash/clone';
import * as isArray from 'lodash/isArray';
import { globalChartOptions, chartOptions, stockChartOptions } from './default-chart-options';
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
  @Input() isStockChart: boolean;
  @Input() enableExport: boolean;
  @ViewChild('container') container: ElementRef;

  private highcharts: any = Highcharts;
  private highstocks: any = Highstock;
  private chart: any = null;
  private config: any = {};
  private stockConfig: any = {};
  private subscription: any;
  private clonedConfig: any = {};

  constructor() {
    this.highcharts.setOptions(globalChartOptions);
  }

  ngAfterViewInit() {
    this.config = defaultsDeep(this.config, this.options, chartOptions);
    this.stockConfig = defaultsDeep(this.stockConfig, this.options, stockChartOptions);
    this.enableExport && this.enableExporting(this.config);
    this.enableExport && this.enableExporting(this.stockConfig);
    if (this.isStockChart) {
      this.chart = this.highstocks.stockChart(this.container.nativeElement, this.stockConfig);
    } else {
      this.chart = this.highcharts.chart(this.container.nativeElement, this.config);
    }
  }

  ngOnInit() {
    // if we have an updater$ observable, subscribe to it
    if (this.updater) {
      this.subscription = this.updater.subscribe({
        next: this.onOptionsChartUpdate.bind(this)
      });
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  /**
   * Enables exporting features for the chart.
   *
   * @param {any} config
   * @memberof ChartComponent
   */
  enableExporting(config) {
    set(config, 'exporting', {
      enabled: true,
      allowHTML: false,
      fallbackToExportServer: false
    });
  }

  /**
   * Sets the name of download file as chart title.
   *
   * @param {any} config
   * @memberof ChartComponent
   */
  addExportConfig(config) {
    set(config, 'exporting.filename', get(config, 'title.text') || 'chart');
    set(config, 'exporting.chartOptions', {
      legend: {
        navigation: {
          enabled: false
        }
      }
    });
  }

  /**
   * Adds the size of chart to export config. There's a timeout because we
   * want to calculate the chart size after it has been drawn, not before it.
   *
   * @param {any} config
   * @memberof ChartComponent
   */
  addExportSize(config) {
    setTimeout(() => {
      set(config, 'exporting.sourceWidth', this.chart.chartWidth);
      set(config, 'exporting.sourceHeight', this.chart.chartHeight);

      this.chart.update({
        exporting: {
          sourceHeight: this.chart.chartHeight,
          sourceWidth: this.chart.chartWidth
        }
      }, false);

    }, 100);
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
      if (this.isStockChart) {
        set(this.config, 'xAxis.0.title.text', get(this.config, 'xAxis.title.text')); // Highstocks adding a default xAxis settings objects with title & categories. So have to populate them inorder the title to display.
        set(this.config, 'xAxis.0.categories', get(this.config, 'xAxis.categories'));

        // Fix --- Highstocks API manipulating external config object, setting series and categories data to NULL
        // https://forum.highcharts.com/highstock-usage/creating-a-chart-manipulates-external-options-object-t15255/#p81794
        this.clonedConfig = clone(this.config);
        this.addExportConfig(this.config);
        this.chart = this.highstocks.stockChart(this.container.nativeElement, this.config);
        this.config = clone(this.clonedConfig);
        this.addExportSize(this.config);
        this.clonedConfig = {};
      } else {
        this.addExportConfig(this.config);
        this.chart = this.highcharts.chart(this.container.nativeElement, this.config);
        this.addExportSize(this.config);
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
