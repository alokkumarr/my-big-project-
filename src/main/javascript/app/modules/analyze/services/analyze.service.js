import omit from 'lodash/omit';
import fpMap from 'lodash/fp/map';
import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';

export function AnalyzeService($http, $timeout, $q) {
  'ngInject';

  let _menuResolver = null;
  const _menu = new Promise(resolve => {
    _menuResolver = resolve;
  });

  return {
    createAnalysis,
    getCategories,
    getCategory,
    getMethods,
    getArtifacts,
    getAnalyses,
    deleteAnalysis,
    getLastPublishedAnalysis,
    getPublishedAnalysesByAnalysisId,
    getPublishedAnalysisById,
    executeAnalysis,
    getAnalysisById,
    getDataByQuery,
    getDataBySettings,
    generateQuery,
    saveReport,
    getSemanticLayerData,
    chartBe2Fe,
    chartFe2Be,
    updateMenu,
    getPivotData,
    getAnalysesFor
  };

  function updateMenu(menu) {
    _menuResolver(menu);
  }

  function getAnalysesFor(subCategoryId, opts = {}) {
    /* Wait until the menu has been loaded. The menu payload contains the
       analyses list from which we'll load the result for this function. */
    return _menu.then(menu => {
      const subCategories = flatMap(menu, category => category.children);
      const subCategory = find(subCategories, sc => sc.id === subCategoryId);
      let items = fpGet('data.list', subCategory) || [];

      if (fpGet('filter', opts)) {
        items = searchAnalyses(items, opts.filter);
      }

      return items;
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
    return $http.get(`/api/analyze/publishedAnalyses/${id}`).then(fpGet('data'));
  }

  function getLastPublishedAnalysis(id) {
    return $http.get(`/api/analyze/lastPublishedAnalysis/${id}`).then(fpGet('data'));
  }

  function getPublishedAnalysisById(id) {
    return $http.get(`/api/analyze/publishedAnalysis/${id}`).then(fpGet('data'));
  }

  function executeAnalysis(analysisId) {
    return $q(resolve => {
      $timeout(() => {
        resolve({
          publishedAnalysisId: 3,
          analysisId
        });
      }, 0);
    });
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
      return find(subCategories, sc => sc.id === id);
    });
  }

  function getMethods() {
    return $http.get('/api/analyze/methods').then(fpGet('data'));
  }

  function getArtifacts() {
    return $http.get('/api/analyze/artifacts').then(fpGet('data'));
  }

  function getDataBySettings(model) {
    const payload = {data: [], links: [], contents: {
      action: 'execute',
      keys: [{id: model.id, module: 'analyze', type: model.type}],
      analyze: [model]
    }};
    return $http.post('/api/analyze/apply', payload).then(fpGet('data.data'));
  }

  function getDataByQuery() {
    return $http.get('/api/analyze/dataByQuery').then(fpGet('data'));
  }

  function getPivotData() {
    return $http.get('/api/analyze/pivotData').then(fpGet('data'));
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  function saveReport(payload) {
    return $http.post('/api/analyze/saveReport', payload).then(fpGet('data'));
  }

  function getSemanticLayerData() {
    return $http.get('/api/analyze/semanticLayerData').then(fpGet('data'));
  }

  function createAnalysis(metricId, type) {
    const payload = {
      action: 'read',
      keys: [{
        type: 'semantic',
        analysisType: type,
        module: 'analyze',
        id: metricId
      }]
    };
    return $http.post('/api/analyze/createAnalysis', payload).then(fpGet('data.contents.analyze.[0]'));
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
