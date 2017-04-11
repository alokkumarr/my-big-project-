import intersection from 'lodash/intersection';
import spread from 'lodash/spread';
import isEmpty from 'lodash/isEmpty';
import curry from 'lodash/curry';
import pipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import fpMap from 'lodash/fp/map';
import fpFlatMap from 'lodash/fp/flatMap';
import fpFind from 'lodash/fp/find';
import fpIsEqual from 'lodash/fp/isEqual';
import fpSome from 'lodash/fp/some';
import fpSet from 'lodash/fp/set';
import fpGet from 'lodash/fp/get';

export function AnalyzeService($http, $timeout, $q) {
  'ngInject';

  return {
    getCategories,
    getCategory,
    getMethods,
    getMetrics,
    getArtifacts,
    getAnalyses,
    deleteAnalysis,
    getLastPublishedAnalysis,
    getPublishedAnalysesByAnalysisId,
    getPublishedAnalysisById,
    executeAnalysis,
    getAnalysisById,
    getDataByQuery,
    getSupportedMethods,
    generateQuery,
    getNewPivotAnalysis,
    saveReport,
    setAvailableMetrics: curry(setAvailableItems)(metricMapper, metricHasSupportedMethod),
    setAvailableAnalysisMethods: curry(setAvailableItems)(analysisMethodMapper, isMethodSupported)
  };

  function getAnalyses(category, query) {
    return $http.get('/api/analyze/analyses', {params: {category, query}}).then(fpGet('data'));
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
    return $http.get('/api/analyze/categories').then(fpGet('data'));
  }

  function getCategory(id) {
    return $http.get(`/api/analyze/category/${id}`).then(fpGet('data'));
  }

  function getMethods() {
    return $http.get('/api/analyze/methods').then(fpGet('data'));
  }

  function getMetrics() {
    return $http.get('/api/analyze/metrics').then(fpGet('data'));
  }

  function getArtifacts() {
    return $http.get('/api/analyze/artifacts').then(fpGet('data'));
  }

  function getDataByQuery() {
    return $http.get('/api/analyze/dataByQuery').then(fpGet('data'));
  }

  function getNewPivotAnalysis() {
    return $http.get('/api/analyze/newPivotAnalysis').then(fpGet('data'));
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(fpGet('data'));
  }

  function saveReport(payload) {
    return $http.post('/api/analyze/saveReport', payload).then(fpGet('data'));
  }

  /**
   * Set the disabled attribute on an item, whether it supports the supported analysis methods
   * - the first 2 parameters are used to make specialized functions for metrics and AnalysisMethods
   *
   * @param mapper(actionFn, collection) maps an arbitrary collection setting the "disabled" property
   *        based on a checker function
   * @param checkerFn checks if the disabled attribute should be true or false, based on
   *        whether the current item supports the supported methods or not
   * @param items - the collection on which we do the modifications
   * @param supportedMethods array of supported methods (strings)
   * @returns {*}
   */
  function setAvailableItems(mapperFn, checkerFn, items, supportedMethods) {
    const setDisabled = item => {
      // if there are no supported methods it's probably because no metric was selected
      const nothingSelected = isEmpty(supportedMethods);
      const disabledValue = nothingSelected ? false : !checkerFn(item, supportedMethods);

      return fpSet('disabled', disabledValue, item);
    };

    return mapperFn(setDisabled)(items);
  }

  /**
   * Mapper function for the metrics collection
   * @param actionFn
   */
  function metricMapper(actionFn) {
    return fpMap(metric => actionFn(metric));
  }

  /**
   * Mapper function for the analysis methods collection
   * @param actionFn
   * @returns {*}
   */
  function analysisMethodMapper(actionFn) {
    return pipe(
      fpMap(method => {
        method.children = fpMap(child => actionFn(child))(method.children);
        return method;
      })
    );
  }

  function metricHasSupportedMethod(metric, supportedMethods) {
    return pipe(
      fpFlatMap(fpGet('children')),
      fpMap(fpGet('type')),
      fpSome(type =>
        fpFind(fpIsEqual(type), supportedMethods)
      )
    )(metric.supports);
  }

  function isMethodSupported(method, supportedMethods) {
    return fpFind(fpIsEqual(method.type), supportedMethods);
  }

  /**
   * Intersects all the supported methods of a collection of metrics
   * @param metrics
   * @returns [String] array of supported methods (strings)
   */
  function getSupportedMethods(metrics) {
    return pipe(
      fpFilter(metric => metric.checked === true),
      fpMap(fpGet('supports')),
      fpMap(supports =>
        pipe(
          fpFlatMap(fpGet('children')),
          fpMap(fpGet('type'))
        )(supports)),
      spread(intersection)
    )(metrics);
  }
}
