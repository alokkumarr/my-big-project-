import map from 'lodash/map';
import find from 'lodash/find';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import fpMap from 'lodash/fp/map';

export function SortService() {

  return {
    mapBackend2FrontendSort,
    mapFrontend2BackendSort,
    getArtifactColumns2SortFieldMapper
  };

  function mapBackend2FrontendSort(sorts, sortFields) {
    return map(sorts, sort => {
      const targetField = find(sortFields, ({dataField}) => dataField === sort.columnName);
      return {
        field: targetField,
        order: sort.order
      };
    });
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
