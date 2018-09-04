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

import { JwtService } from '../../../../login/services/jwt.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import AppConfig from '../../../../../../../appConfig';

const apiUrl = AppConfig.api.url;

type ExecutionRequestOptions = {
  take?: number,
  skip?: number,
  executionType?: string,
  forcePaginate?: boolean,
  analysisType?: string
};
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

  _menuResolver: any;
  _menu: any;
  _executingAnalyses: Object = {};
  _executions: Object = {};

  constructor(
    private _http : HttpClient,
    private _jwtService: JwtService,
    private _toastMessage: ToastService
  ) {
    this._menu = new Promise(resolve => {
      this._menuResolver = resolve;
    });
  }

  /* Maintains a list of analyses being executed.
     Allows showing of execution badge across pages and possibly block
     executions until current ones get completed */

  updateMenu(menu) {
    this._menuResolver(menu);
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

  getExportData(analysisId, executionId, analysisType, executionType = EXECUTION_DATA_MODES.NORMAL) {
    const onetimeExecution = executionType === EXECUTION_DATA_MODES.ONETIME ? '&executionType=onetime' : '';
    return this.getRequest(`exports/${executionId}/executions/${analysisId}/data?analysisType=${analysisType}${onetimeExecution}`)
      .then(fpGet('data.data'));
  }

  getAnalysesFor(subCategoryId/* , opts = {} */) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return this._menu.then(() => {
      const payload = this.getRequestParams([
        ['contents.action', 'search'],
        ['contents.keys.[0].categoryId', subCategoryId]
      ]);
      return this.postRequest(`analysis`, payload);
    })
      .then(fpGet('data.contents.analyze'))
      .then(fpSortBy([analysis => -(analysis.createdTimestamp || 0)]));
  }

  getPublishedAnalysesByAnalysisId(id) {
    return this.getRequest(`analysis/${id}/executions`)
      .then(fpGet(`data.executions`))
      .then(fpSortBy([obj => -obj.finished]));
  }

  forcePagination(data, options: ExecutionRequestOptions = {}) {
    if (isEmpty(data) || !(has(options, 'skip') || has(options, 'take'))) {
      return data;
    }

    return data.slice(options.skip, options.skip + options.take);
  }

  getExecutionData(analysisId, executionId, options: ExecutionRequestOptions = {}) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;
    const onetimeExecution = options.executionType === EXECUTION_DATA_MODES.ONETIME ? '&executionType=onetime' : '';
    return this.getRequest(
      `analysis/${analysisId}/executions/${executionId}/data?page=${page}&pageSize=${options.take}&analysisType=${options.analysisType}${onetimeExecution}`
    ).then(resp => {
      const data = fpGet(`data.data`, resp);
      const count = fpGet(`data.totalRows`, resp) || data.length;
      return {data: options.forcePaginate ? this.forcePagination(data, options) : data, count};
    });
  }

  readAnalysis(analysisId) {
    const payload = this.getRequestParams([
      ['contents.action', 'read'],
      ['contents.keys.[0].id', analysisId]
    ]);
    return this.postRequest(`analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  previewExecution(model, options = {}) {
    return this.applyAnalysis(model, EXECUTION_MODES.PREVIEW, options);
  }

  executeAnalysis(model, executionType = EXECUTION_MODES.LIVE) {
    const promise = new Promise((resolve, reject) => {
      if (this.isExecuting(model.id)) {
        const msg = 'Analysis is executing already. Please try again in some time.';
        this._toastMessage.error(msg);
        reject(msg);
      } else {
        this._executions[model.id] = promise;

        this._executingAnalyses[model.id] = EXECUTION_STATES.EXECUTING;
        this.applyAnalysis(model, executionType).then(({data, executionId, executionType, count}) => {
          this._executingAnalyses[model.id] = EXECUTION_STATES.SUCCESS;
          resolve({data, executionId, executionType, count});
        }, err => {
          this._executingAnalyses[model.id] = EXECUTION_STATES.ERROR;
          reject(err);
        });
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
    return this.postRequest(`scheduler/fetchJob`, requestBody).then(({data}) => data);
  }

  getAllCronJobs(model) {
    return this.postRequest(`scheduler/jobs`, model).then(({data}) => data);
  }

  getlistFTP(custCode) {
    return this.postRequest(`exports/listFTP`, custCode);
  }

  deleteAnalysis(model) {
    if (!this._jwtService.hasPrivilege('DELETE', {
      subCategoryId: model.categoryId,
      creatorId: model.userId
    })) {
      return Promise.reject(new Error('Access denied.'));
    }
    const payload = this.getRequestParams([
      ['contents.action', 'delete'],
      ['contents.keys.[0].id', model.id]
    ]);
    return this.postRequest(`analysis`, payload);
  }

  getCategories(privilege) {
    if (!privilege) {
      return this._menu;
    }

    return this._menu.then(menu => {
      const menuClone = cloneDeep(menu);
      forEach(menuClone, menuFeature => {
        menuFeature.children = filter(menuFeature.children, menuSubFeature => {
          return this._jwtService.hasPrivilege(privilege, {subCategoryId: menuSubFeature.id});
        });
      });
      return menuClone;
    });
  }

  getCategory(id) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return this._menu.then(menu => {
      const subCategories = flatMap(menu, category => category.children);
      return find(subCategories, sc => sc.id.toString() === id);
    });
  }

  getMethods() {
    return this.getRequest('/api/analyze/methods').then(fpGet('data'));
  }

  updateAnalysis(model) {
    delete model.isScheduled;
    delete model.executionType;
    const payload = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return this.postRequest(`analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  applyAnalysis(model, mode = EXECUTION_MODES.LIVE, options: ExecutionRequestOptions = {}) {
    delete model.isScheduled;

    model.executionType = mode;

    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;

    const payload = this.getRequestParams([
      ['contents.action', 'execute'],
      ['contents.page', page],
      ['contents.pageSize', options.take],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return this.postRequest(`analysis`, payload).then(resp => {
      return {
        data: fpGet(`data.contents.analyze.[0].data`, resp),
        executionId: fpGet(`data.contents.analyze.[0].executionId`, resp),
        executionType: mode,
        count: fpGet(`data.contents.analyze.[0].totalRows`, resp)
      };
    });
  }

  getDataBySettings(analysis, mode = EXECUTION_MODES.PREVIEW, options = {}) {
    return this.applyAnalysis(analysis, mode, options).then(({data, executionId, executionType, count}) => {
      // forEach(analysis.artifacts[0].columns, column => {
      //   column.columnName = this.getColumnName(column.columnName);
      // });

      // forEach(analysis.sqlBuilder.dataFields, field => {
      //   field.columnName = this.getColumnName(field.columnName);
      // });

      // forEach(data, row => {
      //   forEach(row, (value, key) => {
      //     key = this.getColumnName(key);
      //     data[key] = value;
      //   });
      // });
      return {analysis, data, executionId, executionType, count};
    });
  }

  generateQuery(payload) {
    return this.postRequest('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  saveReport(model) {
    model.saved = true;
    const updatePromise = this.updateAnalysis(model);
    return updatePromise;
  }

  getSemanticLayerData() {
    const params = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.select', 'headers'],
      ['contents.context', 'Semantic']
    ]);
    return this.postRequest(`md`, params).then(fpGet(`data.contents.[0].${MODULE_NAME}`));
  }

  createAnalysis(metricId, type) {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', metricId || 'c7a32609-2940-4492-afcc-5548b5e5a040'],
      ['contents.keys.[0].analysisType', type]
    ]);
    return this.postRequest(`analysis`, params).then(fpGet('data.contents.analyze.[0]'));
  }

  getRequest(path) {
    return this._http.get(`${apiUrl}/${path}`).toPromise();
  }

  postRequest(path: string, params: Object) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
    return this._http.post(`${apiUrl}/${path}`, params, httpOptions).toPromise();
  }

}
