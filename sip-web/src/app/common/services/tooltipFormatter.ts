import * as Highcharts from 'highcharts/highcharts';
import * as isUndefined from 'lodash/isUndefined';
import * as round from 'lodash/round';
import moment from 'moment';
import * as get from 'lodash/get';

import {
  FLOAT_TYPES,
  NUMBER_TYPES,
  DATE_TYPES,
  AGGREGATE_TYPES_OBJ
} from '../consts';

export const displayNameWithoutAggregateFor = (column): string => {
  if (!column.dataField) {
    return column.displayName;
  }

  const match = column.displayName.match(/\((.+)\)/);
  return match ? match[1] : column.displayName;
};
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
      return ['tsspline', 'tsPane'].includes(chartType)
        ? moment(point.category).format('dddd, MMM Do YYYY, h:mm a')
        : point.category;
    }
    return ['tsspline', 'tsPane'].includes(chartType)
      ? moment(point.category).format('dddd, MMM Do YYYY, h:mm a')
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
  /* Don't try to apply aggregate to a derived metric */
  if (field.expression) {
    return field.alias || `${field.displayName}`;
  } else {
    const aggregate = AGGREGATE_TYPES_OBJ[field.aggregate].designerLabel;
    return (
      field.alias || `${aggregate}(${displayNameWithoutAggregateFor(field)})`
    );
  }
}

function getYValueBasedOnAggregate(point) {
  const aggregate = get(point.series, 'userOptions.aggregate') || '';
  switch (aggregate) {
    case 'percentage':
      return Math.round((point.y || point.value) * 100) / 100 + '%';
    case 'percentagebyrow':
      return round(point.percentage, 2) + '%';
    default:
      return isUndefined(point.value) ? round(point.y, 2) : round(point.value, 2);
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
      <td>${getYValueBasedOnAggregate(point)}</td>
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
