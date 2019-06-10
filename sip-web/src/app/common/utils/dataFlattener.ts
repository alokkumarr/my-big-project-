import * as map from 'lodash/map';
import * as flatMap from 'lodash/flatMap';
import * as isEmpty from 'lodash/isEmpty';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpOmit from 'lodash/fp/omit';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as orderBy from 'lodash/orderBy';
import * as keys from 'lodash/keys';
import * as find from 'lodash/find';
import * as concat from 'lodash/concat';
import * as isUndefined from 'lodash/isUndefined';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpReduce from 'lodash/fp/reduce';
import * as mapKeys from 'lodash/mapKeys';
import * as fpMap from 'lodash/fp/map';
import * as fpSplit from 'lodash/fp/split';
import { ArtifactColumnDSL } from 'src/app/models';

// function substituteEmptyValues(data, fields) {
//   return flatMap(fields, field =>
//     fpPipe(
//       fpMap(value => {
//         // As per AC on 5216, if key is empty show undefined
//         if (field.area === 'data') {
//           return value;
//         }
//         if (isEmpty(value[field.name])) {
//           value[field.name] = 'undefined';
//         }
//         return value;
//       })
//     )(data)
//   );
// }

export function substituteEmptyValues(data) {
  return fpPipe(
    fpMap(
      fpMapValues(value => {
        return value === '' ? 'Undefined' : value;
      })
    )
  )(data);
}

export function flattenPivotData(data, sipQuery) {
  if (sipQuery.artifacts) {
    // const columnRowFields = sipQuery.artifacts[0].fields.filter(field =>
    //   ['row', 'column', 'data'].includes(field.area)
    // );
    // As per AC on 5216, if key is empty show undefined
    data = substituteEmptyValues(data);
    return data;
  }
  const nodeFieldMap = getNodeFieldMapPivot(sipQuery);
  return parseNodePivot(data, {}, nodeFieldMap, 0);
}

/** Map the tree level to the columnName of the field
 * Example:
 * row_field_1: 0 -> SOURCE_OS
 * row_field_2: 1 -> SOURCE_MANUFACTURER
 * column_field_1: 2 -> TARGET_OS
 */
function getNodeFieldMapPivot(sqlBuilder) {
  const rowFieldMap = map(sqlBuilder.rowFields, 'columnName');
  const columnFieldMap = map(sqlBuilder.columnFields, 'columnName');
  return concat(rowFieldMap, columnFieldMap);
}

function parseNodePivot(node, dataObj, nodeFieldMap, level) {
  if (!isUndefined(node.key)) {
    // As per AC on 5216, if key is empty show undefined
    node.key = isEmpty(node.key) ? 'undefined' : node.key;
    const columnName = getColumnName(nodeFieldMap, level);
    dataObj[columnName] = node.key_as_string || node.key;
  }

  const nodeName = getChildNodeName(node);
  if (nodeName && node[nodeName]) {
    const data = flatMap(node[nodeName].buckets, bucket =>
      parseNodePivot(bucket, dataObj, nodeFieldMap, level + 1)
    );
    return data;
  }
  const datum = parseLeafPivot(node, dataObj);

  return datum;
}

function parseLeafPivot(node, dataObj) {
  const dataFields = fpPipe(
    fpOmit(['doc_count', 'key', 'key_as_string']),
    fpMapValues('value')
  )(node);

  return {
    ...dataFields,
    ...dataObj
  };
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

function getChildNodeName(node) {
  const nodeKeys = keys(node);
  const childNodeName = find(nodeKeys, key => {
    const isRow = key.indexOf('row_level') > -1;
    const isColumn = key.indexOf('column_level') > -1;
    return isRow || isColumn;
  });

  return childNodeName;
}

export function getStringFieldsFromDSLArtifact(
  fields: ArtifactColumnDSL[]
): string[] {
  return fields
    .filter(field => field.type === 'string')
    .map(field => field.columnName.replace('.keyword', ''));
}

/** parse the tree structure data and return a flattened array:
 * [{
 *   x: ..,
 *   y: ..,
 *   g: ..,
 *   z: ..
 * }, ..]
 */
export function flattenChartData(data, sqlBuilder) {
  const sorts = sqlBuilder.sorts;
  if (sqlBuilder.artifacts) {
    const stringFields = getStringFieldsFromDSLArtifact(
      sqlBuilder.artifacts[0].fields
    );
    if (stringFields.length === 0) {
      return sortChartData(data, sorts);
    } else {
      /* If any string data is blank, replace it with 'Undefined'. This avoids
      highcharts giving default 'Series 1' label to blank data
      */
      const result = data.map(row => {
        const res = { ...row };
        stringFields.forEach(field => {
          res[field] = res[field] || 'Undefined';
        });
        return sortChartData(res, sorts);
      });
      return sortChartData(result, sorts);
    }
  }
}

export function checkNullinReportData(data) {
  return fpPipe(
    fpMap(
      fpMapValues(value => {
        return value === null ? 'null' : value;
      })
    )
  )(data);
}

export function flattenReportData(data, analysis) {
  if (analysis.edit) {
    return data;
  }
  const columnMap = fpPipe(
    fpFlatMap(artifact => artifact.columns),
    fpReduce((accumulator, column) => {
      const { columnName, aggregate } = column;
      const key = `${columnName})-${aggregate}`;
      accumulator[key] = column;
      return accumulator;
    }, {})
  )(analysis.artifacts);
  data = checkNullinReportData(data);
  return data.map(row => {
    return mapKeys(row, (value, key) => {
      const hasAggregateFunction = key.includes('(') && key.includes(')');

      if (!hasAggregateFunction) {
        return key;
      }
      const [aggregate, columnName] = fpPipe(fpSplit('('))(key);

      const columnMapKey = `${columnName}-${aggregate}`;
      const isInArtifactColumn = Boolean(columnMap[columnMapKey]);

      if (isInArtifactColumn) {
        return columnName.split(')')[0];
      }
      return key;
    });
  });
}

function sortChartData(data, sorts) {
  return orderBy(
    data,
    map(sorts, 'columnName'),
    map(sorts, 'order')
  );
}
