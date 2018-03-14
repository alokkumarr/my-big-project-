import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';

const template = require('./widget-analysis.component.html');
require('./widget-analysis.component.scss');

import { AnalyzeService } from '../../../../analyze/services/analyze.service';
import { ANALYSIS_METHODS } from '../../../../analyze/consts';

export enum WIDGET_ANALYSIS_ACTIONS {
  ADD_ANALYSIS,
  REMOVE_ANALYSIS
}
const ALLOWED_ANALYSIS_TYPES = ['chart'];

@Component({
  selector: 'widget-analysis',
  template
})
export class WidgetAnalysisComponent implements OnInit {
  @Output() onAnalysisAction = new EventEmitter();
  analyses: Array<any> = [];
  showProgress = false;
  searchTerm: string;
  icons = {};

  constructor(private analyze: AnalyzeService) {
    this.loadIcons();
  }

  ngOnInit() { }

  loadIcons() {
    const chartTypes = find(ANALYSIS_METHODS, method => method.label === 'CHARTS');
    forEach(chartTypes.children, chart => {
      this.icons[chart.type.split(':')[1]] = chart.icon.font;
    });
  }

  @Input() set category(id: number | string) {
    this.showProgress = true;
    this.analyze.getAnalysesFor(id.toString()).then(result => {
      this.analyses = filter(result, analysis => analysis && ALLOWED_ANALYSIS_TYPES.includes(analysis.type));
      this.showProgress = false;
    }, () => {
      this.showProgress = false;
    });
  }

  sendAnalysisAction(action, analysis) {
    this.onAnalysisAction.next({
      action, analysis
    });
  }

  addAnalysis (analysis) {
    this.sendAnalysisAction(WIDGET_ANALYSIS_ACTIONS.ADD_ANALYSIS, analysis);
  }

  removeAnalysis (analysis) {
    this.sendAnalysisAction(WIDGET_ANALYSIS_ACTIONS.REMOVE_ANALYSIS, analysis);
  }
}
