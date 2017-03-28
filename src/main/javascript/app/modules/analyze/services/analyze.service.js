import intersection from 'lodash/intersection';
import spread from 'lodash/spread';
import isEmpty from 'lodash/isEmpty';
import curry from 'lodash/curry';
import pipe from 'lodash/fp/pipe';
import filter from 'lodash/fp/filter';
import fpMap from 'lodash/fp/map';
import fpFlatMap from 'lodash/fp/flatMap';
import find from 'lodash/find';
import isEqual from 'lodash/fp/isEqual';
import some from 'lodash/fp/some';
import set from 'lodash/fp/set';
import get from 'lodash/fp/get';

export function AnalyzeService($http, $timeout, $q) {
  'ngInject';

  return {
    getCategories,
    getCategory,
    getMethods,
    getMetrics,
    getArtifacts,
    getAnalyses,
    getLastPublishedAnalysis,
    getPublishedAnalysesByAnalysisId,
    getPublishedAnalysisById,
    executeAnalysis,
    getAnalysisById,
    getDataByQuery,
    getSupportedMethods,
    generateQuery,
    getPivotData,
    saveReport,
    setAvailableMetrics: curry(setAvailableItems)(metricMapper, metricHasSupportedMethod),
    setAvailableAnalysisMethods: curry(setAvailableItems)(analysisMethodMapper, isMethodSupported)
  };

  function getAnalyses(category, query) {
    return $http.get('/api/analyze/analyses', {params: {category, query}}).then(get('data'));
  }

  function getPublishedAnalysesByAnalysisId(id) {
    return $http.get(`/api/analyze/publishedAnalyses/${id}`).then(get('data'));
  }

  function getLastPublishedAnalysis(id) {
    return $http.get(`/api/analyze/lastPublishedAnalysis/${id}`).then(get('data'));
  }

  function getPublishedAnalysisById(id) {
    return $http.get(`/api/analyze/publishedAnalysis/${id}`).then(get('data'));
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
    return $http.get(`/api/analyze/byId/${id}`).then(get('data'));
  }

  function getCategories() {
    return $http.get('/api/analyze/categories').then(get('data'));
  }

  function getCategory(id) {
    return $http.get(`/api/analyze/category/${id}`).then(get('data'));
  }

  function getMethods() {
    return $http.get('/api/analyze/methods').then(get('data'));
  }

  function getMetrics() {
    return $http.get('/api/analyze/metrics').then(get('data'));
  }

  function getArtifacts() {
    return $http.get('/api/analyze/artifacts').then(get('data'));
  }

  function getDataByQuery() {
    return $http.get('/api/analyze/dataByQuery').then(get('data'));
  }

  function getPivotData() {
    return $http.get('/api/analyze/pivotData').then(get('data'));
  }

  function generateQuery(payload) {
    return $http.post('/api/analyze/generateQuery', payload).then(get('data'));
  }

  function saveReport(payload) {
    return $http.post('/api/analyze/saveReport', payload).then(get('data'));
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

      return set('disabled', disabledValue, item);
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
      fpFlatMap(get('children')),
      fpMap(get('type')),
      some(type =>
        find(isEqual(type), supportedMethods)
      )
    )(metric.supports);
  }

  function isMethodSupported(method, supportedMethods) {
    return find(isEqual(method.type), supportedMethods);
  }

  /**
   * Intersects all the supported methods of a collection of metrics
   * @param metrics
   * @returns [String] array of supported methods (strings)
   */
  function getSupportedMethods(metrics) {
    return pipe(
      filter(metric => metric.checked === true),
      fpMap(get('supports')),
      fpMap(supports =>
        pipe(
          fpFlatMap(get('children')),
          fpMap(get('type'))
        )(supports)),
      spread(intersection)
    )(metrics);
  }
}
