import { Component, OnInit } from '@angular/core';
import { Transition } from '@uirouter/angular';
import * as get from 'lodash/get';
import { Subscription } from 'rxjs/Subscription';

import { AnalyzeService } from '../services/analyze.service';
import {
  ExecuteService,
  IExecuteEvent,
  EXECUTION_STATES
} from '../services/execute.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';

import { Analysis } from '../types';
import { JwtService } from '../../../../login/services/jwt.service';

const template = require('./executed-view.component.html');
require('./executed-view.component.scss');

@Component({
  selector: 'executed-view',
  template
})

export class ExecutedViewComponent implements OnInit {

  _executionId: string;
  analysis: Analysis;
  analyses: Analysis[];
  data: any[];
  dataLoader: Function;
  canUserPublish = false;
  canUserFork = false;
  canUserEdit = false;
  isExecuting = false;
  executionSub: Subscription;

  constructor(
    private _executeService: ExecuteService,
    private _headerProgressService: HeaderProgressService,
    private _analyzeService: AnalyzeService,
    private _transition: Transition,
    private _jwt: JwtService
  ) {}

  ngOnInit() {
    const { analysis, analysisId, executionId } = this._transition.params();
    if (analysis) {
      this.analysis = analysis;
      this.setPrivileges(analysis);
      // this.watchAnalysisExecution();
      this.loadExecutedAnalysesAndExecutionData(analysisId, executionId, analysis.type);
    } else {
      this.loadAnalysisById(analysisId).then(analysis => {
        this.setPrivileges(analysis);
        // this.watchAnalysisExecution();
        this.loadExecutedAnalysesAndExecutionData(analysisId, executionId, analysis.type);
      });
    }

    this.onExecutionEvent = this.onExecutionEvent.bind(this);

    this._executeService.subscribe(analysisId, this.onExecutionEvent);
  }

  onExecutionEvent(e: IExecuteEvent) {
    this.isExecuting = e.executionState === EXECUTION_STATES.EXECUTING;
  }

  loadExecutedAnalysesAndExecutionData(analysisId, executionId, analysisType) {
    if (executionId) {
      this.loadExecutedAnalyses(analysisId);
      this.loadDataOrSetDataLoader(analysisId, executionId, analysisType);
    } else {
      // get the last execution id and load the data for that analysis
      this.loadExecutedAnalyses(analysisId).then(analyses => {
        const lastExecutionId = get(analyses, '[0].id', null);
        this.loadDataOrSetDataLoader(
          analysisId,
          lastExecutionId,
          analysisType
        );
      });
    }
  }

  loadExecutedAnalyses(analysisId) {
    this._headerProgressService.show();
    return this._analyzeService.getPublishedAnalysesByAnalysisId(analysisId)
      .then(analyses => {
        this.analyses = analyses;
        this._headerProgressService.hide();
        return analyses;
      }, err => {
        this._headerProgressService.hide();
        throw err;
      });
  }

  loadAnalysisById(analysisId) {
    this._headerProgressService.show();
    return this._analyzeService.readAnalysis(analysisId)
      .then(analysis => {
        this.analysis = analysis;
        this._headerProgressService.hide();
        return analysis;
      }, err => {
        this._headerProgressService.hide();
        throw err;
      });
  }

  loadDataOrSetDataLoader(analysisId, executionId, analysisType) {
    // report type data will be loaded by the report grid, because of the paging mechanism
    const isReportType = ['report', 'esReport'].includes(analysisType);
    if (isReportType) {
      /* The Execution data loader defers data loading to the report grid, so it can load the data needed depending on paging */
      this.dataLoader = options => this.loadExecutionData(analysisId, executionId, analysisType, options);
    } else {
      this.loadExecutionData(analysisId, executionId, analysisType).then(({data}) => {
        this.data = data;
      });
    }
  }

  loadExecutionData(analysisId, executionId, analysisType, options: any = {}) {
    this._headerProgressService.show();
    options.analysisType = analysisType;

    return this._analyzeService.getExecutionData(analysisId, executionId, options)
      .then(({data, count}) => {
        this._headerProgressService.hide();
        return {data, totalCount: count};
      }, err => {
        this._headerProgressService.hide();
        throw err;
      });
  }

  setPrivileges({ categoryId, userId }: Analysis) {
    this.canUserPublish = this._jwt.hasPrivilege('PUBLISH', {
      subCategoryId: categoryId
    });
    this.canUserFork = this._jwt.hasPrivilege('FORK', {
      subCategoryId: categoryId
    });
    this.canUserEdit = this._jwt.hasPrivilege('EDIT', {
      subCategoryId: categoryId,
      creatorId: userId
    });
  }

  goBack() {
    window.history.back();
  }

  edit() {

  }

  fork() {

  }

  publish() {

  }

  afterDelete() {

  }

  exportData() {

  }
}
