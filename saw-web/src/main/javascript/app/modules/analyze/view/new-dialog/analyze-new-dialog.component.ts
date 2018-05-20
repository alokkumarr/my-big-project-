import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpReduce from 'lodash/fp/reduce';
import * as forEach from 'lodash/forEach';

import {AnalyseTypes, ANALYSIS_METHODS, ENTRY_MODES} from '../../consts';
import {IAnalysisMethod, AnalysisType, ChartType} from '../../types';
import { AnalyzeDialogService } from '../../services/analyze-dialog.service'

const template = require('./analyze-new-dialog.component.html');
require('./analyze-new-dialog.component.scss');

enum METHODS {
  ES_REPORT = 'table:esReport',
  REPORT = 'table:report',
  PIVOT = 'table:peport',
  CHART_COLUMN= 'chart:column',
  CHART_BAR = 'chart:bar',
  CHART_STACK = 'chart:stack',
  CHART_LINE = 'chart:line',
  CHART_DONUT = 'chart:donut',
  CHART_SCATTER = 'chart:scatter',
  CHART_BUBBLE = 'chart:bubble',
  CHART_AREA = 'chart:area',
  CHART_COMBO = 'chart:combo',
  CHART_TSSPLINE = 'chart:tsspline',
  CHART_TSPANE = 'chart:tsPane'
}

@Component({
  selector: 'analyze-new-dialog',
  template
})
export class AnalyzeNewDialogComponent {

  methodCategories = ANALYSIS_METHODS;
  selectedMethod: IAnalysisMethod;
  selectedMetric;

  constructor(
    private _analyzeDialogService : AnalyzeDialogService,
    @Inject('$mdDialog') private _$mdDialog: any,
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
    const supportMap = fpPipe(
      fpFlatMap(support => support.children),
      fpReduce((accumulator, method) => {
        if (method.supportedTypes) {
          forEach(method.supportedTypes, type => {
            accumulator[type] = true;
          });
        } else {
          accumulator[method.type] = true;
        }
        return accumulator;
      },{})
    )(metric.supports);

    forEach(this.methodCategories, category => {
      forEach(category.children, method => {
        method.disabled = !supportMap[method.type];
      });
    });

    this.selectedMethod = null;
  }

  createButtonDisabled() {
    return !this.selectedMethod || !this.selectedMetric;
  }

  createAnalysis() {
    let tpl;
    let model;
    let type;
    const semanticId = this.selectedMetric.id;
    const metricName = this.selectedMetric.metricName;
    const mode = ENTRY_MODES.NEW;

    switch (this.selectedMethod.type) {
    /* eslint-disable no-fallthrough */
    case 'table:esReport':
    case 'table:report':
    case 'table:pivot':
      this.openUpgradedModal();
      return;
    default:
      type = this.selectedMethod.type.split(':')[1];
      tpl = `<analyze-chart model="model" mode="${mode}"></analyze-chart>`;
      model = {
        type: AnalyseTypes.Chart,
        chartType: type,
        name: 'Untitled Chart',
        metricName,
        semanticId,
        description: '',
        categoryId: this.data.id,
        scheduled: null
      };
      break;
    }
    this._$mdDialog.show({
      template: tpl,
      controller: scope => {
        scope.model = model;
      },
      controllerAs: '$ctrl',
      autoWrap: false,
      focusOnOpen: false,
      multiple: true,
      clickOutsideToClose: true,
      hasBackdrop: false
    }).then(successfullySaved => {
      if (successfullySaved) {
        this._dialogRef.close(successfullySaved);
      }
    });
  }

  openUpgradedModal() {
    const semanticId = this.selectedMetric.id;
    const metricName = this.selectedMetric.metricName;
    const method = this.selectedMethod.type.split(':');
    const isChartType = method[0] === 'chart';
    const type = <AnalysisType>(isChartType ? method[0] : method[1]);
    const chartType = <ChartType>(isChartType ? method[1] : null);
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
