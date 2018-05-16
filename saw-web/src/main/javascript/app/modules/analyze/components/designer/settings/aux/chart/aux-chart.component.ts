import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { ChartService } from '../../../../../services/chart.service';

const template = require('./aux-chart.component.html');
require('./aux-chart.component.scss');

const INVERTING_OPTIONS = [
  {
    label: 'TOOLTIP_INVERTED',
    type: true,
    icon: { font: 'icon-hor-bar-chart' }
  },
  {
    label: 'TOOLTIP_NOT_INVERTED',
    type: false,
    icon: { font: 'icon-vert-bar-chart' }
  }
];

@Component({
  selector: 'designer-settings-aux-chart',
  template
})
export class DesignerSettingsAuxChartComponent implements OnInit {
  @Input() chartType: string;
  @Input() isInverted: boolean;

  @Output() change = new EventEmitter();

  showLegendOpts: boolean;
  showLabelOpts: boolean = true;
  showInversion: boolean;

  @Input() labelOptions: { enabled: boolean; value: string };

  @Input('legend')
  set analysisLegend(data: any) {
    if (!data) return;

    this.legend = this.legend || {};
    this.legend.align = data.align;
    this.legend.layout = data.layout;
  }

  legend: any;

  constructor(private chartService: ChartService) {}

  ngOnInit() {
    this.legend = this.chartService.initLegend({
      ...(this.legend ? { legend: this.legend } : {}),
      chartType: this.chartType
    });
    this.showLegendOpts = this.chartType !== 'pie';
    this.showLabelOpts = this.chartType === 'pie';
    this.showInversion = [
      'column',
      'bar',
      'line',
      'area',
      'combo',
      'stack'
    ].includes(this.chartType);
  }

  setInversion(isInverted) {
    this.isInverted = isInverted;
    this.change.emit({
      subject: 'inversion',
      data: { isInverted }
    });
  }

  onLabelOptsChange(evt) {
    this.change.emit({
      subject: 'labelOptions',
      data: {
        labelOptions: {
          enabled: this.labelOptions.enabled,
          value: this.labelOptions.value
        }
      }
    });
  }

  onLegendChange() {
    this.change.emit({
      subject: 'legend',
      data: {
        legend: {
          align: this.legend.align,
          layout: this.legend.layout
        }
      }
    });
  }
}
