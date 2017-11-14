import * as assign from 'lodash/assign';
import * as keys from 'lodash/keys';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import * as fpMap from 'lodash/fp/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as split from 'lodash/split';
import * as first from 'lodash/first';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as fpOmit from 'lodash/fp/omit';
import * as invert from 'lodash/invert';
import * as concat from 'lodash/concat';
import * as clone from 'lodash/clone';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as fpPick from 'lodash/fp/pick';
import * as moment from 'moment';

import {NUMBER_TYPES, DATE_TYPES} from '../consts';
import {DATE_INTERVALS_OBJ} from '../components/pivot/settings/analyze-pivot-settings.component';

const FRONT_2_BACK_PIVOT_FIELD_PAIRS = {
  caption: 'displayName',
  dataField: 'columnName',
  aggregate: 'aggregate'
};

const BACK_2_FRONT_PIVOT_FIELD_PAIRS = invert(FRONT_2_BACK_PIVOT_FIELD_PAIRS);

export function PivotService() {
  'ngInject';

  return {
    getArea,
    artifactColumns2PivotFields,
    parseData,
    trimSuffixFromPivotFields,
    formatDates
  };

  function artifactColumns2PivotFields() {
    return fpPipe(
      fpFilter(field => field.checked && field.area),
      fpMap(artifactColumn => {
        if (NUMBER_TYPES.includes(artifactColumn.type)) {
          artifactColumn.dataType = 'number';
          artifactColumn.format = {
            type: 'fixedPoint',
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

  function trimSuffixFromPivotFields(fields) {
    return map(fields, field => {
      const clonedField = clone(field);
      if (field.dataField && field.type === 'string') {
        const split = field.dataField.split('.');
        clonedField.dataField = split[0];
      }
      return clonedField;
    });
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
    const split = columnName.split('.');
    if (split[1]) {
      return split[0];
    }
    return columnName;
  }

  function formatDates(data, fields) {
    const formattedFields = map(fields, field => {
      if (DATE_TYPES.includes(field.type) &&
          ['day', 'quarter', 'month'].includes(field.dateInterval)) {
        const clonedField = clone(field);
        clonedField.dataType = 'string';
        return clonedField;
      }
      return field;
    });

    const dateFields = fpPipe(
      fpFilter(({type}) => DATE_TYPES.includes(type)),
      fpMap(fpPick(['dataField', 'dateInterval']))
    )(fields);
    if (isEmpty(dateFields)) {
      return {formattedData: data, formattedFields: fields};
    }

    const formattedData = map(data, dataPoint => {
      const clonedDataPoint = clone(dataPoint);
      forEach(dateFields, ({dataField, dateInterval}) => {
        const format = DATE_INTERVALS_OBJ[dateInterval].format;
        clonedDataPoint[dataField] = moment(dataPoint[dataField]).utcOffset(0).format(format);
        if (dateInterval === 'quarter') {
          const parts = split(clonedDataPoint[dataField], '-');
          clonedDataPoint[dataField] = `${parts[0]}-Q${parts[1]}`;
        }
      });
      return clonedDataPoint;
    });

    return {formattedData, formattedFields};
  }

  function parseNode(node, dataObj, nodeFieldMap, level) {
    if (node.key) {
      const columnName = getColumnName(nodeFieldMap, level);
      dataObj[columnName] = node.key_as_string || node.key;
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
      fpOmit(['doc_count', 'key', 'key_as_string']),
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
