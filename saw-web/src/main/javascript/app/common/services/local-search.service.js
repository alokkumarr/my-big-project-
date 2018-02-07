import * as filter from 'lodash/filter';
import * as some from 'lodash/some';
import * as trim from 'lodash/trim';

export class LocalSearchService {
  constructor($q) {
    'ngInject';
    this._$q = $q;
  }

  /* @searchTerm looks something like column:value
     Column is optional
     If value is enclosed within quotes like column:"value",
     the exact flag is set
     */
  parseSearchTerm(searchTerm = '') {
    const searchText = searchTerm.split(':');
    const result = searchText.length > 1 ?
      {field: trim(searchText[0]), fullTerm: trim(searchText.slice(1).join(':'))} :
      {field: null, fullTerm: trim(searchText[0])};

    result.trimmedTerm = trim(trim(result.fullTerm, '"'), '\'');
    result.exact = result.fullTerm.toUpperCase() !== result.trimmedTerm.toUpperCase();
    return result;
  }

  /* @searchCriteria is the format returned from parseSearchTerm function above.
     @data is an array of objects - the data store to search through
     @fieldConfig is an array of objects. Each object in this array can have following fields:

       keyword: used to match against column input by user
       fieldName: actual property name in any row in the @data array
       accessor: (optional) use for getting properties that are nested or require some modification
    */
  doSearch(searchCriteria, data = [], fieldConfig = []) {
    if (!searchCriteria.trimmedTerm) {
      return this._$q.resolve(data);
    }

    const term = searchCriteria.trimmedTerm.toUpperCase();

    const matchIn = item => {
      if (angular.isArray(item)) {
        return some(item, val => (val || '').toUpperCase().indexOf(term) !== -1);
      }
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };

    const matchFull = item => {
      if (angular.isArray(item)) {
        return some(item, val => (val || '').toUpperCase() === term);
      }
      return (item || '').toUpperCase() === term;
    };

    const searchConfig = searchCriteria.field ?
      filter(fieldConfig, config => config.keyword.toUpperCase() === searchCriteria.field.toUpperCase()) :
      fieldConfig;

    if (!searchConfig || searchConfig.length === 0) {
      return this._$q.reject(new Error(`"${searchCriteria.field}" column does not exist.`));
    }

    const result = filter(data, row => {
      return some(searchConfig, config => {
        const rowValue = angular.isFunction(config.accessor) ?
          config.accessor(row[config.fieldName]) :
          row[config.fieldName];
        return searchCriteria.exact ?
          matchFull(rowValue) :
          matchIn(rowValue);
      });
    });

    return this._$q.resolve(result);
  }
}
