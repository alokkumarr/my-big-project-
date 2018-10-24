import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as forEach from 'lodash/forEach';
import * as get from 'lodash/get';
import * as flatMap from 'lodash/flatMap';
import * as fpFilter from 'lodash/fp/filter';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as fpPipe from 'lodash/fp/pipe';

import { ANALYSIS_METHODS } from '../../consts';
import { IAnalysisMethod } from '../../types';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';
import { MatHorizontalStepper } from '@angular/material/stepper';

@Component({
  selector: 'analyze-new-dialog',
  templateUrl: './analyze-new-dialog.component.html',
  styleUrls: ['./analyze-new-dialog.component.scss']
})
export class AnalyzeNewDialogComponent {
  methodCategories = ANALYSIS_METHODS;
  supportedMetricCategories: Array<any> = [
    {
      label: 'All',
      metrics: []
    }
  ];
  selectedMethod: IAnalysisMethod;
  selectedMetric;
  private _sortOrder = 'asc';

  @ViewChild('newAnalysisStepper') stepper: MatHorizontalStepper;

  constructor(
    public _analyzeDialogService: AnalyzeDialogService,
    public _dialogRef: MatDialogRef<AnalyzeNewDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      metrics: any[];
      id: string;
    }
  ) {}

  onMetricSelected(metric) {
    this.selectedMetric = metric;
  }

  onMethodSelected(method) {
    this.selectedMethod = method.type ? method : null;
    this.setSupportedMetrics(method);
  }

  trackById(index, metric) {
    return metric.id;
  }

  setSupportedMetrics(method) {
    this._sortOrder = 'asc';
    this.supportedMetricCategories[0].metrics = fpPipe(
      fpFilter(metric => {
        const isEsMetric = get(metric, 'esRepository.storageType') === 'ES';
        const supports = flatMap(
          metric.supports,
          category => category.children
        );

        return isEsMetric || method.type === 'table:report';
      }),
      fpOrderBy(['metricName'], [this._sortOrder])
    )(this.data.metrics);
    this.selectedMetric = null;
  }

  nextButtonDisabled() {
    if (this.stepper.selectedIndex === 0) {
      return !this.selectedMethod;
    } else if (this.stepper.selectedIndex === 1) {
      return !this.selectedMethod || !this.selectedMetric;
    }
  }

  toggleSort() {
    this._sortOrder = this._sortOrder === 'asc' ? 'desc' : 'asc';
    this.supportedMetricCategories[0].metrics = fpOrderBy(
      ['metricName'],
      [this._sortOrder],
      this.supportedMetricCategories[0].metrics
    );
  }

  previousStep() {
    this.stepper.previous();
  }

  nextStep() {
    if (this.stepper.selectedIndex === 0) {
      this.stepper.next();
    } else if (!this.nextButtonDisabled()) {
      this.createAnalysis();
    }
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
    const { type, chartType } = this.getAnalysisType(
      this.selectedMethod,
      this.selectedMetric
    );
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
    this._analyzeDialogService
      .openNewAnalysisDialog(model)
      .afterClosed()
      .subscribe(successfullySaved => {
        if (successfullySaved) {
          this._dialogRef.close(successfullySaved);
        }
      });
  }
}
