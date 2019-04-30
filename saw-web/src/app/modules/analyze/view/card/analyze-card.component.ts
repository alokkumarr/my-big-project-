import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AnalyzeActionsService } from '../../actions';
import {
  ExecuteService,
  IExecuteEvent,
  IExecuteEventEmitter,
  EXECUTION_STATES
} from '../../services/execute.service';
import { DesignerSaveEvent, isDSLAnalysis } from '../../designer/types';
import {
  Analysis,
  AnalysisDSL,
  AnalysisChartDSL,
  AnalysisChart,
  AnalyzeViewActionEvent
} from '../types';
import { JwtService } from '../../../../common/services';
import { generateSchedule } from '../../../../common/utils/cron2Readable';
import * as isUndefined from 'lodash/isUndefined';

@Component({
  selector: 'analyze-card',
  templateUrl: './analyze-card.component.html',
  styleUrls: ['./analyze-card.component.scss']
})
export class AnalyzeCardComponent implements OnInit {
  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analysis: Analysis | AnalysisDSL;
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input()
  set cronJobs(cronJobs: any) {
    const cron = isUndefined(cronJobs) ? '' : cronJobs[this.analysis.id];
    if (!cron) {
      this.schedule = '';
      return;
    }
    const { cronExpression, activeTab, timezone } = cron.jobDetails;
    this.schedule = generateSchedule(cronExpression, activeTab, timezone);
  }

  placeholderClass: string;
  schedule: string;
  // type identifier used for e2e tag
  typeIdentifier: string;
  canUserFork = false;
  isExecuting = false;

  constructor(
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _executeService: ExecuteService
  ) {
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
  }

  ngOnInit() {
    this.canUserFork = this._jwt.hasPrivilege('FORK', {
      subCategoryId: isDSLAnalysis(this.analysis)
        ? this.analysis.category
        : this.analysis.categoryId
    });
    const { type, id } = this.analysis;
    const chartType = isDSLAnalysis(this.analysis)
      ? (<AnalysisChartDSL>this.analysis).chartOptions.chartType
      : (<AnalysisChart>this.analysis).chartType;
    this.placeholderClass = `m-${type}${chartType ? `-${chartType}` : ''}`;
    this.typeIdentifier = `analysis-type:${type}${
      chartType ? `:${chartType}` : ''
    }`;

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

  afterEdit({ analysis, requestExecution }: DesignerSaveEvent) {
    this.action.emit({
      action: 'edit',
      analysis,
      requestExecution
    });
  }

  fork(an) {
    this._analyzeActionsService.fork(an);
  }

  getType(type) {
    let analysisType = type;
    if (analysisType === 'esReport') {
      analysisType = 'REPORT';
    }
    return analysisType.toUpperCase();
  }
}
