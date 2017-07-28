import forEach from 'lodash/forEach';
import startCase from 'lodash/startCase';
import set from 'lodash/set';
import reduce from 'lodash/reduce';
import trim from 'lodash/trim';
import fpSortBy from 'lodash/fp/sortBy';
import fpGet from 'lodash/fp/get';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';

const EXECUTION_MODES = {
  PREVIEW: 'preview',
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

  function getExecutionData(analysisId, executionId) {
    return $http.get(`${url}/analysis/${analysisId}/executions/${executionId}/data`).then(fpGet(`data.data`));
  }

  function readAnalysis(analysisId) {
    const payload = getRequestParams([
      ['contents.action', 'read'],
      ['contents.keys.[0].id', analysisId]
    ]);
    return $http.post(`${url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
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
      applyAnalysis(model).then(analysis => {
        _executingAnalyses[model.id] = EXECUTION_STATES.SUCCESS;
        deferred.resolve(analysis);
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

  function getCategories() {
    return _menu;
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

  function applyAnalysis(model, mode = EXECUTION_MODES.LIVE) {
    delete model.isScheduled;
    if (mode === EXECUTION_MODES.PREVIEW) {
      model.executionType = EXECUTION_MODES.PREVIEW;
    }

    const payload = getRequestParams([
      ['contents.action', 'execute'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return $http.post(`${url}/analysis`, payload).then(resp => {
      return fpGet(`data.contents.analyze.[1].data`, resp) ||
        fpGet(`data.contents.analyze.[0].data`, resp);
    });
  }

  function getDataBySettings(analysis) {
    return applyAnalysis(analysis, EXECUTION_MODES.PREVIEW).then(data => {
      return {analysis, data};
    });
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  function saveReport(model) {
    model.saved = true;
    const updatePromise = updateAnalysis(model);

    updatePromise.then(analysis => {
      return applyAnalysis(model, EXECUTION_MODES.PREVIEW).then(data => {
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
