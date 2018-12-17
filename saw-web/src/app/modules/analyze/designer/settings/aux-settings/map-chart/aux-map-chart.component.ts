import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';

import { ChartService } from '../../../../../../common/services/chart.service';

@Component({
  selector: 'designer-settings-aux-map-chart',
  templateUrl: 'aux-map-chart.component.html',
  styleUrls: ['aux-map-chart.component.scss']
})

export class DesignerSettingsAuxMapChartComponent implements OnInit {

  @Input() chartTitle;
  @Input() chartType: string;
  @Output() change = new EventEmitter();

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
  public editMode: false;

  constructor(private _chartService: ChartService) { }

  ngOnInit() {
    this.legend = {
      align: 'top',
      layout: 'horizontal',
      options: {
        align: values(this._chartService.LEGEND_POSITIONING),
        layout: values(this._chartService.LAYOUT_POSITIONS)
      }
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
      this.change.emit({
        subject: 'chartTitle',
        data: {
          title: this.chartTitle
        }
      });
    }
  }
}
