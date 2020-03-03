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
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as isNil from 'lodash/isNil';
import * as forEach from 'lodash/forEach';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as isEqual from 'lodash/isEqual';
import * as some from 'lodash/some';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isArray from 'lodash/isArray';
import * as isUndefined from 'lodash/isUndefined';

import {
  globalChartOptions,
  chartOptions,
  stockChartOptions,
  bulletChartOptions
} from './default-chart-options';
import { MatCheckboxChange } from '@angular/material';

export const UPDATE_PATHS = {
  SERIES: 'series.0',
  X_AXIS: 'xAxis'
};

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'chart',
  templateUrl: './chart.component.html'
})
export class ChartComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input()
  updater: any;
  @Input() chartType: string;
  @Input()
  enableExport: boolean;
  @ViewChild('container', { static: true })
  container: ElementRef;

  comparisonConfig: {
    categories: Array<{ name: string; checked: boolean }>;
    series: Array<any>;
  } = {
    categories: null,
    series: null
  };
  /**
   * Cloning all the options to create a new copy per object.
   * Otherwise, any changes to these options will be shared between
   * all instances of chart.
   *
   * @memberof ChartComponent
   */
  chartSettings = {
    default: cloneDeep(chartOptions),
    highStock: cloneDeep(stockChartOptions),
    bullet: cloneDeep(bulletChartOptions)
  };

  public highcharts: any = Highcharts;
  public highstocks: any = Highstock;
  public chart: any = null;
  public _options: any;
  public config: any = {};
  public subscription: any;

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
    if (isUndefined(chartType)) {
      return;
    }
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
    const chartSettingsType = this.getChartSettingsType(this.chartType);
    const chartSettings =
      this.chartSettings[chartSettingsType] || cloneDeep(chartOptions);
    this.config = defaultsDeep({}, options, this.config, chartSettings);

    if (this.enableExport) {
      this.config.exporting = {
        enabled: true
      };
    }
  }

  ngOnDestroy() {
    this.subscription && this.subscription.unsubscribe();
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
      const { chartWidth, chartHeight } = this.chart;
      set(config, 'exporting.sourceWidth', chartWidth);
      set(config, 'exporting.sourceHeight', chartHeight);

      this.chart.update(
        {
          exporting: {
            sourceWidth: chartWidth,
            sourceHeight: chartHeight
          }
        },
        false
      );
    }, 100);
  }

  /**
   * Save categories and series for comparison chart. They are used as
   * caches to support turning on/off data points from inline filters.
   *
   * @param {{ path: string; data: any }} updateObj
   * @memberof ChartComponent
   */
  saveComparisonConfig(updateObj: { path: string; data: any }) {
    if (
      this.chartType === 'comparison' &&
      updateObj.path === 'xAxis.categories'
    ) {
      /* If categories already exist, and they're same as the new update, no
      need to change them. This preserves any categories that have been turned off */
      this.comparisonConfig.categories = isEqual(
        fpMap(cat => cat.name, this.comparisonConfig.categories),
        updateObj.data
      )
        ? this.comparisonConfig.categories
        : fpMap(
            category => ({
              name: category,
              checked: true
            }),
            updateObj.data
          );

      set(
        this.config,
        updateObj.path,
        fpPipe(
          fpFilter(cat => cat.checked),
          fpMap(cat => cat.name)
        )(this.comparisonConfig.categories)
      );
    } else if (this.chartType === 'comparison' && updateObj.path === 'series') {
      this.comparisonConfig.series = updateObj.data;
    }
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
      this.saveComparisonConfig(updateObj);
    });

    // Not using chart.update due to a bug with navigation
    // update and bar styles.
    const chartSettingsType = this.getChartSettingsType(this.chartType);
    const defaultConfig =
      this.chartSettings[chartSettingsType] || cloneDeep(chartOptions);
    const config = defaultsDeep({}, this.config, defaultConfig);
    switch (chartSettingsType) {
      case 'highStock':
        // Highstocks adding a default xAxis settings objects with title & categories.
        set(config, 'xAxis.0.title.text', get(config, 'xAxis.title.text'));
        // So have to populate them inorder the title to display.
        set(config, 'xAxis.0.categories', get(config, 'xAxis.categories'));

        // Fix --- Highstocks API manipulating external config object, setting series and categories data to NULL
        // https://forum.highcharts.com/highstock-usage/creating-a-chart-manipulates-external-options-object-t15255/#p81794

        this.addExportConfig(config);
        this.chart = this.highstocks.stockChart(
          this.container.nativeElement,
          config
        );
        break;
      default:
        const shouldSetColumnStackingToPercent = some(
          this.config.series,
          ({ aggregate }) => aggregate === 'percentagebyrow'
        );
        if (shouldSetColumnStackingToPercent) {
          set(config, 'plotOptions.column.stacking', 'percent');
        }
        this.addExportConfig(config);
        this.chart = this.highcharts.chart(
          this.container.nativeElement,
          config
        );
    }

    this.onCategoryToggle(null, null);
    this.addExportSize(config);

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

      const positives = fpFilter(slice => slice.y > 0, pie.data);

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

  /**
   * Handles data on chart when user chooses to turn off/on categories
   *
   * @param {MatCheckboxChange} event
   * @param {number} index
   * @memberof ChartComponent
   */
  onCategoryToggle(event: MatCheckboxChange, index: number) {
    /* Save the toggle status in category cache first */
    if (!isNil(index) && isArray(this.comparisonConfig.categories)) {
      this.comparisonConfig.categories[index].checked = event.checked;
    }

    /* Map of all categories' status. Looks like [true, false, true]
     if there are three categories. */
    const allCategoryStatus = fpMap(
      category => category.checked,
      this.comparisonConfig.categories
    );

    /* Map of all active categories' names. Doesn't include those that
    user has turned off */
    const activeCategories = fpPipe(
      fpFilter(category => category.checked),
      fpMap(category => category.name)
    )(this.comparisonConfig.categories);

    this.chart.xAxis[0].setCategories(activeCategories);

    forEach(this.comparisonConfig.series, (series, i) => {
      let data = cloneDeep(series.data);
      data = fpPipe(
        fpOrderBy(dataPoint => dataPoint.x),
        fpFilter(dataPoint => allCategoryStatus[dataPoint.x]),
        /* In each data row, replace category index with actual names */
        fpMap(dataPoint => ({
          x: this.comparisonConfig.categories[dataPoint.x].name,
          y: dataPoint.y
        })),
        /* Then search for new index from active categories and use that index */
        fpMap(dataPoint => ({
          x: activeCategories.indexOf(dataPoint.x),
          y: dataPoint.y
        }))
      )(data);

      this.chart.series[i].setData(data);
    });
  }
}
