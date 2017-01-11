import intersection from 'lodash/intersection';
import spread from 'lodash/spread';
import isEmpty from 'lodash/isEmpty';
import curry from 'lodash/curry';
import pipe from 'lodash/fp/pipe';
import filter from 'lodash/fp/filter';
import map from 'lodash/fp/map';
import flatMap from 'lodash/fp/flatMap';
import find from 'lodash/fp/find';
import isEqual from 'lodash/fp/isEqual';
import some from 'lodash/fp/some';
import set from 'lodash/fp/set';
import get from 'lodash/fp/get';
import forEach from 'lodash/fp/forEach';

export function AnalyzeService($http) {
  'ngInject';

  return {
    getMenu,
    getMethods,
    getMetrics,
    getArtifacts,
    getAnalyses,
    getDataByQuery,
    getSupportedMethods,
    setAvailableMetrics: curry(setAvailableItems)(metricMapper, metricHasSupportedMethod),
    setAvailableAnalysisMethods: curry(setAvailableItems)(analysisMethodMapper, isMethodSupported)
  };

  function getAnalyses() {
    return $http.get('api/analyze/analyses').then(get('data'));
  }

  function getMenu() {
    return $http.get('/api/menu/analyze').then(get('data'));
  }

  function getMethods() {
    return $http.get('/api/analyze/methods').then(get('data'));
  }

  function getMetrics() {
    return $http.get('/api/analyze/metrics').then(get('data'));
  }

  function getArtifacts() {
    return $http.get('/api/analyze/reportArtifacts').then(get('data'));
  }

  function getArtifacts() {
    return $http.get('/api/analyze/artifacts').then(get('data'));
  }

  function getDataByQuery() {
    return $http.get('/api/analyze/dataByQuery').then(get('data'));
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
    return map(metric => actionFn(metric));
  }

  /**
   * Mapper function for the analysis methods collection
   * @param actionFn
   * @returns {*}
   */
  function analysisMethodMapper(actionFn) {
    return pipe(
      map(method => {
        method.children = map(child => actionFn(child))(method.children);
        return method;
      })
    );
  }

  function metricHasSupportedMethod(metric, supportedMethods) {
    return pipe(
      flatMap(get('children')),
      map(get('type')),
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
      map(get('supports')),
      map(supports =>
        pipe(
          flatMap(get('children')),
          map(get('type'))
        )(supports)),
      spread(intersection)
    )(metrics);
  }
}
