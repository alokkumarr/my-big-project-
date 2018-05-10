import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnalyzeActionsService } from '../../actions';
import { generateSchedule } from '../../cron';
import { AnalyzeService } from '../../services/analyze.service';
import { Analysis, AnalysisChart, AnalyzeViewActionEvent } from '../types';

const template = require('./analyze-card.component.html');
require('./analyze-card.component.scss');

@Component({
  selector: 'analyze-card-u',
  template
})

export class AnalyzeCardComponent implements OnInit {

  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analysis: Analysis;
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input() cronJobs: any;

  placeholderClass: string;

  schedule: string;
  // type identifier used for e2e tag
  typeIdentifier: string;

  constructor(
    private _analyzeService: AnalyzeService,
    private _analyzeActionsService: AnalyzeActionsService
  ) { }

  ngOnInit() {
    const { type, id, chartType } = this.analysis as AnalysisChart;
    this.placeholderClass = `m-${type}${chartType ? `-${chartType}` : ''}`;
    this.schedule = generateSchedule(this.cronJobs, id);
    this.typeIdentifier = `analysis-type:${type}${chartType ? `:${chartType}` : ''}`;
  }

  showExecutingFlag(analysisId) {
    return analysisId && this._analyzeService.isExecuting(analysisId);
  }

  afterDelete(analysis) {
    this.action.emit({
      action: 'delete',
      analysis
    });
  }

  afterExecute(analysis) {
    this.action.emit({
      action: 'execute',
      analysis
    });
  }

  afterPublish(analysis) {
    this.action.emit({
      action: 'publish',
      analysis
    });
  }

  afterEdit() {
    this.action.emit({
      action: 'edit'
    });
  }

  fork(analysis) {
    this._analyzeActionsService.fork(analysis).then(status => {
      if (!status) {
        return;
      }
      this.action.emit({
        action: 'fork'
      });
    });
  }

  getType(type) {
    let analysisType = type;
    if (analysisType === 'esReport') {
      analysisType = 'REPORT';
    }
    return analysisType.toUpperCase();
  }
}
