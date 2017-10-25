import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';

export function SortService() {

  return {
    mapBackend2FrontendSort,
    mapFrontend2BackendSort,
    filterInvalidSorts,
    getArtifactColumns2SortFieldMapper
  };

  function mapBackend2FrontendSort(sorts, sortFields) {
    return fpFilter(val => val.field, map(sorts, sort => {
      const targetField = find(sortFields, ({dataField}) => dataField === sort.columnName);
      return {
        field: targetField,
        order: sort.order
      };
    }));
  }

  function filterInvalidSorts(sorts, sortFields) {
    return fpFilter(sort => {
      return find(sortFields, ({dataField}) => dataField === get(sort, 'field.dataField'));
    }, sorts);
  }

  function mapFrontend2BackendSort(sorts) {
    return map(sorts, sort => {
      return {
        columnName: sort.field.dataField,
        type: sort.field.type,
        order: sort.order
      };
    });
  }

  function getArtifactColumns2SortFieldMapper() {
    return fpPipe(
      fpFilter(({checked}) => checked &&
        (checked === 'x' || checked === 'g')),
      fpMap(artifactColumn => {
        return {
          type: artifactColumn.type,
          dataField: artifactColumn.columnName,
          label: artifactColumn.alias || artifactColumn.displayName
        };
      })
    );
  }
}