import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Store } from '@ngxs/store';
import {
  Analysis,
  ArtifactColumns,
  AnalysisDSL,
  isDSLAnalysis
} from '../types';
import { DesignerService } from '../designer.service';
import {
  flattenPivotData,
  flattenChartData,
  flattenReportData
} from '../../../../common/utils/dataFlattener';
import { DesignerStates } from '../consts';

import * as isEmpty from 'lodash/isEmpty';
import * as orderBy from 'lodash/orderBy';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import { Observable } from 'rxjs';

@Component({
  selector: 'designer-preview-dialog',
  templateUrl: './designer-preview-dialog.component.html',
  styleUrls: ['./designer-preview-dialog.component.scss']
})
export class DesignerPreviewDialogComponent implements OnInit {
  public previewData = null;
  public artifactColumns: ArtifactColumns;
  public analysis: Analysis | AnalysisDSL;
  public chartType: string;
  public state = DesignerStates.SELECTION_WITH_DATA;
  public dataLoader: (options: {}) => Promise<{
    data: any[];
    totalCount: number;
  }>;

  constructor(
    public _dialogRef: MatDialogRef<DesignerPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { analysis: Analysis },
    public _designerService: DesignerService,
    private _store: Store
  ) {
    this.analysis = data.analysis;
    this.chartType =
      this.analysis['chartType'] ||
      get(this.analysis, 'chartOptions.chartType');
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      this.artifactColumns = get(this.analysis, 'sipQuery.artifacts[0].fields');
      break;
    case 'report':
    case 'esReport':
      let execId: string;
      this.dataLoader = (options = {}) => {
        if (execId) {
          return this._designerService.getDataForExecution(
            this.analysis.id,
            execId,
            {...options, analysisType: this.analysis.type, executionType: 'onetime', isDSL: isDSLAnalysis(this.analysis)}
          )
            .then((result) => ({data: flattenReportData(result.data, this.analysis), totalCount: result.count}));
        } else {
          return this._designerService
            .getDataForAnalysisPreview(this.analysis, options)
            .then(({ data: previewData, executionId, count }) => {
              execId = executionId;
              return { data: flattenReportData(previewData, this.analysis), totalCount: count };
            });
        }
      };
      break;
    }
  }

  get metricName(): Observable<string> {
    return this._store.select(state => state.designerState.metric.metricName);
  }

  get analysisSorts() {
    return isDSLAnalysis(this.analysis)
      ? this.analysis.sipQuery.sorts
      : this.analysis.sqlBuilder.sorts;
  }

  get analysisArtifacts() {
    return this.analysis.edit
      ? null
      : isDSLAnalysis(this.analysis)
      ? this.analysis.sipQuery.artifacts
      : this.analysis.artifacts;
  }

  ngOnInit() {
    const analysis = this.data.analysis;
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
    case 'chart':
    case 'map':
      this._designerService.getDataForAnalysisPreview(analysis, {})
        .then(data => {
          this.previewData = this.flattenData(data.data, analysis);
        });
      break;
    }
  }

  flattenData(data, analysis: Analysis | AnalysisDSL) {
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
    return flattenPivotData(data, (<AnalysisDSL>analysis).sipQuery || (<Analysis>analysis).sqlBuilder);
    case 'report':
    case 'esReport':
      return data;
    case 'chart':
    case 'map':
      let chartData = flattenChartData(
        data,
        (<AnalysisDSL>analysis).sipQuery || (<Analysis>analysis).sqlBuilder
      );

      /* Order chart data manually. Backend doesn't sort chart data. */
      const sorts = get(this.analysis, 'sqlBuilder.sorts', get(this.analysis, 'sipQuery.sorts', []));
      if (!isEmpty(sorts)) {
        chartData = orderBy(
          chartData,
          map(sorts, 'columnName'),
          map(sorts, 'order')
        );
      }

      return chartData;
    }
  }

  close() {
    this._dialogRef.close();
  }
}
