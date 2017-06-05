import omit from 'lodash/omit';
import keys from 'lodash/keys';
import forEach from 'lodash/forEach';
import set from 'lodash/set';
import fpMap from 'lodash/fp/map';
import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';

export function AnalyzeService($http, $timeout, $q, AppConfig, JwtService, toastMessage, $translate) {
  'ngInject';

  const MODULE_NAME = 'ANALYZE';
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
    chartBe2Fe,
    chartFe2Be,
    createAnalysis,
    deleteAnalysis,
    executeAnalysis,
    generateQuery,
    getAnalyses,
    getAnalysesFor,
    getAnalysisById,
    getArtifacts,
    getCategories,
    getCategory,
    getDataByQuery,
    getDataBySettings,
    getLastPublishedAnalysis,
    getMethods,
    getPivotData,
    getPublishedAnalysesByAnalysisId,
    getPublishedAnalysisById,
    getSemanticLayerData,
    isExecuting,
    readAnalysis,
    saveReport,
    searchAnalyses,
    updateMenu
  };

  function updateMenu(menu) {
    _menuResolver(menu);
  }

  function isExecuting(analysisId) {
    return Boolean(_executingAnalyses[analysisId]);
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
      return $http.post(`${url}/analysis`, payload).then(fpGet('data.contents.analyze'));
    }).then(analyses => {
      return analyses.slice(0, 10);
    });
  }

  function searchAnalyses(analyses, searchTerm) {
    const term = searchTerm.toUpperCase();
    const matchIn = item => {
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };

    return filter(analyses, item => {
      return matchIn(item.name) ||
        matchIn(item.type) ||
        matchIn(item.metricName);
    });
  }

  function getAnalyses(category, query) {
    return $http.get('/api/analyze/analyses', {params: {category, query}})
      .then(fpGet('data'))
      .then(fpMap(analysis => {
        if (analysis.type === 'chart') {
          return chartBe2Fe(analysis);
        }
        return analysis;
      }));
  }

  function getPublishedAnalysesByAnalysisId(id) {
    return $http.get(`${url}/analysis/${id}/results`).then(fpGet(`data.results`));
  }

  function getLastPublishedAnalysis(id) {
    return $http.get(`/api/analyze/lastPublishedAnalysis/${id}`).then(fpGet('data'));
  }

  function getPublishedAnalysisById(id) {
    return $http.get(`/api/analyze/publishedAnalysis/${id}`).then(fpGet('data'));
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
    const isOngoingExecution = keys(_executingAnalyses).length > 0;

    if (isOngoingExecution) {
      $translate('ERROR_ANALYSIS_ALREADY_EXECUTING').then(msg => {
        toastMessage.error(msg);
        deferred.reject(msg);
      });

    } else {
      $translate('INFO_ANALYSIS_SUBMITTED').then(msg => {
        toastMessage.info(msg);
      });
      _executingAnalyses[model.id] = true;
      applyAnalysis(model).then(analysis => {
        delete _executingAnalyses[model.id];
        deferred.resolve(analysis);
      }, err => {
        delete _executingAnalyses[model.id];
        deferred.reject(err);
      });
    }

    return deferred.promise;
  }

  function getAnalysisById(id) {
    return $http.get(`/api/analyze/byId/${id}`).then(fpGet('data'));
  }

  function deleteAnalysis(id) {
    return $http.delete(`/api/analyze/byId/${id}`).then(fpGet('data'));
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

  function getArtifacts() {
    return $http.get('/api/analyze/artifacts').then(fpGet('data'));
  }

  function updateAnalysis(model) {
    const payload = getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return $http.post(`${url}/analysis`, payload).then(fpGet(`data.contents.analyze.[0]`));
  }

  function applyAnalysis(model) {
    const payload = getRequestParams([
      ['contents.action', 'execute'],
      ['contents.keys.[0].id', model.id],
      ['contents.keys.[0].type', model.type],
      ['contents.analyze', [model]]
    ]);
    return $http.post(`${url}/analysis`, payload).then(fpGet(`data.contents.analyze.[1].data`));
  }

  function getDataBySettings(model) {
    return updateAnalysis(model).then(analysis => {
      return applyAnalysis(model).then(data => {
        return {analysis, data};
      });
    });
  }

  function getDataByQuery() {
    return $http.get('/api/analyze/dataByQuery').then(fpGet('data'));
  }

  function getPivotData() {
    return $http.get('/api/analyze/pivotData').then(fpGet('data.aggregations.filtered.row_level_1'));
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  function saveReport(model) {
    model.saved = true;
    return updateAnalysis(model);
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

  /**
   * Converts chart type analysis from backend
   * to a format usable on front-end
   */
  function chartBe2Fe(source) {
    const result = omit(source, ['_id', 'chart_type', 'plot_variant']);
    result.id = source._id || source.id;
    result.chartType = source.chart_type || source.chartType;
    result.plotVariant = source.plot_variant || source.plotVariant;

    return result;
  }

  function chartFe2Be() {
    // TODO
  }
}
