import {
  Component,
  ElementRef,
  Input,
  ViewChild
} from '@angular/core';
import { Subject, isObservable } from 'rxjs';

import * as Highmaps from 'highcharts/highmaps';
// Had to import both highstocks & highcharts api since highstocks not supporting bubble chart.
import * as defaultsDeep from 'lodash/defaultsDeep';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as isArray from 'lodash/isArray';
import * as reduce from 'lodash/reduce';

import {
  globalChartOptions,
  geoChartOptions
} from './default-chart-options';

export interface IChartUpdate {
  path: string;
  data: any;
}

export interface IChartAction {
  export?: boolean;
}

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'map-chart',
  template: `<div #container></div>`
})
export class MapChartComponent {
  @Input()
  set updater(updater: Subject<IChartUpdate>) {
    this._updater = updater;
    if (isObservable(updater)) {
      updater.subscribe(this.onOptionsUpdate);
    }
  }
  @Input()
  set actionBus(actionBus: Subject<IChartAction>) {
    this._actionBus = actionBus;
    if (isObservable(actionBus)) {
      actionBus.subscribe(this.onAction);
    }
  }
  @Input()
  set options(options) {
    this._options = options;
    this.setOptions(this._options);
  }
  @Input()
  isExportEnabled: boolean;
  @ViewChild('container')
  container: ElementRef;

  public highmaps: any = Highmaps;
  public chart: any;
  public _updater: Subject<IChartUpdate>;
  public _actionBus: Subject<any>;
  public _options: any;
  public config: any = {};
  public chartSettingsType: string;

  constructor() {
    this.highmaps.setOptions(globalChartOptions);
    this.onOptionsUpdate = this.onOptionsUpdate.bind(this);
    this.onAction = this.onAction.bind(this);
  }

  setOptions(options) {
    if (!options) {
      return;
    }
    // set the appropriate config based on chart type
    this.config = defaultsDeep(
      options,
      this.isExportEnabled ? this.getExportConfig() : {},
      geoChartOptions
    );

    this.chart = this.highmaps.mapChart(
      this.container.nativeElement,
      this.config
    );
    this.addExportSize(this.config);
  }

  onOptionsUpdate(update: IChartUpdate) {
    const chartUpdate = this.transformUpdateIfNeeded(update);

    this.delayIfNeeded(!this.chart, () => {
      this.chart.update(chartUpdate, true, true);
      this.chart.reflow();
    }, 0);
  }

  delayIfNeeded(condition, fn, delay) {
    if (condition) {
      setTimeout(() => fn(), delay);
    } else {
      fn();
    }
  }

  /**
   * Trasnform the updates to highcharts config object form, if it comes in array form
   */
  transformUpdateIfNeeded(updates) {
    if (isArray(updates)) {
      return reduce(updates, (acc, update) => {
        set(acc, update.path, update.data);
        return acc;
      }, {});
    }

    return updates;
  }

  onAction(action: IChartAction) {
    if (action && action.export) {
      this.onExport();
    }
  }

  getExportConfig() {
    const filename = 'chart';
    return {
      enabled: true,
      allowHTML: false,
      fallbackToExportServer: false,
      filename,
      chartOptions: {
        legend: {
          navigation: {
            enabled: false
          }
        }
      }
    };
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

  onExport() {
    this.chart.exportChartLocal({
      type: 'application/pdf',
      filename: get(this.config, 'title.exportFilename') || 'chart'
    });
  }
}
