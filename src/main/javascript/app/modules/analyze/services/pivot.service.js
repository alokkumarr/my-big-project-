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

export function PivotService(FilterService) {
  'ngInject';

  return {
    denormalizeData,
    getUniquesFromNormalizedData,
    getFieldsFromData,
    putSettingsDataInFields,
    putSelectedFilterModelsIntoFilters,
    getFieldToFilterMapper,
    getArea
  };

  function denormalizeData(normalizedData) {
    return flatMap(normalizedData, node => traverseRecursive({keys: {}, currentKey: 'row_level_1', node}));
  }

/* eslint-disable camelcase */
  function traverseRecursive({keys, currentKey, node}) {
    const containerProp = getContainerProp(node);
    const container = node[containerProp];
    keys[currentKey] = node.key;
    if (container) {
      // this is a node
      return flatMap(container.buckets, node => {
        return traverseRecursive({keys, currentKey: containerProp, node});
      });
    }
    // this is a leaf
    return assign({
      total_price: node.total_price.value
    }, keys);
  }
  /* eslint-enable camelcase */

  function getUniquesFromNormalizedData(data, targetKey) {
    return uniq(flatMap(data, node => traverseRec({currentKey: 'row_level_1', targetKey, node})));
  }

  /* eslint-disable camelcase */
  function traverseRec({currentKey, targetKey, node}) {
    const containerProp = getContainerProp(node);
    const container = node[containerProp];
    const isInTargetContainer = currentKey === targetKey;

    if (!isInTargetContainer) {
      // this is a node
      return flatMap(container.buckets, node => {
        return traverseRec({currentKey: containerProp, targetKey, node});
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

  function putSelectedFilterModelsIntoFilters(filters, selectedFilters) {
    forEach(filters, filter => {
      const targetFilter = find(selectedFilters, ({name}) => name === filter.name);
      if (targetFilter && FilterService.isFilterModelNonEmpty(targetFilter.model)) {
        filter.model = targetFilter.model;
      }
    });
  }

  function getFieldToFilterMapper(data) {
    return fpPipe(
      fpFilter(field => getArea(field.dataField) === 'row'),
      fpMap(field => {
        return {
          name: field.dataField,
          type: field.dataType,
          items: field.dataType === 'string' ?
          getUniquesFromNormalizedData(data, field.dataField) :
          null,
          label: field.caption,
          model: null
        };
      })
    );
  }

  function getArea(key) {
    const area = first(split(key, '_'));
    if (area !== 'row' && area !== 'column') {
      return 'data';
    }
    return area;
  }

  function getFieldsFromData() {
      // const obj = deNormalizedData[0];
      // const objKeys = keys(obj);
      // const fields = map(objKeys, key => {
      //   return {
      //     caption: key,
      //     dataField: key,
      //     width: 120,
      //     area: this.getArea(key),
      //     format: key.includes('price') ? 'currency' : null
      //   };
      // });
    return [{
      caption: 'Affiliate Name',
      width: 120,
      dataType: 'string',
      dataField: 'row_level_1'
    }, {
      caption: 'Product',
      width: 120,
      dataType: 'string',
      dataField: 'row_level_2'
    }, {
      caption: 'Date Month',
      dataField: 'column_level_1',
      format: datum => {
        const date = new Date(datum);
        return `${date.getFullYear()}-${date.getMonth()}`;
      },
      width: 120
    }, {
      caption: 'Date Day',
      dataField: 'column_level_2',
      dataType: 'string',
      format: datum => {
        const date = new Date(datum);
        return `${date.getDay()}`;
      },
      width: 120
    }, {
      caption: 'Total Price',
      dataField: 'total_price',
      dataType: 'double',
      summaryType: 'sum',
      format: 'currency'
    }];
  }
}
