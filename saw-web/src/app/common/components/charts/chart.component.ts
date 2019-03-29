import {
  Component,
  ElementRef,
  Input,
  ViewChild,
  OnInit,
  AfterViewInit,
  OnDestroy
} from '@angular/core';

import * as Highcharts from 'highcharts/highcharts';
import * as Highstock from 'highcharts/highstock';
// Had to import both highstocks & highcharts api since highstocks not supporting bubble chart.
import * as defaultsDeep from 'lodash/defaultsDeep';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as clone from 'lodash/clone';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isArray from 'lodash/isArray';
import * as find from 'lodash/find';

import {
  globalChartOptions,
  chartOptions,
  stockChartOptions,
  bulletChartOptions
} from './default-chart-options';

export const UPDATE_PATHS = {
  SERIES: 'series.0',
  X_AXIS: 'xAxis'
};

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'chart',
  template: `
    <div style="width: 100%;" #container></div>
  `
})
export class ChartComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input()
  updater: any;
  @Input() chartType: string;
  @Input()
  enableExport: boolean;
  @ViewChild('container')
  container: ElementRef;

  /**
   * Cloning all the options to create a new copy per object.
   * Otherwise, any changes to these options will be shared between
   * all instances of chart.
   *
   * @memberof ChartComponent
   */
  chartSettings = [
    { type: 'default', config: cloneDeep(chartOptions) },
    { type: 'highStock', config: cloneDeep(stockChartOptions) },
    { type: 'bullet', config: cloneDeep(bulletChartOptions) }
  ];

  public highcharts: any = Highcharts;
  public highstocks: any = Highstock;
  public chart: any = null;
  public _options: any;
  public config: any = {};
  public subscription: any;
  public clonedConfig: any = {};
  public chartSettingsType: string;

  constructor() {
    this.highcharts.setOptions(cloneDeep(globalChartOptions));
    this.highstocks.setOptions(cloneDeep(globalChartOptions));
  }

  @Input()
  set options(data) {
    this._options = data;
    this.updateOptions(this._options);
  }

  ngAfterViewInit() {
    if (this.enableExport) {
      this.enableExporting(this.config);
    }
    setTimeout(() => {
      this.reflow();
    });
  }

  ngOnInit() {
    this.updateOptions(this._options);
    // if we have an updater$ observable, subscribe to it
    if (this.updater) {
      this.subscription = this.updater.subscribe({
        next: this.onOptionsChartUpdate.bind(this)
      });
    }
  }

  getChartSettingsType(chartType) {
    if (chartType.substring(0, 2) === 'ts') {
      return 'highStock';
    }
    if (chartType === 'bullet') {
      return 'bullet';
    }
    return 'default';
  }

  updateOptions(options) {
    if (!options) {
      return;
    }
    // set the appropriate config based on chart type
    this.chartSettingsType = this.getChartSettingsType(this.chartType);
    this.config = defaultsDeep(
      {},
      options,
      this.config,
      get(
        find(this.chartSettings, ['type', this.chartSettingsType]),
        'config',
        cloneDeep(chartOptions)
      )
    );
    if (this.enableExport) {
      this.config.exporting = {
        enabled: true
      };
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  /**
   * Enables exporting features for the chart.
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
   */
  addExportConfig(config) {
    set(
      config,
      'exporting.filename',
      get(config, 'title.exportFilename') || 'chart'
    );
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
   * want to calculate the chart size after it has been drawn, not before it
   */
  addExportSize(config) {
    setTimeout(() => {
      set(config, 'exporting.sourceWidth', this.chart.chartWidth);
      set(config, 'exporting.sourceHeight', this.chart.chartHeight);

      this.chart.update(
        {
          exporting: {
            sourceHeight: this.chart.chartHeight,
            sourceWidth: this.chart.chartWidth
          }
        },
        false
      );
    }, 100);
  }

  reflow() {
    if (this.chart) {
      this.chart.reflow();
    }
  }

  onOptionsChartUpdate(updates) {
    if (!isArray(updates)) {
      if (updates.export) {
        this.onExport();
      }
      if (updates.reflow) {
        this.reflow();
      }
      return;
    }

    forEach(updates, updateObj => {
      set(this.config, updateObj.path, updateObj.data);
    });

    // Not using chart.update due to a bug with navigation
    // update and bar styles.
    const chartSettingsType = this.getChartSettingsType(this.chartType);
    switch (chartSettingsType) {
      case 'highStock':
        set(
          this.config,
          'xAxis.0.title.text',
          get(this.config, 'xAxis.title.text')
        ); // Highstocks adding a default xAxis settings objects with title & categories.
        // So have to populate them inorder the title to display.
        set(
          this.config,
          'xAxis.0.categories',
          get(this.config, 'xAxis.categories')
        );

        // Fix --- Highstocks API manipulating external config object, setting series and categories data to NULL
        // https://forum.highcharts.com/highstock-usage/creating-a-chart-manipulates-external-options-object-t15255/#p81794
        this.clonedConfig = clone(this.config);
        this.addExportConfig(this.config);
        this.chart = this.highstocks.stockChart(
          this.container.nativeElement,
          this.config
        );
        this.config = clone(this.clonedConfig);
        this.addExportSize(this.config);
        this.clonedConfig = {};
        break;
      default:
        this.addExportConfig(this.config);
        const requestConfig = cloneDeep(this.config);
        forEach(this.config.series, seriesOptions => {
          if (['percentageByRow'].includes(seriesOptions.aggregate)) {
            set(requestConfig, 'plotOptions.column.stacking', 'percent');
          }
        });
        this.chart = this.highcharts.chart(
          this.container.nativeElement,
          requestConfig
        );
        this.addExportSize(requestConfig);
    }

    // This is causing more problems than it solves. Updating the defaultsDeep
    // call in ngAfterViewInit callback. Hopefully this isn't needed anymore.
    // if (!isUndefined(this.config.xAxis)) {
    //   this.config.xAxis.categories = [];
    // }

    const pieNegatives = this.pieHasNegatives();
    if (pieNegatives.all) {
      // do nothing
    } else if (pieNegatives.some) {
      // do nothing
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
      filename: this.config.title.exportFilename || 'chart'
    });
  }
}
