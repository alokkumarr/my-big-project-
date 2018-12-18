import { MatSidenav } from '@angular/material';
import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as moment from 'moment';
import {
  Subscription,
  Subject,
  BehaviorSubject,
  combineLatest,
  timer
} from 'rxjs';
import { debounce } from 'rxjs/operators';
import * as clone from 'lodash/clone';

import {
  AnalyzeService,
  EXECUTION_MODES,
  EXECUTION_DATA_MODES
} from '../services/analyze.service';
import { AnalyzeExportService } from '../services/analyze-export.service';
import {
  ExecuteService,
  IExecuteEventEmitter,
  EXECUTION_STATES
} from '../services/execute.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import {
  flattenPivotData,
  flattenChartData,
  flattenReportData
} from '../../../common/utils/dataFlattener';
import { IPivotGridUpdate } from '../../../common/components/pivot-grid/pivot-grid.component';
import { AnalyzeActionsService } from '../actions';

import { Analysis } from '../types';
import { JwtService, CUSTOM_JWT_CONFIG } from '../../../common/services';

@Component({
  selector: 'executed-view',
  templateUrl: './executed-view.component.html',
  styleUrls: ['./executed-view.component.scss']
})
export class ExecutedViewComponent implements OnInit, OnDestroy {
  analysis: Analysis; // the latest analysis definition
  executedAnalysis: Analysis; // the exact analysis that was executed
  analyses: Analysis[];
  onetimeExecution: boolean;
  executedBy: string;
  executedAt: any;
  data: any[];
  dataLoader: Function;
  canAutoRefresh: boolean;
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

  @ViewChild('detailsSidenav') detailsSidenav: MatSidenav;

  constructor(
    public _executeService: ExecuteService,
    public _analyzeService: AnalyzeService,
    public _router: Router,
    public _route: ActivatedRoute,
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _analyzeExportService: AnalyzeExportService,
    public _toastMessage: ToastService
  ) { }

  ngOnInit() {
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
    combineLatest(this._route.params, this._route.queryParams)
      .pipe(debounce(() => timer(100)))
      .subscribe(([params, queryParams]) => {
        this.onParamsChange(params, queryParams);
      });

    this.canAutoRefresh = this._jwt.hasCustomConfig(
      CUSTOM_JWT_CONFIG.ES_ANALYSIS_AUTO_REFRESH
    );
  }

  onParamsChange(params, queryParams) {
    const { analysisId } = params;
    const { awaitingExecution, loadLastExecution, executionId } = queryParams;

    this.executionId = executionId;

    this.loadAnalysisById(analysisId).then((analysis: Analysis) => {
      this.setPrivileges(analysis);

      /* If an execution is not already going on, create a new execution
       * as applicable. */
      this.executeIfNotWaiting(
        analysis,
        /* awaitingExecution and loadLastExecution paramaters are supposed to be boolean,
         * but all query params come as strings. So typecast them properly */
        awaitingExecution === 'true',
        loadLastExecution === 'true',
        executionId
      );
    });

    this.executionsSub = this._executeService.subscribe(
      analysisId,
      this.onExecutionsEvent
    );
  }

  ngOnDestroy() {
    if (this.executionsSub) {
      this.executionsSub.unsubscribe();
    }
  }

  executeIfNotWaiting(
    analysis,
    awaitingExecution,
    loadLastExecution,
    executionId
  ) {
    if (awaitingExecution) {
      return;
    }
    const isDataLakeReport = analysis.type === 'report';
    if (
      executionId ||
      loadLastExecution ||
      isDataLakeReport ||
      !this.canAutoRefresh
    ) {
      this.loadExecutedAnalysesAndExecutionData(
        analysis.id,
        executionId,
        analysis.type,
        null
      );
    } else {
      this.executeAnalysis(analysis, EXECUTION_MODES.LIVE);
    }
  }

  onSidenavChange(isOpen: boolean) {
    if (isOpen && !this.analyses) {
      this.loadExecutedAnalyses(this.analysis.id).then(analyses => {
        const lastExecutionId = get(analyses, '[0].id', null);
        if (!this.executionId && lastExecutionId) {
          this.executionId = lastExecutionId;
          if (!this.executedAt) {
            this.setExecutedAt(this.executionId);
          }
        }
      });
    }
  }

  onExecutionsEvent(e: IExecuteEventEmitter) {
    if (!e.subject.isStopped) {
      e.subject.subscribe(this.onExecutionEvent);
    }
  }

  onExecutionEvent({ state, response }) {
    /* prettier-ignore */
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
    const isDataLakeReport = get(this.analysis, 'type') === 'report';
    this.onetimeExecution = response.executionType !== EXECUTION_MODES.PUBLISH;
    if (isDataLakeReport && thereIsDataLoaded) {
      this._toastMessage.success(
        'Tap this message to reload data.',
        'Execution finished',
        {
          timeOut: 0,
          extendedTimeOut: 0,
          closeButton: true,
          onclick: () =>
            this.loadExecutedAnalysesAndExecutionData(
              this.analysis.id,
              response.executionId,
              this.analysis.type,
              response
            )
        }
      );
    } else {
      this.loadExecutedAnalysesAndExecutionData(
        this.analysis.id,
        response.executionId,
        this.analysis.type,
        response
      );
    }
  }

  onExecutionError() {
    this.onetimeExecution = false;
    this.loadExecutedAnalysesAndExecutionData(
      this.analysis.id,
      null,
      this.analysis.type,
      null
    );
  }

  onSelectExecution(executionId) {
    if (!executionId) {
      return;
    }
    this.detailsSidenav && this.detailsSidenav.close();
    window['siden'] = this.detailsSidenav;
    this._router.navigate(
      ['analyze', 'analysis', this.analysis.id, 'executed'],
      {
        queryParams: {
          executionId,
          awaitingExecution: false,
          loadLastExecution: false
        }
      }
    );
  }

  executeAnalysis(analysis, mode) {
    this._analyzeActionsService
      .execute(analysis, mode)
      .then(executionStarted => {
        // this.afterExecuteLaunched(analysis);
        if (!executionStarted && !this.analyses) {
          // at least load the executed analyses if none are loaded
          this.loadExecutedAnalysesAndExecutionData(
            analysis.id,
            null,
            analysis.type,
            null
          );
        }
      });
  }

  loadExecutedAnalysesAndExecutionData(
    analysisId,
    executionId,
    analysisType,
    executeResponse
  ) {
    this.executionId = executionId;
    this.loadDataOrSetDataLoader(
      analysisId,
      executionId,
      analysisType,
      executeResponse
    );
  }

  gotoLastPublished(analysis, { executionId }) {
    return () => {
      this._toastMessage.clear();
      this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
        queryParams: {
          executionId,
          awaitingExecution: false,
          loadLastExecution: true
        }
      });
    };
  }

  setExecutedBy(executedBy) {
    this.executedBy =
      executedBy ||
      (this.onetimeExecution ? this._jwt.getLoginId() : 'Scheduled');
  }

  setExecutedAt(executionId) {
    const finished = (
      find(this.analyses, execution => execution.id === executionId) || {
        finished: null
      }
    ).finished;

    this.executedAt = finished
      ? moment
          .utc(finished)
          .local()
          .format('YYYY/MM/DD h:mm A')
      : this.executedAt;
  }

  loadExecutedAnalyses(analysisId) {
    return this._analyzeService
      .getPublishedAnalysesByAnalysisId(analysisId)
      .then(
        (analyses: Analysis[]) => {
          this.analyses = analyses;
          this.setExecutedAt(this.executionId);
          return analyses;
        },
        err => {
          throw err;
        }
      );
  }

  loadAnalysisById(analysisId) {
    return this._analyzeService.readAnalysis(analysisId).then(
      (analysis: Analysis) => {
        this.analysis = analysis;
        // this._analyzeService
        //   .getLastExecutionData(this.analysis.id, {
        //     analysisType: this.analysis.type
        //   })
        //   .then(data => {
        //     console.log(data);
        //   });
        this.executedAnalysis = { ...this.analysis };
        return analysis;
      },
      err => {
        throw err;
      }
    );
  }

  loadDataOrSetDataLoader(
    analysisId,
    executionId,
    analysisType,
    executeResponse = null
  ) {
    // report type data will be loaded by the report grid, because of the paging mechanism
    const isReportType = ['report', 'esReport'].includes(analysisType);
    if (isReportType) {
      /* The Execution data loader defers data loading to the report grid, so it can load the data needed depending on paging */
      if (executeResponse) {
        executeResponse.data = clone(
          flattenReportData(executeResponse.data, this.executedAnalysis)
        );
        // resolve the data that is sent by the execution
        // and the paginated data after that
        this.executedAnalysis = {
          ...this.analysis,
          sqlBuilder:
            executeResponse.queryBuilder || this.executedAnalysis.sqlBuilder
        };
        this.setExecutedBy(executeResponse.executedBy);
        this.executedAt = moment
          .utc(executeResponse.executedAt)
          .local()
          .format('YYYY/MM/DD h:mm A');

        let isItFirstTime = true;
        this.dataLoader = options => {
          if (isItFirstTime) {
            isItFirstTime = false;
            return Promise.resolve({
              data: executeResponse.data,
              totalCount: executeResponse.count
            });
          }
          return this.loadExecutionData(
            analysisId,
            executionId,
            analysisType,
            this.onetimeExecution
              ? { ...options, executionType: EXECUTION_DATA_MODES.ONETIME }
              : options
          );
        };
      } else {
        this.dataLoader = options => {
          return this.loadExecutionData(
            analysisId,
            executionId,
            analysisType,
            this.onetimeExecution
              ? { ...options, executionType: EXECUTION_DATA_MODES.ONETIME }
              : options
          );
        };
      }
    } else {
      if (executeResponse) {
        this.executedAnalysis = {
          ...this.analysis,
          sqlBuilder:
            executeResponse.queryBuilder || this.executedAnalysis.sqlBuilder
        };
        this.setExecutedBy(executeResponse.executedBy);
        this.executedAt = moment
          .utc(executeResponse.executedAt)
          .local()
          .format('YYYY/MM/DD h:mm A');
        this.data = this.flattenData(
          executeResponse.data,
          this.executedAnalysis
        );
      } else {
        this.loadExecutionData(analysisId, executionId, analysisType).then(
          ({ data }) => {
            this.data = this.flattenData(data, this.executedAnalysis);
          }
        );
      }
    }
  }

  flattenData(data, analysis) {
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
      return flattenPivotData(data, analysis.sqlBuilder);
    case 'chart':
      return flattenChartData(data, analysis.sqlBuilder);
    default:
      return data;
    }
  }

  loadExecutionData(analysisId, executionId, analysisType, options: any = {}) {
    options.analysisType = analysisType;

    return (executionId
      ? this._analyzeService.getExecutionData(analysisId, executionId, options)
      : this._analyzeService.getLastExecutionData(analysisId, options)
    ).then(
      ({ data, count, queryBuilder, executedBy }) => {
        if (this.executedAnalysis && queryBuilder) {
          this.executedAnalysis.sqlBuilder = queryBuilder;
        }

        const isReportType = ['report', 'esReport'].includes(analysisType);
        if (isReportType) {
          data = clone(flattenReportData(data, this.analysis));
        }

        this.setExecutedBy(executedBy);
        this.setExecutedAt(executionId);
        return { data: data, totalCount: count };
      },
      err => {
        throw err;
      }
    );
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
    this._router.navigate(['analyze', get(analysis, 'categoryId')]);
  }

  edit() {
    this._analyzeActionsService.edit(this.analysis);
  }

  fork() {
    this._analyzeActionsService.fork(this.analysis);
  }

  gotoForkedAnalysis(analysis) {
    this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
      queryParams: {
        executionId: null,
        awaitingExecution: true,
        loadLastExecution: false
      }
    });
  }

  afterDelete(analysis) {
    this.goBackToMainPage(analysis);
  }

  exportData() {
    /* prettier-ignore */
    switch (this.analysis.type) {
    case 'pivot':
      // export from front end
      this.pivotUpdater$.next({
        export: true
      });
      break;
    case 'chart':
      this.chartUpdater$.next({ export: true });
      break;
    default:
      const executionType = this.onetimeExecution ? EXECUTION_DATA_MODES.ONETIME : EXECUTION_DATA_MODES.NORMAL;
      this._analyzeExportService.export(this.executedAnalysis, this.executionId, executionType);
    }
  }
}
