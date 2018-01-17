import * as get from 'lodash/get';
import * as set from 'lodash/set';
import * as clone from 'lodash/clone';
import * as sum from 'lodash/sum';
import * as map from 'lodash/map';
import * as round from 'lodash/round';
import * as flatMap from 'lodash/flatMap';
import * as assign from 'lodash/assign';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as indexOf from 'lodash/indexOf';
import * as isEmpty from 'lodash/isEmpty';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpOmit from 'lodash/fp/omit';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpMap from 'lodash/fp/map';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as fpInvert from 'lodash/fp/invert';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as reduce from 'lodash/reduce';
import * as concat from 'lodash/concat';
import * as compact from 'lodash/compact';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpUniq from 'lodash/fp/uniq';
import * as findIndex from 'lodash/findIndex';
import * as mapValues from 'lodash/mapValues';
import * as sortBy from 'lodash/sortBy';
import * as moment from 'moment';
import * as values from 'lodash/values';
import * as toString from 'lodash/toString';
import * as replace from 'lodash/replace';
import * as isUndefined from 'lodash/isUndefined';

import {NUMBER_TYPES, DATE_TYPES, AGGREGATE_TYPES_OBJ, CHART_COLORS} from '../consts';

const LEGEND_POSITIONING = {
  left: {
    name: 'left',
    displayName: 'Left',
    align: 'left',
    verticalAlign: 'middle'
  },
  right: {
    name: 'right',
    displayName: 'Right',
    align: 'right',
    verticalAlign: 'middle'
  },
  top: {
    name: 'top',
    displayName: 'Top',
    align: 'center',
    verticalAlign: 'top'
  },
  bottom: {
    name: 'bottom',
    displayName: 'Bottom',
    align: 'center',
    verticalAlign: 'bottom'
  }
};

const LAYOUT_POSITIONS = {
  horizontal: {
    name: 'horizontal',
    displayName: 'Horizontal',
    layout: 'horizontal'
  },
  vertical: {
    name: 'vertical',
    displayName: 'Vertical',
    layout: 'vertical'
  }
};

/* Customize default config for stack column chart */
const stackConfig = config => {
  set(config, 'chart.type', 'column');
  set(config, 'plotOptions.column.stacking', 'normal');
  return config;
};

const donutConfig = config => {

  set(config, 'chart.type', 'pie');
  return pieConfig(config);
};

const pieConfig = config => {
  delete config.xAxis;
  delete config.yAxis;
  set(config, 'series', [{
    name: 'Brands',
    colorByPoint: true,
    data: []
  }]);
  set(config, 'plotOptions.pie.showInLegend', false);
  return config;
};

const configCustomizer = {
  stack: stackConfig,
  pie: pieConfig,
  donut: donutConfig
};

const addCommas = nStr => {
  nStr = String(nStr);
  const x = nStr.split('.');
  let x1 = x[0];
  const x2 = x.length > 1 ? '.' + x[1] : '';
  const rgx = /(\d+)(\d{3})/;
  while (rgx.test(x1)) {
    x1 = x1.replace(rgx, '$1,$2');
  }
  return x1 + x2;
};

export class ChartService {
  constructor(Highcharts) {
    'ngInject';

    this._Highcharts = Highcharts;
    this.LEGEND_POSITIONING = LEGEND_POSITIONING;
    this.LAYOUT_POSITIONS = LAYOUT_POSITIONS;
  }

  updateAnalysisModel(analysis) {
    switch (analysis.chartType) {
    case 'pie':
      analysis.labelOptions = analysis.labelOptions || {enabled: true, value: 'percentage'};
      break;
    default:
      break;
    }
    return analysis;
  }

  initLegend(analysis) {
    const initialLegendPosition = analysis.chartType === 'combo' ? 'top' : analysis.chartType.substring(0, 2) === 'ts' ? 'bottom' : 'right';
    const initialLegendLayout = (analysis.chartType === 'combo' || analysis.chartType.substring(0, 2) === 'ts') ? 'horizontal' : 'vertical';

    return {
      align: get(analysis, 'legend.align', initialLegendPosition),
      layout: get(analysis, 'legend.layout', initialLegendLayout),
      options: {
        align: values(this.LEGEND_POSITIONING),
        layout: values(this.LAYOUT_POSITIONS)
      }
    };
  }

  /* Returns default chart config for various chart types */
  getChartConfigFor(type, options) {
    const legendPosition = LEGEND_POSITIONING[get(options, 'legend.align')];
    const legendLayout = LAYOUT_POSITIONS[get(options, 'legend.layout')];

    const SPACING = 45;
    const HEIGHT = (angular.isUndefined(options.chart) ? 400 : options.chart.height);

    const config = {
      chart: {
        type: ['combo', 'bar'].includes(type) ? null : this.splinifyChartType(type),
        spacingLeft: SPACING,
        spacingRight: SPACING,
        spacingBottom: SPACING,
        spacingTop: SPACING,
        height: HEIGHT,
        reflow: true
      },
      legend: {
        align: legendPosition.align,
        verticalAlign: legendPosition.verticalAlign,
        layout: legendLayout.layout,
        enabled: false
      },
      series: [{
        name: 'Series 1',
        data: []
      }],
      yAxis: {
        title: {x: -15}
      },
      xAxis: {
        categories: [],
        title: {y: 15}
      }
    };

    if (configCustomizer[type]) {
      return configCustomizer[type](config);
    }
    return config;
  }

  /* Provides view related configuration for various chart types */
  getViewOptionsFor(type) {
    const config = {
      yAxisLabels: [],
      axisLabels: {
        x: 'Dimensions', y: 'Metrics', z: 'Z-Axis', g: 'Group By'
      },
      renamable: {
        x: true, y: true, z: false, g: false
      },
      required: {
        x: true, y: true, z: false, g: false
      },
      labelOptions: [],
      customTooltip: true,
      legend: true
    };

    switch (type) {
    case 'bubble':
      config.axisLabels.z = 'Size By';
      config.axisLabels.g = 'Color By';
      config.required.z = true;
      return config;

    case 'pie':
      config.axisLabels.y = 'Angle';
      config.axisLabels.x = 'Color By';
      config.renamable.x = false;
      config.labelOptions = [{
        value: 'percentage',
        name: 'Show Percentage'
      }, {
        value: 'data',
        name: 'Show Data Value'
      }];
      config.customTooltip = false;
      config.legend = false;
      return config;

    case 'column':
    case 'bar':
    case 'line':
    case 'area':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'tsspline':
    case 'tsPane':
    default:
      return config;
    }
  }

  /** the mapping between the tree level, and the columName of the field
   * Example:
   * string_field_1: 0 -> SOURCE_OS (marker on the checked attribute)
   * string_field_2: 1 -> SOURCE_MANUFACTURER
   */
  getNodeFieldMap(nodeFields) {
    return map(nodeFields, 'columnName');
  }

  /** parse the tree structure data and return a flattened array:
   * [{
   *   x: ..,
   *   y: ..,
   *   g: ..,
   *   z: ..
   * }, ..]
   */
  parseData(data, sqlBuilder) {
    const nodeFieldMap = this.getNodeFieldMap(sqlBuilder.nodeFields);
    return this.parseNode(data, {}, nodeFieldMap, 1);
  }

  parseNode(node, dataObj, nodeFieldMap, level) {
    if (node.key) {
      dataObj[nodeFieldMap[level - 2]] = node.key;
    }

    const childNode = node[`node_field_${level}`];
    if (childNode) {
      const data = flatMap(childNode.buckets, bucket => this.parseNode(bucket, dataObj, nodeFieldMap, level + 1));
      return data;
    }
    const datum = this.parseLeaf(node, dataObj);
    return datum;
  }

  parseLeaf(node, dataObj) {
    const dataFields = fpPipe(
      fpOmit(['doc_count', 'key', 'key_as_string']),
      fpMapValues('value')
    )(node);

    return assign(
      dataFields,
      dataObj
    );
  }

  splitToSeriesAndCategories(parsedData, fields, {sorts}, type) {
    let series = [];
    const categories = {};
    const areMultipleYAxes = fields.y.length > 1;
    const isGrouped = fields.g;
    const isHighStock = type.substring(0, 2) === 'ts';

    const fieldsArray = compact([fields.x, ...fields.y, fields.z, fields.g]);
    if (!isHighStock) {         // check if Highstock timeseries(ts) or Highchart
      const dateFields = filter(fieldsArray, ({type}) => DATE_TYPES.includes(type));
      this.formatDatesIfNeeded(parsedData, dateFields);
    }
    if (areMultipleYAxes) {
      series = this.splitSeriesByYAxes(parsedData, fields, type);
    } else if (isGrouped) {
      series = this.splitSeriesByGroup(parsedData, fields);
    } else {
      const axesFieldNameMap = this.getAxesFieldNameMap(fields);
      const yField = fields.y[0];
      series = [this.getSerie(yField, 0, fields.y, type)];
      series[0].data = map(parsedData,
        dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])
      );
    }
    // split out categories frem the data
    forEach(series, serie => {
      serie.data = map(serie.data, point => {
        const dataPoint = clone(point);
        forEach(dataPoint, (v, k) => {
          if (this.isCategoryAxis(fields, k)) {
            this.addToCategory(categories, k, v);
          }
        });
        return dataPoint;
      });
    });

    // sort the categories if a sort is specified for category field
    if (sorts && sorts.length) {
      forEach(categories, (v, k) => {
        const field = filter(fieldsArray, field => field.checked === k)[0] || {};
        const sortField = filter(sorts, sortF => sortF.field.dataField === field.columnName)[0] || {};
        categories[k] = fpOrderBy(
          value => this.getSortValue(field, value),
          sortField.order === 'asc' ? 'asc' : 'desc',
          categories[k]
        );
      });
    }

    /* assign data points to category indexes. If this is done
    at the same time as creation of categories, it becomes impossible
    to get the index from sorted categories array since it'll be
    incomplete */
    forEach(series, serie => {
      serie.data = map(serie.data, point => {
        const dataPoint = clone(point);
        forEach(dataPoint, (v, k) => {
          if (this.isCategoryAxis(fields, k)) {
            if (!isHighStock) {
              dataPoint[k] = indexOf(categories[k], v);
            } else {
              dataPoint[k] = v;
            }
          }
        });
        return dataPoint;
      });
    });

    if (!isEmpty(categories)) {
      forEach(series, serie => {
        serie.data = sortBy(serie.data, 'x');
      });
    }
    return {
      series,
      categories
    };
  }

  splinifyChartType(type) {
    switch (type) {
    case 'line':
      return 'spline';
    case 'area':
      return 'areaspline';
    case 'tsspline':
      return 'spline';
    case 'tsPane':
      return 'spline';
    default:
      return type;

    }
  }

  getSortValue(field, value) {
    if (!field) {
      return value;
    }

    if (DATE_TYPES.includes(field.type)) {
      const momentDateFormat = this.getMomentDateFormat(field.dateFormat);
      return moment(value, momentDateFormat);
    }

    return value;
  }

  formatDatesIfNeeded(parsedData, dateFields) {
    if (!isEmpty(dateFields)) {
      forEach(parsedData, dataPoint => {
        forEach(dateFields, ({columnName, dateFormat}) => {
          const momentDateFormat = this.getMomentDateFormat(dateFormat);
          dataPoint[columnName] = moment.utc(dataPoint[columnName]).format(momentDateFormat);
        });
      });
    }
  }

  getSerie({alias, displayName, comboType, aggregate, chartType}, index, fields, type) {
    const comboGroups = fpPipe(
      fpMap('comboType'),
      fpUniq,
      fpInvert,
      fpMapValues(parseInt)
    )(fields);

    const splinifiedChartType = this.splinifyChartType(comboType);
    const zIndex = this.getZIndex(comboType);
    return {
      name: alias || `${AGGREGATE_TYPES_OBJ[aggregate].label} ${displayName}`,
      type: splinifiedChartType,
      yAxis: (chartType === 'tsPane' || type === 'tsPane') ? index : comboGroups[comboType],
      zIndex,
      data: []
    };
  }

  getMomentDateFormat(dateFormat) {
    // the backend and moment.js require different date formats for days of month
    // the backend represents it with "d", and momentjs with "Do"
    return replace(dateFormat, 'd', 'Do');
  }

  getZIndex(type) {
    switch (type) {
    case 'area':
      return 0;
    case 'column':
    case 'bar':
      return 1;
    case 'pline':
      return 3;
    default:
      return 4;
    }
  }

  splitSeriesByYAxes(parsedData, fields, type) {
    const axesFieldNameMap = this.getAxesFieldNameMap(fields, 'y');
    forEach(fields.y, field => {
      field.chartType = type;
    });
    const series = map(fields.y, this.getSerie.bind(this));

    forEach(parsedData, dataPoint => {
      forEach(fields.y, (field, index) => {
        series[index].data.push(assign(
          {y: dataPoint[field.columnName]},
          mapValues(axesFieldNameMap, val => dataPoint[val])
        ));
      });
    });

    return series;
  }

  splitSeriesByGroup(parsedData, fields) {
    const axesFieldNameMap = this.getAxesFieldNameMap(fields);
    let comboType = fields.y[0].comboType;
    if (angular.isDefined(comboType)) {
      if (comboType === 'tsspline' || comboType === 'tsPane') {
        comboType = comboType === 'tsPane' ? 'spline' : comboType.slice(2);
      }
    }

    return fpPipe(
      fpMap(dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])),
      fpGroupBy('g'),
      fpToPairs,
      fpMap(([name, data]) => ({name, data, type: comboType}))
    )(parsedData);
  }

  /**
   * Get the map from colmnNames to the axes, or group
   * Ex
   * y -> AVAILABLE_MB
   */
  getAxesFieldNameMap(fields, exclude) {
    // Y axis ommitted because it is added in splitSeriesByYAxes
    const y = exclude === 'y' ? [] : fields.y;
    const fieldsArray = compact([fields.x, ...y, fields.z, fields.g]);
    return reduce(fieldsArray, (accumulator, field) => {
      accumulator[field.checked] = field.columnName;
      return accumulator;
    }, {});
  }

  addToCategory(categories, key, newCategoryValue) {
    if (!categories[key]) {
      categories[key] = [];
    }
    if (indexOf(categories[key], newCategoryValue) < 0) {
      categories[key].push(newCategoryValue);
    }
  }

  isCategoryAxis(fields, key) {
    const dataType = get(fields, `${key}.type`);
    const isAxis = key !== 'g';
    // Strings should be represented as categories in the chart
    /* eslint-disable angular/typecheck-string */
    const isCategoryAxis = isAxis &&
      (dataType === 'string' || dataType === 'String' || DATE_TYPES.includes(dataType));
    /* eslint-enable angular/typecheck-string */
    return isCategoryAxis;
  }

  dataToNestedDonut(series, categories) {
    /* Group by option forms the inner circle. X axis option forms the outer region
       This logic is adapted from https://www.highcharts.com/demo/pie-donut */

    const colors = this._Highcharts.getOptions().colors;
    const gCategories = map(series, s => s.name);
    const data = map(series, (s, i) => {
      const drilldown = {
        color: colors[i],
        categories: map(s.data, d => get(categories, `x.${d.x}`)),
        data: map(s.data, d => d.y)
      };
      return {
        drilldown,
        y: sum(drilldown.data)
      };
    });
    const innerData = [];
    const outerData = [];
    const dataLen = data.length;

    for (let i = 0; i < dataLen; i += 1) {

      innerData.push({
        name: gCategories[i],
        y: data[i].y,
        color: data[i].color
      });

      const drillDataLen = data[i].drilldown.data.length;
      for (let j = 0; j < drillDataLen; j += 1) {
        const brightness = 0.2 - (j / drillDataLen);
        outerData.push({
          name: data[i].drilldown.categories[j],
          y: data[i].drilldown.data[j],
          /* eslint-disable */
          color: this._Highcharts.Color(data[i].color).brighten(brightness).get()
          /* eslint-enable */
        });
      }
    }

    const chartSeries = [{
      data: innerData,
      dataLabels: {
        color: '#ffffff',
        distance: -30
      },
      size: '60%'
    }, {
      data: outerData,
      size: '100%',
      innerSize: '60%',
      id: 'outerData'
    }];

    return chartSeries;
  }

  customizeSeriesForChartType(series, chartType, categories, fields) {
    let mapperFn;
    let chartSeries;

    switch (chartType) {
    case 'column':
    case 'bar':
    case 'line':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'bubble':
    case 'area':
    case 'combo':
      // the bubble chart already supports the parsed data
      return {chartSeries: series};

    case 'pie':
      if (!fields.g) {
        mapperFn = ({x, y}) => {
          const category = get(categories, `x.${x}`, toString(x || 0));
          return {name: category, y, drilldown: category};
        };
        forEach(series, serie => {
          serie.data = map(serie.data, mapperFn);
        });

        chartSeries = series;

      } else {
        chartSeries = this.dataToNestedDonut(series, categories);
      }

      return {chartSeries};

    default:
      throw new Error(`Chart type: ${chartType} is not supported!`);
    }
  }

  getPieChangeConfig(type, settings, fields, gridData, opts) {
    const changes = [];
    const yField = get(fields, 'y.0', {});
    const yLabel = get(opts, 'labels.y') || `${AGGREGATE_TYPES_OBJ[yField.aggregate].label} ${yField.displayName}`;

    const labelOptions = get(opts, 'labelOptions', {enabled: true, value: 'percentage'});

    if (!isEmpty(gridData)) {
      const {series, categories} = this.splitToSeriesAndCategories(gridData, fields, opts, type);
      const {chartSeries} = this.customizeSeriesForChartType(series, type, categories, fields, opts);

      forEach(chartSeries, seriesData => {
        seriesData.name = yLabel;
        seriesData.dataLabels = seriesData.dataLabels || {};
        seriesData.dataLabels.enabled = labelOptions.enabled;
        /* eslint-disable */
        seriesData.dataLabels.formatter = function () {
          if (this.percentage <= 5) {
            return null;
          }
          const data = labelOptions.value === 'percentage' ? this.percentage : this.y;
          const isPercent = labelOptions.value === 'percentage';
          return `${this.point.name}: ${addCommas(round(data, 2))}${isPercent ? '%' : '' }`;
        };
        /* eslint-enable */
      });
      changes.push({
        path: 'series',
        data: chartSeries
      });
    }

    return changes;
  }

  getYAxesChanges(type, fields, opts) {
    const panes = fields.length;
    const labelHeight = 15;
    if (type !== 'tsPane') {
      const changes = fpPipe(
        fpGroupBy('comboType'),
        fpToPairs,
        fpMap(([, fields]) => {
          const titleText = map(fields, field => {
            if (!isUndefined(field.alias)) {
              return field.alias || `${AGGREGATE_TYPES_OBJ[field.aggregate].label} ${field.displayName}`;
            }
            return opts.labels.y || `${AGGREGATE_TYPES_OBJ[field.aggregate].label} ${field.displayName}`;
          }).join('<br/>');
          const isSingleField = fields.length === 1;
          return {
            gridLineWidth: 0,
            opposite: true,
            columnName: isSingleField ? fields[0].columnName : null,
            title: {
              useHtml: true,
              margin: labelHeight * fields.length,
              text: titleText,
              style: isSingleField
            },
            labels: {
              style: isSingleField
            }
          };
        })
      )(fields);

      forEach(changes, (change, changeIndex) => {
        if (changeIndex === 0) {
          change.opposite = false;
        }
        if (change.columnName) {
          const fieldIndex = findIndex(fields, ({columnName}) => columnName === change.columnName);
          const style = {color: CHART_COLORS[fieldIndex]};
          change.title.style = change.title.style ? style : null;
          change.labels.style = change.labels.style ? style : null;
        }
      });
      return changes;
    }
    const changes = [];
    forEach(fields, (field, index) => {
      const titleText = field.alias || `${AGGREGATE_TYPES_OBJ[field.aggregate].label} ${field.displayName}`;
      changes.push({
        labels: {
          align: 'right',
          x: -3
        },
        title: {
          text: titleText
        },
        top: this.paneTopPercent(panes, index),
        height: panes === 1 ? '100%' : `${100 / panes}%`,
        offset: 0,
        lineWidth: 2,
        resize: {
          enabled: panes !== index + 1,
          lineWidth: 3,
          lineDashStyle: 'solid',
          lineColor: '#cccccc',
          x: 0,
          y: 0
        }
      });
    });
    return changes;
  }

  paneTopPercent(panes, index) {
    if (panes === 1 || (panes !== 1 && index === 0)) {
      return null;
    }
    const height = 100 / panes;
    const top = height * index;
    return `${top}%`;
  }

  getBarChangeConfig(type, settings, fields, gridData, opts) {
    const labels = {
      x: get(fields, 'x.alias') || get(opts, 'labels.x') || get(fields, 'x.displayName', '')
    };

    const changes = [{
      path: 'xAxis.title.text',
      data: labels.x || (opts.labels && opts.labels.x)
    }];

    const yAxesChanges = this.getYAxesChanges(type, fields.y, opts);

    changes.push({
      path: 'yAxis',
      data: yAxesChanges
    });

    if (!isEmpty(gridData)) {
      const {series, categories} = this.splitToSeriesAndCategories(gridData, fields, opts, type);
      changes.push({
        path: 'series',
        data: series
      });
      // add the categories
      forEach(categories, (category, k) => {
        changes.push({
          path: `${k}Axis.categories`,
          data: category
        });
      });
    }

    return changes;
  }

  dataToChangeConfig(type, settings, gridData, opts) {
    let changes;
    const fields = {
      x: find(settings.xaxis, attr => attr.checked === 'x'),
      y: filter(settings.yaxis, attr => attr.checked === 'y'),
      z: find(settings.zaxis, attr => attr.checked === 'z'),
      g: find(settings.groupBy, attr => attr.checked === 'g')
    };

    switch (type) {
    case 'pie':
      changes = this.getPieChangeConfig(type, settings, fields, gridData, opts);
      break;
    case 'column':
    case 'bar':
    case 'line':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'bubble':
    case 'tsspline':
    case 'tsPane':
    default:
      changes = this.getBarChangeConfig(type, settings, fields, gridData, opts);
      break;
    }

    return concat(
      changes,
      this.addTooltipsAndLegend(fields, type, opts)
    );
  }

  addTooltipsAndLegend(fields, type, opts) {
    const changes = [];

    if (!this.getViewOptionsFor(type).customTooltip) {
      return changes;
    }

    const xIsNumber = NUMBER_TYPES.includes(fields.x.type);
    const xIsString = fields.x.type === 'string';
    // x can be either a string, a number or a date
    // string -> use the value from categories
    // number -> restrict the number to 2 decimals
    // date -> just show the date
    const xStringValue = xIsString ?
      'point.key' : xIsNumber ?
        'point.x:,.2f' : 'point.x';
    const xAxisString = `<tr>
      <th>${fields.x.alias || get(opts, 'labels.x', '') || fields.x.displayName}:</th>
      <td>{${xStringValue}}</td>
    </tr>`;

    const yIsSingle = fields.y.length === 1;
    const yAxisString = `<tr>
      <th>${fields.y.alias || get(opts, 'labels.y', '') || '{series.name}'}:</th>
      <td>{point.y:,.2f}</td>
    </tr>`;
    const zAxisString = fields.z ?
      `<tr><th>${fields.z.alias || get(opts, 'labels.z', '') || fields.z.displayName}:</th><td>{point.z:,.2f}</td></tr>` :
      '';
    const groupString = fields.g ?
      `<tr><th>Group:</th><td>{point.g}</td></tr>` :
      '';

    let tooltipObj = {
      useHTML: true,
      headerFormat: `<table> ${xIsString ? xAxisString : ''}`,
      pointFormat: `${xIsNumber ? xAxisString : ''}
        ${yAxisString}
        ${zAxisString}
        ${groupString}`,
      footerFormat: '</table>',
      followPointer: true
    };

    if (type.substring(0, 2) === 'ts') {
      tooltipObj = {
        enabled: true,
        useHTML: true,
        valueDecimals: 3,
        split: true,
        shared: false,
        // headerFormat: `<table>`,
        pointFormat: `<table> ${yAxisString}
          ${zAxisString}`,
        footerFormat: '</table>',
        followPointer: true
      };
    }

    changes.push({
      path: 'tooltip',
      data: tooltipObj
    });

    // if there is no grouping disable the legend
    // because there is only one data series
    changes.push({
      path: 'legend.enabled',
      data: Boolean(!yIsSingle || fields.g)
    });
    return changes;
  }

  filterNumberTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      NUMBER_TYPES.includes(attr.type)
    ));
  }

  filterNonNumberTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      !NUMBER_TYPES.includes(attr.type)
    ));
  }

  filterDateTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      DATE_TYPES.includes(attr.type)
    ));
  }

  fillSettings(artifacts, model) {
    /* Flatten the artifacts into a single array and sort them */
    const attributes = fpPipe(
      fpFlatMap(metric => {
        return map(metric.columns, attr => {
          attr.tableName = metric.artifactName;
          return attr;
        });
      }),
      fpSortBy('displayName')
    )(artifacts);

    let settingsObj;
    let zaxis;
    const yaxis = this.filterNumberTypes(attributes);
    let xaxis = attributes;
    const groupBy = this.filterNonNumberTypes(attributes);

    switch (model.chartType) {
    case 'bubble':
      zaxis = this.filterNumberTypes(attributes);
      settingsObj = {
        xaxis,
        yaxis,
        zaxis,
        groupBy
      };
      break;
    case 'tsspline':
    case 'tsPane':
      xaxis = this.filterDateTypes(attributes);
      settingsObj = {
        xaxis,
        yaxis,
        groupBy
      };
      break;
    default:
      settingsObj = {
        yaxis,
        xaxis,
        groupBy
      };
    }
    return settingsObj;
  }
}
