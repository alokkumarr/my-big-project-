import {
  Component,
  Output,
  EventEmitter,
  Input,
  OnInit,
  AfterViewInit
} from '@angular/core';
import { ChartService } from '../../../../../../common/services/chart.service';
import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'designer-settings-aux-chart',
  templateUrl: './aux-chart.component.html',
  styleUrls: ['./aux-chart.component.scss']
})
export class DesignerSettingsAuxChartComponent
  implements OnInit, AfterViewInit {
  @Input() chartType: string;
  @Input() isInverted: boolean;
  @Input() chartTitle: string;

  @Output() change = new EventEmitter();

  showLegendOpts: boolean;
  showLabelOpts = true;
  showInversion: boolean;

  EditMode: false;

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
    if (!isEmpty(this.chartTitle)) {
      this.EditMode = false;
      this.change.emit({
        subject: 'chartTitle',
        data: {
          title: this.chartTitle
        }
      });
    }
  }
}
