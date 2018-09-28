import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as forEach from 'lodash/forEach';
import * as get from 'lodash/get';

import { ANALYSIS_METHODS } from '../../consts';
import { IAnalysisMethod } from '../../types';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';

const style = require('./analyze-new-dialog.component.scss');

@Component({
  selector: 'analyze-new-dialog',
  templateUrl: './analyze-new-dialog.component.html',
  styles: [
    `:host {
      z-index: 80;
      display: block;
      max-width: 700px;
    }`,
    style
  ]
})
export class AnalyzeNewDialogComponent {

  methodCategories = ANALYSIS_METHODS;
  selectedMethod: IAnalysisMethod;
  selectedMetric;

  constructor(
    private _analyzeDialogService: AnalyzeDialogService,
    private _dialogRef: MatDialogRef<AnalyzeNewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      metrics: any[],
      id: string
    }
  ) {}

  onMetricSelected(metric) {
    this.selectedMetric = metric;
    this.setSupportedMethods(metric);
  }

  onMethodSelected(method) {
    this.selectedMethod = method;
  }

  setSupportedMethods(metric) {
    const metricType = get(metric, 'esRepository.storageType');
    const isEsMetric = metricType === 'ES';

    forEach(this.methodCategories, category => {
      forEach(category.children, method => {
        const enableMethod = isEsMetric ?
          true :
          method.type === 'table:report';

        method.disabled = !enableMethod;
      });
    });

    this.selectedMethod = null;
  }

  createButtonDisabled() {
    return !this.selectedMethod || !this.selectedMetric;
  }

  getAnalysisType(method, metric) {
    const [first, second] = method.type.split(':');
    switch (first) {
    case 'chart':
      return {
        type: first,
        chartType: second
      };
    case 'table':
      // handle esReport edge case
      const metricType = get(metric, 'esRepository.storageType');
      const isEsMetric = metricType === 'ES';
      if (second === 'report' && isEsMetric) {
        return { type: 'esReport' };
      }
      return { type: second };
    }
  }

  createAnalysis() {
    const semanticId = this.selectedMetric.id;
    const metricName = this.selectedMetric.metricName;
    const {type, chartType} = this.getAnalysisType(this.selectedMethod, this.selectedMetric);
    const model = {
      type,
      chartType,
      categoryId: this.data.id,
      semanticId,
      metricName,
      name: 'Untitled Analysis',
      description: '',
      scheduled: null
    };
    this._analyzeDialogService.openNewAnalysisDialog(model)
      .afterClosed().subscribe(successfullySaved => {
        if (successfullySaved) {
          this._dialogRef.close(successfullySaved);
        }
      });
  }
}
