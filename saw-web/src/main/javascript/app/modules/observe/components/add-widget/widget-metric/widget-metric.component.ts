import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';

import { DATE_TYPES } from '../../../../../common/consts';

const template = require('./widget-metric.component.html');
require('./widget-metric.component.scss');

import { AnalyzeService } from '../../../../analyze/services/analyze.service';
import { ObserveService } from '../../../services/observe.service';

@Component({
  selector: 'widget-metric',
  template
})
export class WidgetMetricComponent implements OnInit {
  @Output() onSelect = new EventEmitter();

  metrics: Array<any>;
  showProgress = false;

  constructor(
    private analyze: AnalyzeService,
    private observe: ObserveService
  ) {}

  ngOnInit() {
    this.showProgress = true;
    Observable.fromPromise(this.analyze.getSemanticLayerData()).subscribe(
      (data: Array<any>) => {
        this.metrics = data;
        this.showProgress = false;
      },
      error => {
        this.showProgress = false;
      }
    );
  }

  onLoadMetricArtifacts(semanticId: string) {
    const metric = find(this.metrics, m => m.id === semanticId);
    if (!metric || metric.kpiColumns) return;

    this.showProgress = true;
    this.observe.createKPI({ semanticId }).subscribe(
      data => {
        this.applyArtifactsToMetric(metric, data);
        this.showProgress = false;
      },
      error => {
        this.showProgress = false;
      }
    );
  }

  applyArtifactsToMetric(metric, metricData) {
    metric.kpiColumns = flatMap(metricData.artifacts, table => {
      return filter(
        table.columns,
        col => col.kpiEligible && !DATE_TYPES.includes(col.type)
      );
    });

    metric.dateColumns = flatMap(metricData.artifacts, table => {
      return filter(
        table.columns,
        col => col.kpiEligible && DATE_TYPES.includes(col.type)
      );
    });

    metric.kpiEligible =
      metric.kpiColumns.length > 0 && metric.dateColumns.length > 0;
  }

  onSelectMetricColumn(column, metric) {
    this.onSelect.emit({
      column,
      metric
    });
  }
}
