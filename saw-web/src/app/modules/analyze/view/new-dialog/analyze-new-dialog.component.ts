import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as get from 'lodash/get';
import * as some from 'lodash/some';
import * as startsWith from 'lodash/startsWith';
import * as fpValues from 'lodash/values';
import * as startCase from 'lodash/startCase';
import * as fpFilter from 'lodash/fp/filter';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpReduce from 'lodash/fp/reduce';

import { ANALYSIS_METHODS, DATAPOD_CATEGORIES_OBJ } from '../../consts';
import { IAnalysisMethod } from '../../types';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { FilterPipe } from '../../../../common/pipes/filter.pipe';

@Component({
  selector: 'analyze-new-dialog',
  templateUrl: './analyze-new-dialog.component.html',
  styleUrls: ['./analyze-new-dialog.component.scss'],
  providers: [FilterPipe]
})
export class AnalyzeNewDialogComponent {
  methodCategories = ANALYSIS_METHODS;
  supportedMetricCategories: Array<any> = [];
  selectedMethod: IAnalysisMethod;
  searchMetric: '';
  selectedMetric;
  private _sortOrder = 'asc';

  @ViewChild('newAnalysisStepper') stepper: MatHorizontalStepper;

  constructor(
    public _analyzeDialogService: AnalyzeDialogService,
    public _dialogRef: MatDialogRef<AnalyzeNewDialogComponent>,
    private filterPipe: FilterPipe,
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

  /**
   * searchCount
   * Returns the number of metrics matching the search criteria
   *
   * @param metrics
   * @returns {number}
   */
  searchCount(metrics): number {
    return this.filterPipe.transform(metrics, 'metricName', this.searchMetric)
      .length;
  }

  setSupportedMetrics(method) {
    this._sortOrder = 'asc';

    this.supportedMetricCategories = fpPipe(
      fpFilter(metric => {
        if (startsWith(method.type, 'chart:geo')) {
          const doesSupportsChartMap = some(metric.supports, ({category}) => category === 'mapChart');
          return doesSupportsChartMap;
        }
        const isEsMetric = get(metric, 'esRepository.storageType') === 'ES';
        return isEsMetric || method.type === 'table:report';
      }),
      fpOrderBy(['metricName'], [this._sortOrder]),
      fpReduce((acc, metric) => {
        const category = startCase(metric.category || 'Default');
        acc[category] = acc[category] || {
          label: category,
          metrics: []
        };
        acc[category].metrics.push(metric);
        return acc;
      }, {}),
      fpValues
    )(this.data.metrics);

    this.selectedMetric = null;
    this.searchMetric = '';
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
      metricName
    };
    this._dialogRef.afterClosed().subscribe(() => {
      this._analyzeDialogService.openNewAnalysisDialog(model);
    });
    this._dialogRef.close();
  }

  getCategoryIcon(metricCategory) {
    const name = startCase(metricCategory.label);
    const icon = get(DATAPOD_CATEGORIES_OBJ[name], 'icon');
    return icon || '';
  }

  getMetricCategoryLabel(metricCategory) {
    const metricCount = this.searchCount(metricCategory.metrics);
    const label = metricCategory.label;
    return `${label} (${metricCount})`;
  }
}
