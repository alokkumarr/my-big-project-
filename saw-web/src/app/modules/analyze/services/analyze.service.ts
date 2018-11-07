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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Analysis } from '../../../models';

import { JwtService } from '../../../common/services';
import { ToastService, MenuService } from '../../../common/services';
import AppConfig from '../../../../../appConfig';

const apiUrl = AppConfig.api.url;

interface ExecutionRequestOptions {
  take?: number;
  skip?: number;
  executionType?: string;
  forcePaginate?: boolean;
  analysisType?: string;
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
  ) {}

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
    return this.getRequest(
      `exports/${executionId}/executions/${analysisId}/data?analysisType=${analysisType}${onetimeExecution}`
    );
  }

  getAnalysesFor(subCategoryId /* , opts = {} */) {
    const payload = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.keys.[0].categoryId', subCategoryId]
    ]);
    return <Promise<Analysis[]>>this.postRequest(`analysis`, payload)
      .then(fpGet('contents.analyze'))
      .then(fpSortBy([analysis => -(analysis.createdTimestamp || 0)]));
  }

  getPublishedAnalysesByAnalysisId(id) {
    return <Promise<Analysis[]>>this.getRequest(`analysis/${id}/executions`)
      .then(fpGet(`executions`))
      .then(fpSortBy([obj => -obj.finished]));
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
    const page = floor(options.skip / options.take) + 1;
    const path = `analysis/${analysisId}/executions/data`;
    const queryParams = `page=${page}&pageSize=${options.take}&analysisType=${
      options.analysisType
    }`;

    return this.getRequest(`${path}?${queryParams}`).then(resp => {
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
    const path = `analysis/${analysisId}/executions/${executionId}/data`;
    const queryParams = `?page=${page}&pageSize=${options.take}&analysisType=${
      options.analysisType
    }${onetimeExecution}`;
    const url = `${path}${queryParams}`;
    return this.getRequest(url).then(resp => {
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

  readAnalysis(analysisId) {
    const payload = this.getRequestParams([
      ['contents.action', 'read'],
      ['contents.keys.[0].id', analysisId]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, payload).then(
      fpGet(`contents.analyze.[0]`)
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
        return this.postRequest(`scheduler/schedule`, schedule);
      case 'exist':
        return this.postRequest(`scheduler/update`, schedule);
      case 'delete':
        return this.postRequest(`scheduler/delete`, schedule);
      default:
    }
  }

  getCronDetails(requestBody) {
    return this.postRequest(`scheduler/fetchJob`, requestBody);
  }

  getAllCronJobs(model) {
    return this.postRequest(`scheduler/jobs`, model);
  }

  getlistFTP(custCode) {
    return this.postRequest(`exports/listFTP`, custCode);
  }

  deleteAnalysis(model) {
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
    return this.postRequest(`analysis`, payload);
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
    return this.getRequest('/api/analyze/methods');
  }

  updateAnalysis(model): Promise<Analysis> {
    delete model.isScheduled;
    delete model.executionType;
    const payload = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, payload).then(
      fpGet(`contents.analyze.[0]`)
    );
  }

  applyAnalysis(
    model,
    mode = EXECUTION_MODES.LIVE,
    options: ExecutionRequestOptions = {}
  ) {
    delete model.isScheduled;

    model.executionType = mode;

    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;

    const payload = this.getRequestParams([
      ['contents.action', 'execute'],
      ['contents.executedBy', this._jwtService.getLoginId()],
      ['contents.page', page],
      ['contents.pageSize', options.take],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return this.postRequest(`analysis`, payload).then(resp => {
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
    return this.postRequest('/api/analyze/generateQuery', payload);
  }

  saveReport(model) {
    model.saved = true;
    const updatePromise = this.updateAnalysis(model);
    return updatePromise;
  }

  getSemanticLayerData() {
    const userProject = 'workbench';
    return this.getRequest(
      `internal/semantic/md?projectId=${userProject}`
    ).then(fpGet(`contents.[0].${MODULE_NAME}`));
  }

  createAnalysis(metricId, type): Promise<Analysis> {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      [
        'contents.keys.[0].id',
        metricId || 'c7a32609-2940-4492-afcc-5548b5e5a040'
      ],
      ['contents.keys.[0].analysisType', type]
    ]);
    return <Promise<Analysis>>this.postRequest(`analysis`, params).then(
      fpGet('contents.analyze.[0]')
    );
  }

  getRequest(path) {
    return this._http.get(`${apiUrl}/${path}`).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this._http
      .post(`${apiUrl}/${path}`, params, httpOptions)
      .toPromise();
  }
}
