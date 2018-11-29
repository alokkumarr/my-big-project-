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

import {
  globalChartOptions,
  geoChartOptions
} from './default-chart-options';

interface IChartUpdate {
  path: string;
  data: any;
}

interface IChartAction {
  name: 'export';
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
      updater.subscribe(this.onOptionsUpdate.bind(this));
    }
  }
  @Input()
  set actionBus(actionBus: Subject<IChartAction>) {
    this._actionBus = actionBus;
    if (isObservable(actionBus)) {
      actionBus.subscribe(this.onAction.bind(this));
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
  public chart: any = null;
  public _updater: Subject<IChartUpdate>;
  public _actionBus: Subject<IChartAction>;
  public _options: any;
  public config: any = {};
  public chartSettingsType: string;

  constructor() {
    this.highmaps.setOptions(globalChartOptions);
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
    this.chart.update(update, true, true);
  }

  onAction(action: IChartAction) {
    switch (action.name) {
      case 'export':
        this.onExport();
      break;
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
