import {
  Component,
  OnInit,
  AfterViewInit,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { ChartService } from '../../../../../../common/services/chart.service';
import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'designer-chart-options',
  templateUrl: 'designer-chart-options.component.html',
  styleUrls: ['designer-chart-options.component.scss']
})
export class DesignerChartOptionsComponent implements OnInit, AfterViewInit {
  @Input('chartType') set setChartType(chartType: string) {
    this.chartType = chartType;
    this.showLegendOpts = chartType !== 'pie';
    this.showLabelOpts = chartType === 'pie';
    this.showInversion = [
      'column',
      'bar',
      'line',
      'area',
      'combo',
      'stack'
    ].includes(chartType);
  }
  @Input() isInverted: boolean;
  @Input() chartTitle: string;

  @Output() change = new EventEmitter();
  showLegendOpts = false;
  showLabelOpts = false;
  showInversion = false;
  chartType: string;

  @Input() labelOptions: { enabled: boolean; value: string };

  @Input('legend')
  set analysisLegend(data: any) {
    if (!data) {
      return;
    }
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
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onLegendChange();
    }, 50);
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

  onTitleChange() {
    this.change.emit({
      subject: 'chartTitle',
      data: {
        title: this.chartTitle || ''
      }
    });
  }
}
