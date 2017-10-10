import * as forEach from 'lodash/forEach';
import * as floor from 'lodash/floor';
import * as startCase from 'lodash/startCase';
import * as set from 'lodash/set';
import * as reduce from 'lodash/reduce';
import * as trim from 'lodash/trim';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGet from 'lodash/fp/get';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as flatMap from 'lodash/flatMap';
import * as cloneDeep from 'lodash/cloneDeep';

const EXECUTION_MODES = {
  PREVIEW: 'interactive',
  LIVE: 'live'
};

const EXECUTION_STATES = {
  SUCCESS: 'success',
  ERROR: 'error',
  EXECUTING: 'executing'
};

export function AnalyzeService($http, $timeout, $q, AppConfig, JwtService, toastMessage, $translate) {
  'ngInject';

  const MODULE_NAME = 'ANALYZE';

  const SCHEDULE_B2F_DICTIONARY = {
    weekly: 'weeks',
    daily: 'days'
  };

  const SCHEDULE_DAYS = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];

  const url = AppConfig.api.url;
  let _menuResolver = null;
  const _menu = new Promise(resolve => {
    _menuResolver = resolve;
  });

  /* Maintains a list of analyses being executed.
     Allows showing of execution badge across pages and possibly block
     executions until current ones get completed */
  const _executingAnalyses = {};

  return {
    createAnalysis,
    deleteAnalysis,
    didExecutionFail,
    executeAnalysis,
    generateQuery,
    getAnalysesFor,
    getCategories,
    getCategory,
    getDataBySettings,
    getExecutionData,
    getMethods,
    getPublishedAnalysesByAnalysisId,
    getSemanticLayerData,
    isExecuting,
    previewExecution,
    publishAnalysis,
    readAnalysis,
    saveReport,
    scheduleToString,
    updateMenu
  };

  function updateMenu(menu) {
    _menuResolver(menu);
  }

  function isExecuting(analysisId) {
    return EXECUTION_STATES.EXECUTING === _executingAnalyses[analysisId];
  }

  function didExecutionFail(analysisId) {
    return EXECUTION_STATES.ERROR === _executingAnalyses[analysisId];
  }

  function scheduleToString(schedule) {
    let result;
    if (schedule.repeatInterval === 1) {
      result = startCase(schedule.repeatUnit);
    } else {
      result = `Every ${schedule.repeatInterval} ${SCHEDULE_B2F_DICTIONARY[schedule.repeatUnit]}`;
    }

    if (schedule.repeatUnit === 'weekly') {
      const dayString = trim(reduce(SCHEDULE_DAYS, (res, day) => {
        res.push(schedule.repeatOnDaysOfWeek[day] ? startCase(day.slice(0, 2)) : '');
        return res;
      }, []).join(' '));

      result += dayString ? ` (${trim(dayString)})` : '';
    }
    return result;
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
  function getRequestParams(params = []) {
    const reqParams = JwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });

    return reqParams;
  }

  function getAnalysesFor(subCategoryId/* , opts = {} */) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return _menu.then(() => {

      const payload = getRequestParams([
        ['contents.action', 'search'],
        ['contents.keys.[0].categoryId', subCategoryId]
      ]);
      return $http.post(`${url}/analysis`, payload);
    })
      .then(fpGet('data.contents.analyze'))
      .then(fpSortBy([analysis => -(analysis.createdTimestamp || 0)]));
  }

  function getPublishedAnalysesByAnalysisId(id) {
    return $http.get(`${url}/analysis/${id}/executions`)
      .then(fpGet(`data.executions`))
      .then(fpSortBy([obj => -obj.finished]));
  }

  function getExecutionData(analysisId, executionId, options = {}) {
    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;
    return $http.get(
      `${url}/analysis/${analysisId}/executions/${executionId}/data?page=${page}&pageSize=${options.take}&analysisType=${options.analysisType}`
    ).then(resp => {
      const data = fpGet(`data.data`, resp);
      const count = fpGet(`data.totalRows`, resp);
      return {data, count};
    });
  }

  function readAnalysis(analysisId) {
    const payload = getRequestParams([
      ['contents.action', 'read'],
      ['contents.keys.[0].id', analysisId]
    ]);
    return $http.post(`${url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  function previewExecution(model, options = {}) {
    return applyAnalysis(model, EXECUTION_MODES.PREVIEW, options);
  }

  function executeAnalysis(model) {
    const deferred = $q.defer();

    if (isExecuting(model.id)) {
      $translate('ERROR_ANALYSIS_ALREADY_EXECUTING').then(msg => {
        toastMessage.error(msg);
        deferred.reject(msg);
      });

    } else {
      $translate('INFO_ANALYSIS_SUBMITTED').then(msg => {
        toastMessage.info(msg);
      });
      _executingAnalyses[model.id] = EXECUTION_STATES.EXECUTING;
      applyAnalysis(model).then(({data}) => {
        _executingAnalyses[model.id] = EXECUTION_STATES.SUCCESS;
        deferred.resolve(data);
      }, err => {
        _executingAnalyses[model.id] = EXECUTION_STATES.ERROR;
        deferred.reject(err);
      });
    }

    return deferred.promise;
  }

  function publishAnalysis(model, execute = false) {
    return updateAnalysis(model).then(analysis => {
      if (execute) {
        executeAnalysis(model);
      }
      return analysis;
    });
  }

  function deleteAnalysis(model) {
    if (!JwtService.hasPrivilege('DELETE', {
      subCategoryId: model.categoryId,
      creatorId: model.userId
    })) {
      return $q.reject(new Error('Access denied.'));
    }
    const payload = getRequestParams([
      ['contents.action', 'delete'],
      ['contents.keys.[0].id', model.id]
    ]);
    return $http.post(`${url}/analysis`, payload);
  }

  function getCategories(privilege) {
    if (!privilege) {
      return _menu;
    }

    return _menu.then(menu => {
      const menuClone = cloneDeep(menu);
      forEach(menuClone, menuFeature => {
        menuFeature.children = filter(menuFeature.children, menuSubFeature => {
          return JwtService.hasPrivilege(privilege, {subCategoryId: menuSubFeature.id});
        });
      });
      return menuClone;
    });
  }

  function getCategory(id) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return _menu.then(menu => {
      const subCategories = flatMap(menu, category => category.children);
      return find(subCategories, sc => sc.id.toString() === id);
    });
  }

  function getMethods() {
    return $http.get('/api/analyze/methods').then(fpGet('data'));
  }

  function updateAnalysis(model) {
    delete model.isScheduled;
    const payload = getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return $http.post(`${url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  function applyAnalysis(model, mode = EXECUTION_MODES.LIVE, options = {}) {
    delete model.isScheduled;
    if (mode === EXECUTION_MODES.PREVIEW) {
      model.executionType = EXECUTION_MODES.PREVIEW;
    }

    if (model.type === 'report' && model.edit === true) {
      toastMessage.error('SQL mode is no longer supported. Please create a new report in designer mode.');
      return $q.resolve({data: [], count: 0});
    }

    options.skip = options.skip || 0;
    options.take = options.take || 10;
    const page = floor(options.skip / options.take) + 1;

    const payload = getRequestParams([
      ['contents.action', 'execute'],
      ['contents.page', page],
      ['contents.pageSize', options.take],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return $http.post(`${url}/analysis`, payload).then(resp => {
      return {
        data: fpGet(`data.contents.analyze.[0].data`, resp),
        count: fpGet(`data.contents.analyze.[0].totalRows`, resp)
      };
    });
  }

  function getDataBySettings(analysis) {
    return applyAnalysis(analysis, EXECUTION_MODES.PREVIEW).then(({data, count}) => {
      return {analysis, data, count};
    });
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  function saveReport(model) {
    model.saved = true;
    const updatePromise = updateAnalysis(model);

    updatePromise.then(analysis => {
      return applyAnalysis(model, EXECUTION_MODES.PREVIEW).then(({data}) => {
        return {analysis, data};
      });
    });
    return updatePromise;
  }

  function getSemanticLayerData() {
    const params = getRequestParams([
      ['contents.action', 'search'],
      ['contents.select', 'headers'],
      ['contents.context', 'Semantic']
    ]);
    return $http.post(`${url}/md`, params).then(fpGet(`data.contents.[0].${MODULE_NAME}`));
  }

  function createAnalysis(metricId, type) {
    const params = getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', metricId || 'c7a32609-2940-4492-afcc-5548b5e5a040'],
      ['contents.keys.[0].analysisType', type]
    ]);
    return $http.post(`${url}/analysis`, params).then(fpGet('data.contents.analyze.[0]'));
  }
}
