import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Analysis, ArtifactColumns } from '../types';
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

@Component({
  selector: 'designer-preview-dialog',
  templateUrl: './designer-preview-dialog.component.html',
  styleUrls: ['./designer-preview-dialog.component.scss']
})
export class DesignerPreviewDialogComponent implements OnInit {
  public previewData = null;
  public artifactColumns: ArtifactColumns;
  public analysis: Analysis;
  public state = DesignerStates.SELECTION_WITH_DATA;
  public dataLoader: (
    options: {}
  ) => Promise<{ data: any[]; totalCount: number }>;

  constructor(
    public _dialogRef: MatDialogRef<DesignerPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { analysis: Analysis },
    public _designerService: DesignerService
  ) {
    this.analysis = data.analysis;
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      this.artifactColumns = this.analysis.artifacts[0].columns;
      break;
    case 'report':
    case 'esReport':
      let execId: string;
      this.dataLoader = (options = {}) => {
        if (execId) {
          return this._designerService.getDataForExecution(
            this.analysis.id,
            execId,
            {...options, analysisType: this.analysis.type, executionType: 'onetime'}
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

  ngOnInit() {
    const analysis = this.data.analysis;
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
    case 'chart':
      this._designerService.getDataForAnalysisPreview(analysis, {})
        .then(data => {
          this.previewData = this.flattenData(data.data, analysis);
        });
      break;
    }
  }

  flattenData(data, analysis: Analysis) {
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
      return flattenPivotData(data, analysis.sqlBuilder);
    case 'report':
    case 'esReport':
      return data;
    case 'chart':
      let chartData = flattenChartData(
        data,
        analysis.sqlBuilder
      );

      /* Order chart data manually. Backend doesn't sort chart data. */
      const sorts = get(this.analysis, 'sqlBuilder.sorts', []);
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
