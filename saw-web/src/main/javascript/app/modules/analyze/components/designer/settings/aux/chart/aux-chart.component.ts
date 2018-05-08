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

  @Output('settingsChange') change = new EventEmitter();

  legend: any;

  showLegendOpts: boolean;
  showInversion: boolean;

  constructor(private chartService: ChartService) {}

  ngOnInit() {
    this.legend = this.chartService.initLegend({ chartType: this.chartType });
    this.showLegendOpts = this.chartType !== 'pie';
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
    this.change.emit({
      isInverted
    });
  }

  onLegendChange() {
    this.change.emit({
      legend: {
        align: this.legend.align,
        layout: this.legend.layout
      }
    });
  }
}
