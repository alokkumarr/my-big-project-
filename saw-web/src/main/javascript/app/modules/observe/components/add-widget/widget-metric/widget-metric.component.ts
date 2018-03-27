import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import * as map from 'lodash/map';

const template = require('./widget-metric.component.html');
require('./widget-metric.component.scss');

import { AnalyzeService } from '../../../../analyze/services/analyze.service';

@Component({
  selector: 'widget-metric',
  template
})
export class WidgetMetricComponent implements OnInit {
  @Output() onSelect = new EventEmitter();

  metrics: Observable<Array<any>>;

  constructor(private analyze: AnalyzeService) {}

  ngOnInit() {
    this.metrics = Observable.fromPromise(
      this.analyze.getSemanticLayerData()
    ).map((data: Array<any>) => {
      return map(data, metric => {
        metric.kpiColumns = [
          {
            columnName: 'AVAILABLE_MB',
            displayName: 'Available MB',
            type: 'integer'
          }
        ];

        metric.dateColumns = [
          {
            columnName: 'TRANSFER_DATE',
            displayName: 'Transfer Date',
            type: 'date'
          }
        ];

        return metric;
      });
    });
  }

  onSelectMetricColumn(column, metric) {
    this.onSelect.emit({
      column,
      metric
    });
  }
}
