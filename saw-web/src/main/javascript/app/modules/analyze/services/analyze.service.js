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

const EXECUTION_MODES = {
  PREVIEW: 'preview',
  LIVE: 'live'
};

const EXECUTION_STATES = {
  SUCCESS: 'success',
  ERROR: 'error',
  EXECUTING: 'executing'
};

const MODULE_NAME = 'ANALYZE';

export class AnalyzeService {
  constructor($http, $q, AppConfig, JwtService, toastMessage, $translate) {
    'ngInject';

    this._$http = $http;
    this._$q = $q;
    this._JwtService = JwtService;
    this._toastMessage = toastMessage;
    this._$translate = $translate;

    this.url = AppConfig.api.url;
    this._menuResolver = null;
    this._menu = new Promise(resolve => {
      this._menuResolver = resolve;
    });
    this._executingAnalyses = {};
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
    const reqParams = this._JwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });

    return reqParams;
  }

  getExportData(analysisId, executionId) {
    return this._$http.get(`${this.url}/exports/${executionId}/executions/${analysisId}/data`)
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
      return this._$http.post(`${this.url}/analysis`, payload);
    })
      .then(fpGet('data.contents.analyze'))
      .then(fpSortBy([analysis => -(analysis.createdTimestamp || 0)]));
  }

  getPublishedAnalysesByAnalysisId(id) {
    return this._$http.get(`${this.url}/analysis/${id}/executions`)
      .then(fpGet(`data.executions`))
      .then(fpSortBy([obj => -obj.finished]));
  }

  forcePagination(data, options = {}) {
    if (isEmpty(data) || !(has(options, 'skip') || has(options, 'take'))) {
      return data;
    }

    return data.slice(options.skip, options.skip + options.take);
  }

  getExecutionData(analysisId, executionId, options = {}) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;
    return this._$http.get(
      `${this.url}/analysis/${analysisId}/executions/${executionId}/data?page=${page}&pageSize=${options.take}&analysisType=${options.analysisType}`
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
    return this._$http.post(`${this.url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  previewExecution(model, options = {}) {
    return this.applyAnalysis(model, EXECUTION_MODES.PREVIEW, options);
  }

  executeAnalysis(model) {
    const deferred = this._$q.defer();

    if (this.isExecuting(model.id)) {
      this._$translate('ERROR_ANALYSIS_ALREADY_EXECUTING').then(msg => {
        this._toastMessage.error(msg);
        deferred.reject(msg);
      });

    } else {
      this._$translate('INFO_ANALYSIS_SUBMITTED').then(msg => {
        this._toastMessage.info(msg);
      });
      this._executingAnalyses[model.id] = EXECUTION_STATES.EXECUTING;
      this.applyAnalysis(model).then(({data}) => {
        this._executingAnalyses[model.id] = EXECUTION_STATES.SUCCESS;
        deferred.resolve(data);
      }, err => {
        this._executingAnalyses[model.id] = EXECUTION_STATES.ERROR;
        deferred.reject(err);
      });
    }

    return deferred.promise;
  }

  publishAnalysis(model, execute = false) {
    if (model.schedule.scheduleState === 'new') {
      this._$http.post(`${this.url}/scheduler/schedule`, model.schedule).then(fpGet(`data.contents.analyze.[0]`));
    }
    if (model.schedule.scheduleState === 'exist') {
      this._$http.post(`${this.url}/scheduler/update`, model.schedule).then(fpGet(`data.contents.analyze.[0]`));
    }
    if (model.schedule.scheduleState === 'delete') {
      this._$http.post(`${this.url}/scheduler/delete`, model.schedule);
    }

    return this.updateAnalysis(model).then(analysis => {
      if (execute) {
        this.executeAnalysis(model);
      }
      return analysis;
    });
  }

  getCronDetails(requestBody) {
    const deferred = this._$q.defer();
    this._$http.post(`${this.url}/scheduler/fetchJob`, requestBody).then(({data}) => {
      deferred.resolve(data);
    }, err => {
      deferred.reject(err);
    });
    return deferred.promise;
  }

  getAllCronJobs(model) {
    const deferred = this._$q.defer();
    this._$http.post(`${this.url}/scheduler/jobs`, model).then(({data}) => {
      deferred.resolve(data);
    }, err => {
      deferred.reject(err);
    });
    return deferred.promise;
  }

  deleteAnalysis(model) {
    if (!this._JwtService.hasPrivilege('DELETE', {
      subCategoryId: model.categoryId,
      creatorId: model.userId
    })) {
      return this._$q.reject(new Error('Access denied.'));
    }
    const payload = this.getRequestParams([
      ['contents.action', 'delete'],
      ['contents.keys.[0].id', model.id]
    ]);
    return this._$http.post(`${this.url}/analysis`, payload);
  }

  getCategories(privilege) {
    if (!privilege) {
      return this._menu;
    }

    return this._menu.then(menu => {
      const menuClone = cloneDeep(menu);
      forEach(menuClone, menuFeature => {
        menuFeature.children = filter(menuFeature.children, menuSubFeature => {
          return this._JwtService.hasPrivilege(privilege, {subCategoryId: menuSubFeature.id});
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
    return this._$http.get('/api/analyze/methods').then(fpGet('data'));
  }

  updateAnalysis(model) {
    delete model.isScheduled;
    const payload = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return this._$http.post(`${this.url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  applyAnalysis(model, mode = EXECUTION_MODES.LIVE, options = {}) {
    delete model.isScheduled;
    if (mode === EXECUTION_MODES.PREVIEW) {
      model.executionType = EXECUTION_MODES.PREVIEW;
    } else {
      delete model.executionType;
    }

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
    return this._$http.post(`${this.url}/analysis`, payload).then(resp => {
      return {
        data: fpGet(`data.contents.analyze.[0].data`, resp),
        count: fpGet(`data.contents.analyze.[0].totalRows`, resp)
      };
    });
  }

  getDataBySettings(analysis) {
    return this.applyAnalysis(analysis, EXECUTION_MODES.PREVIEW).then(({data, count}) => {
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
      return {analysis, data, count};
    });
  }

  generateQuery(payload) {
    return this._$http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  saveReport(model) {
    model.saved = true;
    const updatePromise = this.updateAnalysis(model);

    updatePromise.then(analysis => {
      return this.applyAnalysis(model, EXECUTION_MODES.PREVIEW).then(({data}) => {
        return {analysis, data};
      });
    });
    return updatePromise;
  }

  getSemanticLayerData() {
    const params = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.select', 'headers'],
      ['contents.context', 'Semantic']
    ]);
    return this._$http.post(`${this.url}/md`, params).then(fpGet(`data.contents.[0].${MODULE_NAME}`));
  }

  createAnalysis(metricId, type) {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', metricId || 'c7a32609-2940-4492-afcc-5548b5e5a040'],
      ['contents.keys.[0].analysisType', type]
    ]);
    return this._$http.post(`${this.url}/analysis`, params).then(fpGet('data.contents.analyze.[0]'));
  }
}
