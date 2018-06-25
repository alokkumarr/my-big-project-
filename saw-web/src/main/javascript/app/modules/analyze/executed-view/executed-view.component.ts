import { Component, OnInit } from '@angular/core';
import { Transition, StateService } from '@uirouter/angular';
import * as get from 'lodash/get';
import { Subscription } from 'rxjs/Subscription';

import { AnalyzeService } from '../services/analyze.service';
import { AnalyzeExportService } from '../services/analyze-export.service';
import {
  ExecuteService,
  IExecuteEventEmitter,
  EXECUTION_STATES
} from '../services/execute.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyzeActionsService } from '../actions';

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
  canUserExecute = false;
  isExecuting = false;
  executionsSub: Subscription;
  executionSub: Subscription;
  executionId: string;

  constructor(
    private _executeService: ExecuteService,
    private _headerProgressService: HeaderProgressService,
    private _analyzeService: AnalyzeService,
    private _transition: Transition,
    private _state: StateService,
    private _analyzeActionsService: AnalyzeActionsService,
    private _jwt: JwtService,
    private _analyzeExportService: AnalyzeExportService,
    private _toastMessage: ToastService
  ) {
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
  }

  ngOnInit() {
    const { analysis, analysisId, executionId, awaitingExecution, loadLastExecution } = this._transition.params();

    this.executionId = executionId;
    if (analysis) {
      this.analysis = analysis;
      this.setPrivileges(analysis);

      this.executeIfNotWaiting(analysis, awaitingExecution, loadLastExecution, executionId);
    } else {
      console.log('loadAnalyses');
      this.loadAnalysisById(analysisId).then(analysis => {
        this.setPrivileges(analysis);

        this.executeIfNotWaiting(analysis, awaitingExecution, loadLastExecution, executionId);
      });
    }
    this.executionsSub = this._executeService.subscribe(analysisId, this.onExecutionsEvent);
  }

  ngOnDestroy() {
    if (this.executionsSub) {
      this.executionsSub.unsubscribe();
    }
  }

  executeIfNotWaiting(analysis, awaitingExecution, loadLastExecution, executionId) {
    if (!awaitingExecution) {
      if (executionId || loadLastExecution) {
        this.loadExecutedAnalysesAndExecutionData(analysis.id, executionId, analysis.type);
      } else {
        this.executeAnalysis(analysis)
      }
    }
  }

  onExecutionsEvent(e: IExecuteEventEmitter) {
    console.log('isStopped', e.subject.isStopped);
    if (!e.subject.isStopped) {
      e.subject.subscribe(this.onExecutionEvent);
    }
  }

  onExecutionEvent(state: EXECUTION_STATES) {
    this.isExecuting = state === EXECUTION_STATES.EXECUTING;

    if (state === EXECUTION_STATES.SUCCESS) {
      const thereIsDataLoaded = this.data || this.dataLoader;
      if (thereIsDataLoaded) {
        this._toastMessage.success('Tap this message to reload data.', 'Execution finished', {
          timeOut: 0,
          extendedTimeOut: 0,
          closeButton: true,
          onclick: this.gotoLastPublished(this.analysis)
        });
      } else {
        this.loadExecutedAnalysesAndExecutionData(this.analysis.id, this.executionId, this.analysis.type);
      }
    }
  }

  gotoLastPublished (analysis) {
    return () => {
      this._toastMessage.clear();
      this._state.go('analyze.executedDetail', {
        analysisId: analysis.id,
        analysis: analysis,
        executionId: null,
        awaitingExecution: false,
        loadLastExecution: true
      }, {reload: true});
    }
  };

  executeAnalysis(analysis) {
    this._analyzeActionsService.execute(analysis).then(() => {
      // this.afterExecuteLaunched(analysis);
    });
  }

  loadExecutedAnalysesAndExecutionData(analysisId, executionId, analysisType) {
    if (executionId) {
      this.loadExecutedAnalyses(analysisId);
      this.loadDataOrSetDataLoader(analysisId, executionId, analysisType);
    } else {
      // get the last execution id and load the data for that analysis
      this.loadExecutedAnalyses(analysisId).then(analyses => {
        const lastExecutionId = get(analyses, '[0].id', null);
        this.executionId = lastExecutionId;
        if (lastExecutionId) {
          this.loadDataOrSetDataLoader(
            analysisId,
            lastExecutionId,
            analysisType
          );
        }
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
    this.canUserExecute = this._jwt.hasPrivilege('EXECUTE', {
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
    this._analyzeActionsService.edit(this.analysis).then(result => {
      if (!result) {
        return;
      }
      // const {isSaveSuccessful, analysis} = result;
      // if (isSaveSuccessful) {
      //   this.analysis = analysis;
      //   this.refreshData();
      // }
    });
  }

  fork() {
    this._analyzeActionsService.fork(this.analysis);
  }

  publish() {
    this._analyzeActionsService.publish(this.analysis);
  }

  afterDelete(analysis) {
    this._state.go('analyze.view', {id: analysis.categoryId});
  }

  exportData() {
    if (this.analysis.type === 'pivot') {
      // export from front end
      // this.requester.next({
      //   exportAnalysis: true
      // });
    } else {
      this._analyzeExportService.export(this.analysis, this.executionId);
    }
  }
}
