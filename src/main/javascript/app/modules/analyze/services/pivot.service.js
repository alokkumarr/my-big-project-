import assign from 'lodash/assign';
import keys from 'lodash/keys';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';
import uniq from 'lodash/uniq';

export function PivotService() {
  'ngInject';

  return {
    denormalizeData,
    getUniquesFromNormalizedData
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
}
