import { Component, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { from } from 'rxjs';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';

import { guid } from '../../../../../common/utils/guid';
import { DATE_TYPES } from '../../../../../common/consts';
import { AnalyzeService } from '../../../../analyze/services/analyze.service';
import { ObserveService } from '../../../services/observe.service';
import { HeaderProgressService } from '../../../../../common/services';

const style = require('./widget-metric.component.scss');

@Component({
  selector: 'widget-metric',
  templateUrl: './widget-metric.component.html',
  styles: [
    `:host {
      display: block;
    }`,
    style
  ]
})
export class WidgetMetricComponent implements OnInit, OnDestroy {
  @Output() onSelect = new EventEmitter();
  progressSub;
  metrics: Array<any> = [];
  showProgress = false;

  constructor(
    private analyze: AnalyzeService,
    private observe: ObserveService,
    private _headerProgress: HeaderProgressService
  ) {
    this. progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
  }

  ngOnInit() {
    from(this.analyze.getSemanticLayerData()).subscribe(
      (data: Array<any>) => {
        this.metrics = data;
      }
    );
  }

  ngOnDestroy() {
    this.progressSub.unsubscribe();
  }

  onLoadMetricArtifacts(semanticId: string) {
    const metric = find(this.metrics, m => m.id === semanticId);
    if (!metric || metric.kpiColumns) { return; }

    this.observe.getArtifacts({ semanticId }).subscribe(
      data => {
        this.applyArtifactsToMetric(metric, data);
      }
    );
  }

  applyArtifactsToMetric(metric, metricData) {
    metric.artifacts = metricData.artifacts;
    metric.esRepository = metricData.esRepository;
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
      metric,
      kpi: {
        id: guid(),
        name: column.displayName,
        tableName: column.table || column.tableName,
        semanticId: metric.id,
        dataFields: [
          {
            columnName: column.columnName,
            name: column.columnName,
            displayName: column.displayName,
            aggregate: []
          }
        ],
        filters: [],
        esRepository: metric.esRepository
      }
    });
  }
}
