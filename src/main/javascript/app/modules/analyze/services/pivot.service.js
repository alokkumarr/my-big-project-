import assign from 'lodash/assign';
import keys from 'lodash/keys';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';
import uniq from 'lodash/uniq';
import forEach from 'lodash/forEach';
import fpMap from 'lodash/fp/map';
import fpPipe from 'lodash/fp/pipe';
import fpFilter from 'lodash/fp/filter';
import split from 'lodash/split';
import first from 'lodash/first';
import fpMapKeys from 'lodash/fp/mapKeys';
import fpOmit from 'lodash/fp/omit';
import invert from 'lodash/invert';
import fpGroupBy from 'lodash/fp/groupBy';
import sortBy from 'lodash/sortBy';
import last from 'lodash/last';
import omit from 'lodash/omit';
import mapValues from 'lodash/mapValues';

const FRONT_2_BACK_PIVOT_FIELD_PAIRS = {
  caption: 'displayName',
  dataField: 'columnName',
  summaryType: 'aggregate'
};

const BACK_2_FRONT_PIVOT_FIELD_PAIRS = invert(FRONT_2_BACK_PIVOT_FIELD_PAIRS);

export function PivotService() {
  'ngInject';

  return {
    denormalizeData,
    getUniquesFromNormalizedData,
    putSettingsDataInFields,
    mapFieldsToFilters,
    getArea,
    getFrontend2BackendFieldMapper,
    getBackend2FrontendFieldMapper,
    artifactColumns2PivotFields
  };

  function artifactColumns2PivotFields() {
    return fpPipe(
      fpFilter(field => field.checked && field.area),
      fpMap(artifactColumn => {
        switch (artifactColumn.type) {
          case 'int':
          case 'double':
          case 'long':
            artifactColumn.dataType = 'number';
            break;
          default:
            artifactColumn.dataType = artifactColumn.type;
        }
        return artifactColumn;
      }),
      fpMap(fpMapKeys(key => {
        const newKey = BACK_2_FRONT_PIVOT_FIELD_PAIRS[key];
        return newKey || key;
      }))
    );
  }

  function getFrontend2BackendFieldMapper() {
    return fpMap(
      fpPipe(
        fpMapKeys(key => {
          const newKey = FRONT_2_BACK_PIVOT_FIELD_PAIRS[key];
          return newKey || key;
        }),
        fpOmit(['_initProperties', 'selector', 'dataType'])
      )
    );
  }

  function getBackend2FrontendFieldMapper() {
    return fpPipe(
      fpMap(bEField => {
        switch (bEField.type) {
          case 'int':
          case 'double':
          case 'long':
            bEField.dataType = 'number';
            break;
          default:
            bEField.dataType = bEField.type;
        }
        return bEField;
      }),
      fpMap(fpMapKeys(key => {
        const newKey = BACK_2_FRONT_PIVOT_FIELD_PAIRS[key];
        return newKey || key;
      }))
    );
  }

  function denormalizeData(normalizedData, fields) {
    const groupedFields = getGroupedFields(fields);
    return flatMap(normalizedData.row_level_1.buckets, node => denormalizeRecursive({groupedFields, keys: {}, currentKey: 'row_level_1', node}));
  }

  function getGroupedFields(fields) {
    return fpPipe(
      fpFilter(({area}) => area === 'row' || area === 'column' || area === 'data'),
      fpGroupBy('area'),
      sortGroupedFieldsByAreaIndex
    )(fields);
  }

  function sortGroupedFieldsByAreaIndex(groupedFields) {
    groupedFields.row = sortBy(groupedFields.row, 'areaIndex');
    groupedFields.column = sortBy(groupedFields.column, 'areaIndex');
    groupedFields.data = sortBy(groupedFields.data, 'areaIndex');
    return groupedFields;
  }

/* eslint-disable camelcase */
  function denormalizeRecursive({groupedFields, keys, currentKey, node}) {
    const containerProp = getContainerProp(node);
    const container = node[containerProp];
    keys[getFieldNameFromCurrentKey(groupedFields, currentKey)] = node.key;
    if (container) {
      // this is a node
      return flatMap(container.buckets, node => {
        return denormalizeRecursive({groupedFields, keys, currentKey: containerProp, node});
      });
    }
    // this is a leaf
    // these props are added by elastic search, and are of no use in the UI
    const propsToOmit = ['doc_count', 'key', 'key_as_string'];
    return assign(
      mapValues(omit(node, propsToOmit), 'value'),
      keys
    );
  }
  /* eslint-enable camelcase */

  function getFieldNameFromCurrentKey(groupedFields, key) {
    const index = parseInt(last(split(key, '_')), 10);
    return groupedFields[getArea(key)][index - 1].dataField;
  }

  function getUniquesFromNormalizedData(data, targetKey, groupedFields) {
    return uniq(flatMap(data.buckets, node => traverseRec({groupedFields, currentKey: 'row_level_1', targetKey, node})));
  }

  /* eslint-disable camelcase */
  function traverseRec({groupedFields, currentKey, targetKey, node}) {
    const containerProp = getContainerProp(node);
    const container = node[containerProp];
    const currentFieldKey = getFieldNameFromCurrentKey(groupedFields, currentKey);

    const isInTargetContainer = currentFieldKey === targetKey;

    if (!isInTargetContainer) {
      // this is a node
      return flatMap(container.buckets, node => {
        return traverseRec({groupedFields, currentKey: containerProp, targetKey, node});
      });
    }
    // it's in target container
    return node.key;
  }
  /* eslint-enable camelcase */

  /**
   * Get the container object from the json node, it can be column_level_X or row_level_X where X is an int
   * @param the object from which to get the container object
   */
  function getContainerProp(object) {
    const objKeys = keys(object);
    return find(objKeys, key => {
      return key.includes('column_level') || key.includes('row_level');
    });
  }

  function putSettingsDataInFields(settings, fields) {
    forEach(fields, field => {
      const area = this.getArea(field.dataField);
      const targetField = find(settings[area], ({dataField}) => {
        return dataField === field.dataField;
      });
      field.area = targetField.area;
      field.summaryType = targetField.summaryType;
      field.checked = targetField.checked;
    });
  }

  function mapFieldsToFilters(data, fields) {
    const groupedFields = getGroupedFields(fields);
    return fpPipe(
      fpFilter(field => field.area === 'row' && field.dataType === 'string'),
      fpMap(field => {
        return {
          name: field.dataField,
          type: field.dataType,
          items: field.dataType === 'string' ?
          getUniquesFromNormalizedData(data, field.dataField, groupedFields) :
          null,
          label: field.caption,
          model: null
        };
      })
    )(fields);
  }

  function getArea(key) {
    const area = first(split(key, '_'));
    if (area !== 'row' && area !== 'column') {
      return 'data';
    }
    return area;
  }
}
