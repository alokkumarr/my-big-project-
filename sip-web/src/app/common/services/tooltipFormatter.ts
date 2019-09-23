import * as Highcharts from 'highcharts/highcharts';
import * as isUndefined from 'lodash/isUndefined';
import * as round from 'lodash/round';
import * as moment from 'moment';

import {
  FLOAT_TYPES,
  NUMBER_TYPES,
  DATE_TYPES,
  AGGREGATE_TYPES_OBJ
} from '../consts';

/**
 * If the data type is float OR aggregate is percentage or average, we
 * show two decimal places. Else, no decimal places.
 *
 * @returns {undefined}
 */
const getPrecision = (aggregate, type) => {
  return ['percentage', 'avg', 'percentagebyrow'].includes(aggregate) ||
    FLOAT_TYPES.includes(type)
    ? 2
    : 0;
};

// const handleNaNIssue = (point, options) => {
//   /**
//    * In some cases point.value or point.y is received as 0. Which was causing the round()
//    * to return NaN . So making sure that if value received is 0 then
//    * it should return as it is. Also checking if option datatype
//    * is float or double return the value with correct decimal precision.
//    *
//    */
//   return point.value === 0 || point.y === 0
//     ? point.value
//       ? point.value.toFixed(getPrecision(options.aggregate, options.dataType))
//       : point.y.toFixed(getPrecision(options.aggregate, options.dataType))
//     : round(
//         options.aggregate === 'percentagebyrow'
//           ? round(point.percentage, 2)
//           : point.y
//           ? point.y
//           : point.value,
//         getPrecision(options.aggregate, options.dataType)
//       ).toLocaleString();
// };

export function getTooltipFormats(fields, chartType) {
  return {
    headerFormat: `<table>`,
    pointFormatter: getTooltipFormatter(fields, chartType),
    footerFormat: '</table>'
  };
}

function getXValue(point, fields, chartType) {
  const { x, g } = fields;
  const hasGroupBy = Boolean(g);

  if (chartType === 'chart_scale') {
    return point.name;
  }

  if (chartType === 'pie') {
    return point.name;
  }
  if (chartType === 'packedbubble') {
    return point.name;
  }
  if (NUMBER_TYPES.includes(x.type)) {
    const precision = getPrecision(x.aggregate, x.type);
    return round(point.x, precision);
  }
  if (x.type === 'string') {
    return point.category;
  }
  if (DATE_TYPES.includes(x.type)) {
    if (hasGroupBy) {
      return chartType === 'tsspline' ? new Date(point.category) : point.category;
    }
    return chartType === 'tsspline'
    ? new Date(point.category)
    : point.key || point.category;
  }
}

function getXLabel(point, fields, chartType) {
  if (chartType === 'pie' && fields.g) {
    return point.series.name;
  }
  return fields.x.alias || fields.x.displayName;
}

function getFieldLabelWithAggregateFun(field) {
  const aggregate = AGGREGATE_TYPES_OBJ[field.aggregate].designerLabel;
  return field.alias || `${aggregate}(${field.displayName})`;
}

function getYValueBasedOnAggregate(field, point) {
  switch (field.aggregate) {
    case 'percentage':
      return Math.round(point.y * 100) / 100 + '%';
    case 'percentagebyrow':
      return round(point.percentage, 2) + '%';

    default:
      return isUndefined(point.value) ? point.y : point.value;
  }
}

/**
 * show axes labels and values
 * for x axis
 * for y axes
 * for z axis
 *
 *
 * *notes
 * don't use series name when using groupBy
 *
 * should work wiht different chart types
 * values might be in x, y, z, or name, value or pie chart
 */
export function getTooltipFormatter(fields, chartType) {
  return function() {
    const point: Highcharts.Point = this;
    const seriesName = point.series.name;
    const xLabel = getXLabel(point, fields, chartType);
    const xValue = getXValue(point, fields, chartType);
    const xString = `<tr>
      <td><strong>${xLabel}:</strong></td>
      <td>${xValue}</td>
    </tr>`;
    const yLabel = fields.g
      ? getFieldLabelWithAggregateFun(fields.y[0])
      : seriesName;
    const yString = `<tr>
      <td><strong>${yLabel}:</strong></td>
      <td>${getYValueBasedOnAggregate(fields.y[0], point)}</td>
    </tr>`;

    const zLabel = fields.z ? getFieldLabelWithAggregateFun(fields.z) : '';
    const zString = `<tr>
      <td><strong>${zLabel}:</strong></td>
      <td>${point.z}</td>
    </tr>`;
    return `${xString}
      ${yString}
      ${fields.z ? zString : ''}`;
  };
}
