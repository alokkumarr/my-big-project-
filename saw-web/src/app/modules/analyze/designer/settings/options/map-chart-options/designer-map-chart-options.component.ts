import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ChartService } from '../../../../../../common/services/chart.service';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';

@Component({
  selector: 'designer-map-chart-options',
  templateUrl: 'designer-map-chart-options.component.html'
  // styleUrls: ['designer-map-chart-options.component.scss']
})
export class DesignerMapChartOptionsComponent {
  @Input() chartTitle;
  @Input() chartType: string;
  @Output() change = new EventEmitter();

  public options;
  public legend: any = {};
  public editMode: false;

  @Input('legend')
  set analysisLegend(data: any) {
    if (!data) {
      return;
    }
    this.legend.align = data.align;
    this.legend.layout = data.layout;
  }

  constructor(private _chartService: ChartService) {
    this.legend = {
      align: 'bottom',
      layout: 'horizontal'
    };
    this.options = {
      align: values(this._chartService.LEGEND_POSITIONING),
      layout: values(this._chartService.LAYOUT_POSITIONS)
    };
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
      this.editMode = false;
      this.change.emit({
        subject: 'chartTitle',
        data: {
          title: this.chartTitle
        }
      });
    }
  }
}
