import { Component, OnInit } from '@angular/core';
import { Transition, StateService } from '@uirouter/angular';
import * as get from 'lodash/get';
import { Subscription } from 'rxjs/Subscription';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { AnalyzeService } from '../services/analyze.service';
import { AnalyzeExportService } from '../services/analyze-export.service';
import {
  ExecuteService,
  IExecuteEventEmitter,
  EXECUTION_STATES
} from '../services/execute.service';
import { HeaderProgressService } from '../../../common/services/header-progress.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { flattenPivotData, flattenChartData } from '../../../common/utils/dataFlattener';
import { IPivotGridUpdate } from '../../../common/components/pivot-grid/pivot-grid.component';
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
  pivotUpdater$: Subject<IPivotGridUpdate> = new Subject<IPivotGridUpdate>();
  chartUpdater$: BehaviorSubject<Object> = new BehaviorSubject<Object>({});

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
      const isDataLakeReport = analysis.type === 'report';
      if (executionId || loadLastExecution || isDataLakeReport) {
        this.loadExecutedAnalysesAndExecutionData(analysis.id, executionId, analysis.type, null);
      } else {
        this.executeAnalysis(analysis)
      }
    }
  }

  onExecutionsEvent(e: IExecuteEventEmitter) {
    if (!e.subject.isStopped) {
      e.subject.subscribe(this.onExecutionEvent);
    }
  }

  onExecutionEvent({state, response}) {

    switch (state) {
    case EXECUTION_STATES.SUCCESS:
      this.onExecutionSuccess(response);
      break;
    case EXECUTION_STATES.ERROR:
      this.onExecutionError();
      break;
    default:
    }

    this.isExecuting = state === EXECUTION_STATES.EXECUTING;
  }

  onExecutionSuccess(response) {
    const thereIsDataLoaded = this.data || this.dataLoader;
    const isDataLakeReport = this.analysis.type === 'report';
    if (isDataLakeReport && thereIsDataLoaded) {
      this._toastMessage.success('Tap this message to reload data.', 'Execution finished', {
        timeOut: 0,
        extendedTimeOut: 0,
        closeButton: true,
        onclick: this.gotoLastPublished(this.analysis)
      });
    } else {
      this.loadExecutedAnalysesAndExecutionData(this.analysis.id, null, this.analysis.type, response);
    }
  }

  onExecutionError() {
    this.loadExecutedAnalysesAndExecutionData(this.analysis.id, null, this.analysis.type, null);
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
    this._analyzeActionsService.execute(analysis).then(executionStarted => {
      // this.afterExecuteLaunched(analysis);
      if (!executionStarted && !this.analyses) {
        // at least load the executed analyses if none are loaded
        this.loadExecutedAnalysesAndExecutionData(analysis.id, null, analysis.type, null);
      }
    });
  }

  loadExecutedAnalysesAndExecutionData(analysisId, executionId, analysisType, executeResponse) {
    if (executionId) {
      this.loadExecutedAnalyses(analysisId);
      this.loadDataOrSetDataLoader(analysisId, executionId, analysisType, executeResponse);
    } else {
      // get the last execution id and load the data for that analysis
      this.loadExecutedAnalyses(analysisId).then(analyses => {
        const lastExecutionId = get(analyses, '[0].id', null);
        this.executionId = lastExecutionId;
        if (lastExecutionId) {
          this.loadDataOrSetDataLoader(
            analysisId,
            lastExecutionId,
            analysisType,
            executeResponse
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

  loadDataOrSetDataLoader(analysisId, executionId, analysisType, executeResponse = null) {
    // report type data will be loaded by the report grid, because of the paging mechanism
    const isReportType = ['report', 'esReport'].includes(analysisType);
    if (isReportType) {
      /* The Execution data loader defers data loading to the report grid, so it can load the data needed depending on paging */
      if (executeResponse) {
        // resolve the data that is sent by the execution
        // and the paginated data after that
        let isItFirstTime = true;
        this.dataLoader = options => {
          if (isItFirstTime) {
            isItFirstTime = false;
            return Promise.resolve({
              data: executeResponse.data,
              totalCount: executeResponse.count
            });
          }
          return this.loadExecutionData(analysisId, executionId, analysisType, options);
        }
      } else {
        this.dataLoader = options => {
          return this.loadExecutionData(analysisId, executionId, analysisType, options);
        }
      }
    } else {
      if (executeResponse) {
        this.data = this.flattenData(executeResponse.data, this.analysis);
      } else {
        this.loadExecutionData(analysisId, executionId, analysisType).then(({data}) => {
          this.data = this.flattenData(data, this.analysis);
        });
      }
    }
  }

  flattenData(data, analysis) {
    switch(analysis.type) {
    case 'pivot':
      return flattenPivotData(data, analysis.sqlBuilder);
    case 'chart':
      return flattenChartData(data, analysis.sqlBuilder);
    default:
      return data;
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

  goBackToMainPage(analysis) {
    this._state.go('analyze.view', {id: get(analysis, 'categoryId')});
  }

  edit() {
    this._analyzeActionsService.edit(this.analysis).then(result => {
      if (!result) {
        return;
      }
      const {isSaveSuccessful, analysis} = result;
      if (isSaveSuccessful) {
        this.analysis = analysis;
      }
    });
  }

  fork() {
    this._analyzeActionsService.fork(this.analysis);
  }

  publish() {
    this._analyzeActionsService.publish(this.analysis);
  }

  afterDelete(analysis) {
    this.goBackToMainPage(analysis);
  }

  exportData() {
    switch (this.analysis.type) {
    case 'pivot':
    // export from front end
      this.pivotUpdater$.next({
        export: true
      });
      break;
    case 'chart':
      this.chartUpdater$.next({export: true});
      break;
    default:
      this._analyzeExportService.export(this.analysis, this.executionId);
    }
  }
}
