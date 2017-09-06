import * as assign from 'lodash/assign';
import * as keys from 'lodash/keys';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import * as forEach from 'lodash/forEach';
import * as fpMap from 'lodash/fp/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as split from 'lodash/split';
import * as first from 'lodash/first';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as fpOmit from 'lodash/fp/omit';
import * as invert from 'lodash/invert';
import * as concat from 'lodash/concat';
import * as fpMapValues from 'lodash/fp/mapValues';

import {NUMBER_TYPES} from '../consts';

const FRONT_2_BACK_PIVOT_FIELD_PAIRS = {
  caption: 'displayName',
  dataField: 'columnName',
  summaryType: 'aggregate'
};

const BACK_2_FRONT_PIVOT_FIELD_PAIRS = invert(FRONT_2_BACK_PIVOT_FIELD_PAIRS);

export function PivotService() {
  'ngInject';

  return {
    getArea,
    takeOutKeywordFromArtifactColumns,
    artifactColumns2PivotFields,
    parseData
  };

  function artifactColumns2PivotFields() {
    return fpPipe(
      fpFilter(field => field.checked && field.area),
      fpMap(artifactColumn => {
        if (NUMBER_TYPES.includes(artifactColumn.type)) {
          artifactColumn.dataType = 'number';
          artifactColumn.format = {
            type: 'decimal',
            precision: 2
          };
        } else {
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

  function takeOutKeywordFromArtifactColumns(artifactColumns) {
    forEach(artifactColumns, artifactColumn => {
      if (artifactColumn.columnName && artifactColumn.type === 'string') {
        const split = artifactColumn.columnName.split('.');
        if (split[1] === 'keyword') {
          artifactColumn.columnName = split[0];
        }
      }
    });
    return artifactColumns;
  }

  function getArea(key) {
    const area = first(split(key, '_'));
    if (area !== 'row' && area !== 'column') {
      return 'data';
    }
    return area;
  }

   /** Map the tree level to the columnName of the field
   * Example:
   * row_field_1: 0 -> SOURCE_OS
   * row_field_2: 1 -> SOURCE_MANUFACTURER
   * column_field_1: 2 -> TARGET_OS
   */
  function getNodeFieldMap(sqlBuilder) {
    const rowFieldMap = map(sqlBuilder.rowFields, 'columnName');
    const columnFieldMap = map(sqlBuilder.columnFields, 'columnName');

    return concat(rowFieldMap, columnFieldMap);
  }

  function parseData(data, sqlBuilder) {
    const nodeFieldMap = getNodeFieldMap(sqlBuilder);

    return parseNode(data, {}, nodeFieldMap, 0);
  }

  function getColumnName(fieldMap, level) {
    // take out the .keyword form the columnName
    // if there is one
    const columnName = fieldMap[level - 1];
    if (columnName.indexOf('.keyword') > -1) {
      return columnName.split('.')[0];
    }
    return columnName;
  }

  function parseNode(node, dataObj, nodeFieldMap, level) {
    if (node.key) {
      const columnName = getColumnName(nodeFieldMap, level);
      dataObj[columnName] = node.key;
    }

    const nodeName = getChildNodeName(node);
    if (nodeName && node[nodeName]) {
      const data = flatMap(node[nodeName].buckets, bucket => parseNode(bucket, dataObj, nodeFieldMap, level + 1));
      return data;
    }
    const datum = parseLeaf(node, dataObj);

    return datum;
  }

  function parseLeaf(node, dataObj) {
    const dataFields = fpPipe(
      fpOmit(['doc_count', 'key']),
      fpMapValues('value')
    )(node);

    return assign(
      dataFields,
      dataObj
    );
  }

  function getChildNodeName(node) {
    const nodeKeys = keys(node);
    const childNodeName = find(nodeKeys, key => {
      const isRow = key.indexOf('row_level') > -1;
      const isColumn = key.indexOf('column_level') > -1;
      return isRow || isColumn;
    });

    return childNodeName;
  }
}
