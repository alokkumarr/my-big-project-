import { MatSidenav } from '@angular/material';
import {
  Component,
  OnInit,
  ViewChild,
  OnDestroy,
  ElementRef
} from '@angular/core';
import { Store } from '@ngxs/store';
import { ActivatedRoute, Router } from '@angular/router';
import * as get from 'lodash/get';
import * as values from 'lodash/values';
import * as find from 'lodash/find';
import * as isUndefined from 'lodash/isUndefined';
import * as moment from 'moment';
import {
  Subscription,
  Subject,
  BehaviorSubject,
  combineLatest,
  timer
} from 'rxjs';
import { debounce, first } from 'rxjs/operators';
import * as clone from 'lodash/clone';
import * as Bowser from 'bowser';
import * as forEach from 'lodash/forEach';

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
import { ToastService, HtmlDownloadService } from '../../../common/services';
import {
  flattenPivotData,
  flattenChartData,
  flattenReportData
} from '../../../common/utils/dataFlattener';
import { IPivotGridUpdate } from '../../../common/components/pivot-grid/pivot-grid.component';
import { AnalyzeActionsService } from '../actions';

import { Analysis, AnalysisDSL } from '../types';
import { JwtService, CUSTOM_JWT_CONFIG } from '../../../common/services';
import { isDSLAnalysis, Filter } from '../designer/types';
import { CUSTOM_DATE_PRESET_VALUE } from './../consts';

const browser = get(
  Bowser.getParser(window.navigator.userAgent).getBrowser(),
  'name'
);

@Component({
  selector: 'executed-view',
  templateUrl: './executed-view.component.html',
  styleUrls: ['./executed-view.component.scss']
})
export class ExecutedViewComponent implements OnInit, OnDestroy {
  analysis: Analysis | AnalysisDSL; // the latest analysis definition
  executedAnalysis: Analysis | AnalysisDSL; // the exact analysis that was executed
  analyses: Analysis[];
  metric: any;
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
  noPreviousExecution = false;
  hasExecution = false;
  pivotUpdater$: Subject<IPivotGridUpdate> = new Subject<IPivotGridUpdate>();
  chartUpdater$: BehaviorSubject<Object> = new BehaviorSubject<Object>({});
  actionBus$: Subject<Object> = new Subject<Object>();
  public filters: Filter[] = [];

  @ViewChild('detailsSidenav', { static: true }) detailsSidenav: MatSidenav;
  @ViewChild('mapView', { static: false }) mapView: ElementRef;

  constructor(
    public _executeService: ExecuteService,
    public _analyzeService: AnalyzeService,
    public _router: Router,
    public _route: ActivatedRoute,
    private _htmlService: HtmlDownloadService,
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _analyzeExportService: AnalyzeExportService,
    public _toastMessage: ToastService,
    private store: Store
  ) {
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
  }

  ngOnInit() {
    this.canAutoRefresh = this._jwt.hasCustomConfig(
      CUSTOM_JWT_CONFIG.ES_ANALYSIS_AUTO_REFRESH
    );
    combineLatest(this._route.params, this._route.queryParams)
      .pipe(debounce(() => timer(100)))
      .subscribe(([params, queryParams]) => {
        this.onParamsChange(params, queryParams);
      });
  }

  fetchFilters(analysis) {
    const queryBuilder = isDSLAnalysis(analysis)
      ? analysis.sipQuery
      : analysis.sqlBuilder;
    this.filters = isDSLAnalysis(analysis)
      ? this.generateDSLDateFilters(queryBuilder.filters)
      : queryBuilder.filters;
  }

  generateDSLDateFilters(filters) {
    forEach(filters, filtr => {
      if (
        !filtr.isRuntimeFilter &&
        !filtr.isGlobalFilter &&
        filtr.type === 'date' &&
        filtr.model.operator === 'BTW'
      ) {
        filtr.model.gte = moment(filtr.model.value).format('YYYY-MM-DD');
        filtr.model.lte = moment(filtr.model.otherValue).format('YYYY-MM-DD');
        filtr.model.preset = CUSTOM_DATE_PRESET_VALUE;
      }
    });
    return filters;
  }

  onParamsChange(params, queryParams) {
    const { analysisId } = params;
    const {
      awaitingExecution,
      loadLastExecution,
      executionId,
      isDSL
    } = queryParams;

    this.executionId = executionId;
    this.loadAnalysisById(analysisId, isDSL === 'true').then(
      (analysis: Analysis | AnalysisDSL) => {
        this.analysis = analysis;
        this.setPrivileges(analysis);
        this.fetchFilters(analysis);
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
      }
    );
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
      !this.canAutoRefresh ||
      !this.canUserExecute
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
      this.loadExecutedAnalyses(
        this.analysis.id,
        isDSLAnalysis(this.analysis)
      ).then(analyses => {
        const lastExecutionId = get(analyses, '[0].id', null);
        if (!this.executionId && lastExecutionId) {
          this.executionId = lastExecutionId;
          this.setExecutedAt(this.executionId);
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
      setTimeout(() => {
        this.onExecutionSuccess(response);
      }, 500);
      break;
    case EXECUTION_STATES.ERROR:
      this.onExecutionError();
      break;
    default:
    }

    this.isExecuting = state === EXECUTION_STATES.EXECUTING;
  }

  onExecutionSuccess(response) {
    if (isUndefined(this.analysis)) {
      return;
    }
    const thereIsDataLoaded = this.data || this.dataLoader;
    const isDataLakeReport = get(this.analysis, 'type') === 'report';
    this.onetimeExecution = response.executionType !== EXECUTION_MODES.PUBLISH;
    this.filters = isDSLAnalysis(this.analysis)
      ? this.generateDSLDateFilters(response.queryBuilder.filters)
      : response.queryBuilder.filters;
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
              get(this.analysis, 'id'),
              response.executionId,
              get(this.analysis, 'type'),
              response
            )
        }
      );
    } else {
      this.loadExecutedAnalysesAndExecutionData(
        get(this.analysis, 'id'),
        response.executionId,
        get(this.analysis, 'type'),
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
    this.onetimeExecution = false;
    this._router.navigate(
      ['analyze', 'analysis', this.analysis.id, 'executed'],
      {
        queryParams: {
          executionId,
          awaitingExecution: false,
          loadLastExecution: false,
          isDSL: isDSLAnalysis(this.analysis)
        }
      }
    );
  }

  executeAnalysis(analysis, mode) {
    this.store
      .select(state => state.common.metrics)
      .pipe(first(metrics => values(metrics).length > 0))
      .subscribe(() => {
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
    /* If no execution id present, there can be two possible reasons:
       1. We executed right now using either auto-refresh or manually
       2. We are loading last execution data
    */
    if (!executionId) {
      if (this.onetimeExecution) {
        this.executedAt = this.utcToLocal(Date.now());
      } else if (this.analyses && this.analyses.length) {
        const execution: any = this.analyses[0];
        this.executedAt = this.utcToLocal(
          this.secondsToMillis(execution.finished || execution.finishedTime)
        );
      }
    } else {
      /* If execution id is present, try to find an already existing execution
         to load execution time. If not found, it's a weird case, so show
         current time. Can't do much here.
      */
      const finishedExecution = find(
        this.analyses,
        execution => (execution.id || execution.executionId) === executionId
      ) || {
        finished: null
      };
      const finished =
        finishedExecution.finished || finishedExecution.finishedTime;
      if (finished) {
        this.executedAt = this.utcToLocal(finished);
      } else {
        this.executedAt = this.utcToLocal(Date.now());
      }
    }
  }

  secondsToMillis(timestamp: string | number): number | string {
    const secondsOrMillis = parseInt((timestamp || '').toString(), 10);
    if (!secondsOrMillis) {
      // NaN condition
      return timestamp;
    }

    // Millisecond timestamp consists of 13 digits.
    return secondsOrMillis.toString().length < 13
      ? secondsOrMillis * 1000
      : secondsOrMillis;
  }

  loadExecutedAnalyses(analysisId, isDSL) {
    return this._analyzeService
      .getPublishedAnalysesByAnalysisId(analysisId, isDSL)
      .then(
        (analyses: Analysis[]) => {
          this.analyses = analyses;
          this.noPreviousExecution = !analyses || !analyses.length;
          this.setExecutedAt(this.executionId);
          return analyses;
        },
        err => {
          throw err;
        }
      );
  }

  loadAnalysisById(analysisId, isDSL: boolean) {
    return this._analyzeService
      .readAnalysis(analysisId, isDSL)
      .then((analysis: AnalysisDSL) => {
        this.analysis = analysis;
        // this._analyzeService
        //   .getLastExecutionData(this.analysis.id, {
        //     analysisType: this.analysis.type
        //   })
        //   .then(data => {
        //     console.log(data);
        //   });
        /* Get metrics to get full artifacts. Needed to show filters for fields
        that aren't selected for data */
        return this._analyzeService
          .getArtifactsForDataSet(this.analysis.semanticId)
          .toPromise();
      })
      .then(metric => {
        this.metric = metric;
        if (isDSLAnalysis(this.analysis) && this.analysis.type === 'map') {
          this.executedAnalysis = {
            ...this.analysis,
            sipQuery: this._analyzeService.copyGeoTypeFromMetric(
              get(this.metric, 'artifacts.0.columns', []),
              this.analysis.sipQuery
            )
          };
          return this.analysis;
        } else {
          this.executedAnalysis = { ...this.analysis };
          return this.analysis;
        }
      });
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
        this.executedAnalysis.artifacts = this.metric.artifacts;
        executeResponse.data = clone(
          flattenReportData(
            executeResponse.data,
            this.executedAnalysis || this.analysis
          )
        );
        // resolve the data that is sent by the execution
        // and the paginated data after that
        this.executedAnalysis = {
          ...this.analysis,
          ...(isDSLAnalysis(this.executedAnalysis || this.analysis) &&
          this.executedAnalysis.type === 'map'
            ? {
                sipQuery: this._analyzeService.copyGeoTypeFromMetric(
                  get(this.metric, 'artifacts.0.columns', []),
                  executeResponse.queryBuilder ||
                    (<AnalysisDSL>(this.executedAnalysis || this.analysis))
                      .sipQuery
                )
              }
            : {
                sipQuery:
                  executeResponse.queryBuilder ||
                  (<Analysis>(this.executedAnalysis || this.analysis))
                    .sqlBuilder
              })
        };
        this.fetchFilters(this.executedAnalysis);
        this.setExecutedBy(executeResponse.executedBy);
        this.executedAt = this.utcToLocal(executeResponse.executedAt);

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
        /* Mark hasExecution temporarily as true to allow fetch of data.
           It'll be marked false if data is not found (no execution exists).
        */
        this.hasExecution = true;
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
          ...(isDSLAnalysis(this.executedAnalysis)
            ? {
                sipQuery: this._analyzeService.copyGeoTypeFromMetric(
                  get(this.metric, 'artifacts.0.columns', []),
                  executeResponse.queryBuilder || this.executedAnalysis.sipQuery
                )
              }
            : {
                sqlBuilder:
                  executeResponse.queryBuilder ||
                  this.executedAnalysis.sqlBuilder
              })
        };
        this.setExecutedBy(executeResponse.executedBy);
        this.executedAt = this.utcToLocal(executeResponse.executedAt);
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

  utcToLocal(utcTime) {
    if (isUndefined(utcTime)) {
      return;
    }
    return moment(utcTime)
      .local()
      .format('YYYY/MM/DD h:mm A');
  }

  flattenData(data, analysis) {
    /* prettier-ignore */
    switch (analysis.type) {
    case 'pivot':
      return flattenPivotData(data, (<AnalysisDSL>analysis).sipQuery || (<Analysis>analysis).sqlBuilder);
    case 'chart':
    case 'map':
      return flattenChartData(data, isDSLAnalysis(analysis) ? analysis.sipQuery : analysis.sqlBuilder);
    default:
      return data;
    }
  }

  loadExecutionData(analysisId, executionId, analysisType, options: any = {}) {
    options.analysisType = analysisType;
    options.isDSL = isDSLAnalysis(this.analysis);
    return (executionId
      ? this._analyzeService.getExecutionData(analysisId, executionId, options)
      : this._analyzeService.getLastExecutionData(analysisId, options)
    ).then(
      ({ data, count, queryBuilder, executedBy }) => {
        /* Check if a successful execution data is returned. */
        this.hasExecution =
          Boolean(data.length) || Boolean(queryBuilder) || Boolean(executedBy);
        /* If there's no execution id (loading last execution) and there's no
          execution data as well, then we can deduce there's no previous
          execution for this analysis present */
        this.noPreviousExecution = !executionId && !this.hasExecution;
        if (this.executedAnalysis && queryBuilder) {
          if (this.executedAnalysis.type !== 'report') {
            this.executedAnalysis = {
              ...queryBuilder,
              sipQuery: this._analyzeService.copyGeoTypeFromMetric(
                get(this.metric, 'artifacts.0.columns', []),
                queryBuilder.sipQuery
              )
            };
          } else {
            this.executedAnalysis = {
              ...queryBuilder,
              sipQuery: queryBuilder.sipQuery
            };
          }
        }
        const isReportType = ['report', 'esReport'].includes(analysisType);
        if (isReportType) {
          const requestAnalysis = {
            ...this.executedAnalysis,
            artifacts: get(this.executedAnalysis, 'sipQuery.artifacts')
          };
          data = clone(flattenReportData(data, requestAnalysis));
        }

        this.setExecutedBy(executedBy);
        this.setExecutedAt(executionId);
        this.fetchFilters(this.executedAnalysis);
        return { data: data, totalCount: count };
      },
      err => {
        throw err;
      }
    );
  }

  setPrivileges(analysis: Analysis | AnalysisDSL) {
    const categoryId = isDSLAnalysis(analysis)
      ? analysis.category
      : analysis.categoryId;
    const userId = analysis.userId;
    this.canUserPublish = this._jwt.hasPrivilege('PUBLISH', {
      subCategoryId: categoryId
    });
    const canForkInCurrentFolder = this._jwt.hasPrivilege('FORK', {
      subCategoryId: categoryId
    });
    const canForkInDraftsFolder = this._jwt.hasPrivilegeForDraftsFolder('FORK');
    this.canUserFork = canForkInCurrentFolder && canForkInDraftsFolder;
    this.canUserExecute = this._jwt.hasPrivilege('EXECUTE', {
      subCategoryId: categoryId
    });
    this.canUserEdit = this._jwt.hasPrivilege('EDIT', {
      subCategoryId: categoryId,
      creatorId: userId
    });
  }

  goBackToMainPage(analysis) {
    this._router.navigate([
      'analyze',
      isDSLAnalysis(analysis) ? analysis.category : analysis.categoryId
    ]);
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

  afterPublish(analysis) {
    if (analysis) {
      this.goBackToMainPage(analysis);
    }
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
    case 'map':
      if (get(this.analysis, 'mapOptions.mapType') === 'map') {
        if (browser !== 'Chrome') {
          const title = 'Browser not supported.';
          const msg =
            'Downloading map analysis only works with Chrome browser at the moment.';
          this._toastMessage.warn(msg, title);
          return;
        }
        this._htmlService.turnHtml2pdf(this.mapView.nativeElement, this.analysis.name);
      } else {
        this.actionBus$.next({ export: true });
      }
      break;
    default:
      const executionType = this.onetimeExecution ? EXECUTION_DATA_MODES.ONETIME : EXECUTION_DATA_MODES.NORMAL;
      this._analyzeExportService.export(this.executedAnalysis, this.executionId, executionType);
    }
  }
}
