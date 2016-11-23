import R from 'ramda';
import {mapped, listOf, traversed} from 'ramda-lens';
import intersection from 'lodash/intersection';

// lenses
const methodTypeLens = R.compose(R.lensProp('supports'), traversed, R.lensProp('children'), traversed, R.lensProp('type'));
const methodCategoryLens = R.compose(mapped, R.lensProp('children'), mapped);
const disabledLens = R.lensProp('disabled');
const metricLens = mapped;

export function newAnalysisService($http) {
  'ngInject';

  return {
    getMethods,
    getMetrics,
    getSupportedMethods,
    setAvailableMetrics: R.curry(setAvailableItemsWithLens)(metricLens, metricHasSupportedMethod),
    setAvailableAnalysisMethods: R.curry(setAvailableItemsWithLens)(methodCategoryLens, IsMethodSupported)
  };

  function getMethods() {
    return $http.get('/api/analyze/methods').then(result => result.data);
  }

  function getMetrics() {
    return $http.get('/api/analyze/metrics').then(result => result.data);
  }

  function setAvailableItemsWithLens(lens, setterFn, items, supportedMethods) {
    const setDisabled = item => {
      const nothingSelected = R.isEmpty(supportedMethods);
      const disabledValue = nothingSelected ? false : !setterFn(item, supportedMethods);
      return R.set(disabledLens, disabledValue, item);
    };

    return R.over(lens, setDisabled, items);
  }

  function metricHasSupportedMethod(metric, supportedMethods) {
    const findInSupportedTypes = type => {
      return R.find(R.equals(type), supportedMethods);
    };
    const hasSupportedMethod = R.pipe(
      listOf(methodTypeLens),
      R.any(findInSupportedTypes)
    );

    return hasSupportedMethod(metric);
  }

  function IsMethodSupported(method, supportedMethods) {
    return R.find(R.equals(method.type), supportedMethods);
  }

  function getSupportedMethods(metrics) {
    const getSupportedMethods = R.pipe(
      R.filter(metric => metric.checked === true),
      R.map(metric => listOf(methodTypeLens, metric)),
      R.apply(intersection)
    );

    return getSupportedMethods(metrics);
  }
}
