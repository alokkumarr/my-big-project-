import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnalyzeActionsService } from '../../actions';
import { generateSchedule } from '../../cron';
import {
  ExecuteService,
  IExecuteEvent,
  IExecuteEventEmitter,
  EXECUTION_STATES
} from '../../services/execute.service';
import { DesignerSaveEvent } from '../../designer/types';
import { Analysis, AnalysisChart, AnalyzeViewActionEvent } from '../types';
import { JwtService } from '../../../../common/services';

const style = require('./analyze-card.component.scss');

@Component({
  selector: 'analyze-card',
  templateUrl: './analyze-card.component.html',
  styles: [
    `:host {
      display: block;
      margin: 5px;
    }`,
    style
  ]
})

export class AnalyzeCardComponent implements OnInit {

  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analysis: Analysis;
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input() set cronJobs(cronJobs: any) {
    this.schedule = generateSchedule(cronJobs, this.analysis.id);
  }

  placeholderClass: string;
  schedule: string;
  // type identifier used for e2e tag
  typeIdentifier: string;
  canUserFork = false;
  isExecuting = false;

  constructor(
    private _analyzeActionsService: AnalyzeActionsService,
    private _jwt: JwtService,
    private _executeService: ExecuteService
  ) {
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
  }

  ngOnInit() {
    this.canUserFork = this._jwt.hasPrivilege('FORK', {
      subCategoryId: this.analysis.categoryId
    });
    const { type, id, chartType } = this.analysis as AnalysisChart;
    this.placeholderClass = `m-${type}${chartType ? `-${chartType}` : ''}`;
    this.typeIdentifier = `analysis-type:${type}${chartType ? `:${chartType}` : ''}`;

    this._executeService.subscribe(id, this.onExecutionsEvent);
  }

  onExecutionsEvent(e: IExecuteEventEmitter) {
    if (!e.subject.isStopped) {
      e.subject.subscribe(this.onExecutionEvent);
    }
  }

  onExecutionEvent(e: IExecuteEvent) {
    this.isExecuting = e.state === EXECUTION_STATES.EXECUTING;
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

  afterEdit({analysis, requestExecution}: DesignerSaveEvent) {
    this.action.emit({
      action: 'edit',
      analysis,
      requestExecution
    });
  }

  fork(analysis) {
    this._analyzeActionsService.fork(analysis).then(({analysis, requestExecution}: DesignerSaveEvent) => {
      this.action.emit({
        action: 'fork',
        analysis,
        requestExecution
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
