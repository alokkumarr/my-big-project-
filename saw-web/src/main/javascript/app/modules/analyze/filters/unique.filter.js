import * as uniqBy from 'lodash/uniqBy';

// Simple filter to get unique values during ng-repeat or similar use cases.
// Takes 'key' as a parameter. (ng-repeat="item in items | uniqueFilter:'id'")

export function uniqueFilter() {
  return (arr, key) => {
    return uniqBy(arr, key);
  };
}
