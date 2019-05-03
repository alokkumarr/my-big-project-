import * as forEach from 'lodash/forEach';
import * as floor from 'lodash/floor';
import * as set from 'lodash/set';
import * as isEmpty from 'lodash/isEmpty';
import * as has from 'lodash/has';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGet from 'lodash/fp/get';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isUndefined from 'lodash/isUndefined';
import * as clone from 'lodash/clone';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Analysis, AnalysisDSL, AnalysisType } from '../../../models';

import { JwtService } from '../../../common/services';
import { ToastService, MenuService } from '../../../common/services';
import AppConfig from '../../../../../appConfig';
import { zip, Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { DSL_ANALYSIS_TYPES } from '../consts';
import { isDSLAnalysis } from '../designer/types';

const apiUrl = AppConfig.api.url;

interface ExecutionRequestOptions {
  take?: number;
  skip?: number;
  executionType?: string;
  forcePaginate?: boolean;
  analysisType?: string;
  isDSL?: boolean;
}
export const EXECUTION_MODES = {
  PREVIEW: 'preview',
  LIVE: 'regularExecution',
  PUBLISH: 'publish'
};

export const EXECUTION_DATA_MODES = {
  /* When fetching data for execution by execution id,
   * we need to provide the correct param if the execution wasn't saved in history */
  ONETIME: 'onetime',
  NORMAL: 'normal'
};

const EXECUTION_STATES = {
  SUCCESS: 'success',
  ERROR: 'error',
  EXECUTING: 'executing'
};

const MODULE_NAME = 'ANALYZE';

@Injectable()
export class AnalyzeService {
  _executingAnalyses: Object = {};
  _executions: Object = {};

  constructor(
    public _http: HttpClient,
    public _jwtService: JwtService,
    public _toastMessage: ToastService,
    public _menu: MenuService
  ) {
    window['analysisService'] = this;
  }

  isExecuting(analysisId) {
    return EXECUTION_STATES.EXECUTING === this._executingAnalyses[analysisId];
  }

  didExecutionFail(analysisId) {
    return EXECUTION_STATES.ERROR === this._executingAnalyses[analysisId];
  }

  executionFor(analysisId) {
    return this._executions[analysisId];
  }

  /* getRequestParams will generate the base structure and auto-fill it
     with fields common across all request.

     The argument @params is an array of tuples containing properties
     to add to this base structure, and follow lodash's @set method
     argument format.

     Example of @params:
     [
       ['contents.action', 'update'],
       ['contents.keys.[0].id', '1234556']
     ]
     */
  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });

    return reqParams;
  }

  getExportData(
    analysisId,
    executionId,
    analysisType,
    executionType = EXECUTION_DATA_MODES.NORMAL
  ) {
    const onetimeExecution =
      executionType === EXECUTION_DATA_MODES.ONETIME
        ? '&executionType=onetime'
        : '';
    const requestURL = isUndefined(executionId)
      ? `exports/latestExecution/${analysisId}/data?analysisType=${analysisType}${onetimeExecution}`
      : `exports/${executionId}/executions/${analysisId}/data?analysisType=${analysisType}${onetimeExecution}`;
    return this.getRequest(requestURL).toPromise();
  }

  /**
   * Gets list of analyses from legacy endpoint (non-dsl).
   * This endpoint is for backward-compatibility. Remove when not needed.
   *
   * @param {*} subCategoryId
   * @returns {Observable<Analysis[]>}
   * @memberof AnalyzeService
   */
  getAnalysesForNonDSL(subCategoryId): Observable<Analysis[]> {
    const payload = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.keys.[0].categoryId', subCategoryId]
    ]);
    return <Observable<Analysis[]>>(
      this.postRequest(`analysis`, payload).pipe(map(fpGet('contents.analyze')))
    );
  }

  getAnalysesDSL(subCategoryId: string | number): Observable<AnalysisDSL[]> {
    return <Observable<AnalysisDSL[]>>(
      this.getRequest(`dslanalysis?category=${subCategoryId}`)
    );
  }

  /**
   * Stitches non-dsl and dsl endpoints for listing analyses and provides
   * results as a single array.
   *
   * @param {*} subCategoryId
   * @returns {Promise<Analysis[]>}
   * @memberof AnalyzeService
   */
  getAnalysesFor(
    subCategoryId: string | number /* , opts = {} */
  ): Promise<Array<Analysis | AnalysisDSL>> {
    // Create fp sort's type to nail everything down with types
    type FPSort<T> = (input: Array<T>) => Array<T>;

    return zip(
      this.getAnalysesForNonDSL(subCategoryId),
      this.getAnalysesDSL(subCategoryId)
    )
      .pipe(
        // Merge list of analyses from both observables into one
        map(([nonDSLAnalyses, dslAnalyses]) => {
          return [].concat(nonDSLAnalyses).concat(dslAnalyses);
        }),

        // Sort all the analyses based on their create time in descending order (newest first).
        // Uses correct time field based on if analysis is new dsl type or not
        map(<FPSort<Analysis | AnalysisDSL>>(
          fpSortBy([
            analysis =>
              isDSLAnalysis(analysis)
                ? -(analysis.createdTime || 0)
                : -(analysis.createdTimestamp || 0)
          ])
        ))
      )
      .toPromise();
  }

  getPublishedAnalysesByAnalysisId(id, isDSL) {
    const path = isDSL ? `internal/proxy/storage/${id}/executions` : `analysis/${id}/executions`;
    if (isDSL) {
      return <Promise<Analysis[]>>this.getRequest(path)
      .toPromise()
      .then(fpSortBy([obj =>  -obj.finishedTime]));
    } else {
      return <Promise<Analysis[]>>this.getRequest(path)
      .toPromise()
      .then(fpGet(`executions`))
      .then(fpSortBy([obj => -obj.finished]));
    }
  }

  forcePagination(data, options: ExecutionRequestOptions = {}) {
    if (isEmpty(data) || !(has(options, 'skip') || has(options, 'take'))) {
      return data;
    }

    return data.slice(options.skip, options.skip + options.take);
  }

  getLastExecutionData(analysisId, options: ExecutionRequestOptions = {}) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    let url = '';
    if (options.isDSL) {
      const path = `internal/proxy/storage/${analysisId}/lastExecutions/data`;
      url = `${path}`;
    } else {
      const page = floor(options.skip / options.take) + 1;
      const path = `analysis/${analysisId}/executions/data`;
      const queryParams = `page=${page}&pageSize=${options.take}&analysisType=${
        options.analysisType
      }`;
      url = `${path}?${queryParams}`;
    }

    return this.getRequest(url)
      .toPromise()
      .then(resp => {
        const data = fpGet(`data`, resp);
        const queryBuilder = fpGet(`queryBuilder`, resp);
        const executedBy = fpGet(`executedBy`, resp);
        const count = fpGet(`totalRows`, resp) || data.length;
        return {
          data: options.forcePaginate
            ? this.forcePagination(data, options)
            : data,
          queryBuilder,
          executedBy,
          count
        };
      });
  }

  getExecutionData(
    analysisId,
    executionId,
    options: ExecutionRequestOptions = {}
  ) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;
    const onetimeExecution =
      options.executionType === EXECUTION_DATA_MODES.ONETIME
        ? '&executionType=onetime'
        : '';

    let url = '';
    if (options.isDSL) {
      const path = `internal/proxy/storage/${executionId}/executions/data`;
      url = `${path}`;
    } else {
      const path = `analysis/${analysisId}/executions/${executionId}/data`;
      const queryParams = `?page=${page}&pageSize=${options.take}&analysisType=${
        options.analysisType
      }${onetimeExecution}`;
      url = `${path}${queryParams}`;
    }
    return this.getRequest(url)
      .toPromise()
      .then(resp => {
        const data = fpGet(`data`, resp);
        const queryBuilder = fpGet(`queryBuilder`, resp);
        const executedBy = fpGet(`executedBy`, resp);
        const count = fpGet(`totalRows`, resp) || data.length;
        return {
          data: options.forcePaginate
            ? this.forcePagination(data, options)
            : data,
          queryBuilder,
          executedBy,
          count
        };
      });
  }

  readAnalysis(
    analysisId,
    hasDSL: boolean,
    customHeaders = {}
  ): Promise<Analysis | AnalysisDSL> {
    return hasDSL
      ? this.readAnalysisDSL(analysisId).toPromise()
      : this.readAnalysisNonDSL(analysisId, customHeaders);
  }

  readAnalysisNonDSL(analysisId, customHeaders = {}): Promise<Analysis> {
    const payload = this.getRequestParams([
      ['contents.action', 'read'],
      ['contents.keys.[0].id', analysisId]
    ]);
    return <Promise<Analysis>>this.postRequest(
      `analysis`,
      payload,
      customHeaders
    )
      .toPromise()
      .then(fpGet(`contents.analyze.[0]`));
  }

  readAnalysisDSL(analysisId): Observable<AnalysisDSL> {
    return <Observable<AnalysisDSL>>(
      this._http.get(`${apiUrl}/dslanalysis/${analysisId}`).pipe(
        first(),
        map((resp: { analysis: AnalysisDSL }) => resp.analysis)
      )
    );
  }

  previewExecution(model, options = {}) {
    return this.applyAnalysis(model, EXECUTION_MODES.PREVIEW, options);
  }

  executeAnalysis(model, execType = EXECUTION_MODES.LIVE) {
    const promise = new Promise((resolve, reject) => {
      if (this.isExecuting(model.id)) {
        const msg =
          'Analysis is executing already. Please try again in some time.';
        this._toastMessage.error(msg);
        reject(msg);
      } else {
        this._executions[model.id] = promise;

        this._executingAnalyses[model.id] = EXECUTION_STATES.EXECUTING;
        this.applyAnalysis(model, execType).then(
          ({
            data,
            executionId,
            executedBy,
            executedAt,
            queryBuilder,
            executionType,
            count
          }) => {
            this._executingAnalyses[model.id] = EXECUTION_STATES.SUCCESS;
            resolve({
              data,
              executionId,
              executionType,
              count,
              executedBy,
              executedAt,
              queryBuilder
            });
          },
          err => {
            this._executingAnalyses[model.id] = EXECUTION_STATES.ERROR;
            reject(err);
          }
        );
      }
    });

    return promise;
  }

  changeSchedule(analysis) {
    const schedule = analysis.schedule;
    const scheduleState = schedule.scheduleState;
    switch (scheduleState) {
      case 'new':
        return this.postRequest(`scheduler/schedule`, schedule).toPromise();
      case 'exist':
        return this.postRequest(`scheduler/update`, schedule).toPromise();
      case 'delete':
        return this.postRequest(`scheduler/delete`, schedule).toPromise();
      default:
    }
  }

  getCronDetails(requestBody) {
    return this.postRequest(`scheduler/fetchJob`, requestBody).toPromise();
  }

  getAllCronJobs(model) {
    return this.postRequest(`scheduler/jobs`, model).toPromise();
  }

  getlistFTP(custCode) {
    return this.postRequest(`exports/listFTP`, custCode).toPromise();
  }

  deleteAnalysis(model: Analysis | AnalysisDSL): Promise<any> {
    return !!(<AnalysisDSL>model).sipQuery
      ? this.deleteAnalysisDSL(model as AnalysisDSL).toPromise()
      : this.deleteAnalysisNonDSL(model as Analysis);
  }

  deleteAnalysisDSL(model: AnalysisDSL): Observable<any> {
    return <Observable<AnalysisDSL>>(
      this._http.delete(`${apiUrl}/dslanalysis/${model.id}`).pipe(first())
    );
  }

  deleteAnalysisNonDSL(model: Analysis): Promise<Analysis> {
    if (
      !this._jwtService.hasPrivilege('DELETE', {
        subCategoryId: model.categoryId,
        creatorId: model.userId
      })
    ) {
      return Promise.reject(new Error('Access denied.'));
    }
    const payload = this.getRequestParams([
      ['contents.action', 'delete'],
      ['contents.keys.[0].id', model.id]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, payload).toPromise();
  }

  getCategories(privilege) {
    const menuPromise = this._menu.getMenu(MODULE_NAME);
    if (!privilege) {
      return menuPromise;
    }

    return menuPromise.then(menu => {
      const menuClone = cloneDeep(menu);
      forEach(menuClone, menuFeature => {
        menuFeature.children = filter(menuFeature.children, menuSubFeature => {
          return this._jwtService.hasPrivilege(privilege, {
            subCategoryId: menuSubFeature.id
          });
        });
      });
      return menuClone;
    });
  }

  getCategory(id) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return this._menu.getMenu(MODULE_NAME).then(menu => {
      const subCategories = flatMap(menu, category => category.children);
      return find(subCategories, sc => sc.id.toString() === id);
    });
  }

  getMethods() {
    return this.getRequest('/api/analyze/methods').toPromise();
  }

  updateAnalysis(model): Promise<Analysis | AnalysisDSL> {
    return !!model.sipQuery
      ? this.updateAnalysisDSL(model).toPromise()
      : this.updateAnalysisNonDSL(model);
  }

  updateAnalysisNonDSL(model: Analysis): Promise<Analysis> {
    delete model.isScheduled;
    delete model.executionType;
    /* Add update info */
    model.updatedTimestamp = Date.now();
    model.updatedUserName = this._jwtService.getUserName();

    const payload = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, payload)
      .toPromise()
      .then(fpGet(`contents.analyze.[0]`));
  }

  updateAnalysisDSL(model: AnalysisDSL): Observable<AnalysisDSL> {
    return <Observable<AnalysisDSL>>(
      this._http.put(`${apiUrl}/dslanalysis/${model.id}`, model).pipe(
        first(),
        map((res: { analysis: AnalysisDSL }) => res.analysis)
      )
    );
  }

  applyAnalysis(
    model,
    mode = EXECUTION_MODES.LIVE,
    options: ExecutionRequestOptions = {}
  ) {
    return !!model.sipQuery
      ? this.applyAnalysisDSL(model, mode, options)
      : this.applyAnalysisNonDSL(model, mode, options);
  }

  applyAnalysisDSL(
    model,
    mode = EXECUTION_MODES.LIVE,
    options: ExecutionRequestOptions = {}
  ) {
    return this._http
    .post(`${apiUrl}/internal/proxy/storage/execute?id=${model.id}&ExecutionType=${mode}`, model.sipQuery)
      .pipe(
        map((resp: any) => {
          return {
            data: resp,
            executionId: resp.executionId || (model.sipQuery ? '123456' : null),
            executionType: mode,
            executedBy: this._jwtService.getLoginId(),
            executedAt: Date.now(),
            designerQuery: fpGet(`query`, resp),
            queryBuilder: { ...model.sipQuery },
            count: fpGet(`totalRows`, resp)
          };
        })
      )
      .toPromise();
  }

  applyAnalysisNonDSL(
    model,
    mode = EXECUTION_MODES.LIVE,
    options: ExecutionRequestOptions = {}
  ) {
    delete model.isScheduled;

    model.executionType = mode;

    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;

    // TODO remove clone stuff before merging
    const cloned = clone(model);
    if (cloned.type === 'map') {
      cloned.type = 'chart';
    }

    const payload = this.getRequestParams([
      ['contents.action', 'execute'],
      ['contents.executedBy', this._jwtService.getLoginId()],
      ['contents.page', page],
      ['contents.pageSize', options.take],
      ['contents.keys.[0].id', cloned.id],
      ['contents.keys.[0].type', cloned.type],
      ['contents.analyze', [cloned]]
    ]);
    return this.postRequest(`analysis`, payload)
      .toPromise()
      .then(resp => {
        return {
          data: fpGet(`contents.analyze.[0].data`, resp),
          executionId: fpGet(`contents.analyze.[0].executionId`, resp),
          executionType: mode,
          executedBy: this._jwtService.getLoginId(),
          executedAt: Date.now(),
          designerQuery: fpGet(`query`, resp),
          queryBuilder: { ...model.sqlBuilder },
          count: fpGet(`contents.analyze.[0].totalRows`, resp)
        };
      });
  }

  getDataBySettings(analysis, mode = EXECUTION_MODES.PREVIEW, options = {}) {
    return this.applyAnalysis(analysis, mode, options).then(
      ({
        data,
        executionId,
        executedBy,
        executedAt,
        queryBuilder,
        executionType,
        designerQuery,
        count
      }) => {
        return {
          analysis,
          data,
          executionId,
          executedBy,
          executedAt,
          queryBuilder,
          executionType,
          designerQuery,
          count
        };
      }
    );
  }

  generateQuery(payload) {
    return this.postRequest('/api/analyze/generateQuery', payload).toPromise();
  }

  saveReport(model) {
    model.saved = true;
    const updatePromise = this.updateAnalysis(model);
    return updatePromise;
  }

  getSemanticLayerData() {
    const userProject = 'workbench';
    return this.getRequest(`internal/semantic/md?projectId=${userProject}`)
      .toPromise()
      .then(fpGet(`contents.[0].${MODULE_NAME}`));
  }

  getArtifactsForDataSet(semanticId: string) {
    return this.getRequest(`internal/semantic/workbench/${semanticId}`);
  }

  createAnalysis(metricId, type): Promise<Analysis | AnalysisDSL> {
    // return this.createAnalysisNonDSL(metricId, type);
    return DSL_ANALYSIS_TYPES.includes(type)
      ? this.createAnalysisDSL(
          this.newAnalysisModel(metricId, type)
        ).toPromise()
      : this.createAnalysisNonDSL(metricId, type);
  }

  createAnalysisNonDSL(metricId, type): Promise<Analysis> {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      [
        'contents.keys.[0].id',
        metricId || 'c7a32609-2940-4492-afcc-5548b5e5a040'
      ],
      ['contents.keys.[0].analysisType', type]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, params)
      .toPromise()
      .then(fpGet('contents.analyze.[0]'));
  }

  createAnalysisDSL(model: Partial<AnalysisDSL>): Observable<AnalysisDSL> {
    return <Observable<AnalysisDSL>>(
      this._http.post(`${apiUrl}/dslanalysis/`, model).pipe(
        first(),
        map((resp: { analysis: AnalysisDSL }) => resp.analysis)
      )
    );
  }

  newAnalysisModel(
    semanticId: string,
    type: AnalysisType
  ): Partial<AnalysisDSL> {
    return {
      type,
      semanticId,
      name: 'Untitled Analysis',
      description: '',
      createdBy: this._jwtService.getLoginId(),
      customerCode: 'SYNCHRONOSS',
      projectCode: 'workbench',
      module: 'ANALYZE',
      sipQuery: {
        artifacts: [],
        booleanCriteria: 'AND',
        filters: [],
        sorts: [],
        store: {
          dataStore: 'sampleAlias/sample', // make this dynamic
          storageType: 'ES'
        }
      },
      chartOptions: {
        chartType: 'column',
        chartTitle: 'Untitled Analysis',
        isInverted: false,
        legend: {
          align: 'right',
          layout: 'vertical'
        },
        xAxis: {
          title: null
        },
        yAxis: {
          title: null
        }
      }
    };
  }

  getRequest(path): Observable<any> {
    return this._http.get(`${apiUrl}/${path}`);
  }

  postRequest(
    path: string,
    params: Object,
    customHeaders = {}
  ): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        ...customHeaders
      })
    };
    return this._http.post(`${apiUrl}/${path}`, params, httpOptions);
  }
}
