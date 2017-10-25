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
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as reduce from 'lodash/reduce';
import * as concat from 'lodash/concat';
import * as compact from 'lodash/compact';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as mapValues from 'lodash/mapValues';
import * as sortBy from 'lodash/sortBy';
import * as moment from 'moment';
import * as toString from 'lodash/toString';

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

export function ChartService(Highcharts) {
  'ngInject';

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

  function addCommas(nStr) {
    nStr = String(nStr);
    const x = nStr.split('.');
    let x1 = x[0];
    const x2 = x.length > 1 ? '.' + x[1] : '';
    const rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
      x1 = x1.replace(rgx, '$1,$2');
    }
    return x1 + x2;
  }

  function updateAnalysisModel(analysis) {
    switch (analysis.chartType) {
    case 'pie':
      analysis.labelOptions = analysis.labelOptions || {enabled: true, value: 'percentage'};
      break;
    default:
      break;
    }
    return analysis;
  }

  /* Returns default chart config for various chart types */
  const getChartConfigFor = (type, options) => {
    const initialLegendPosition = type === 'combo' ? 'top' : 'right';
    const initialLegendLayout = type === 'combo' ? 'horizontal' : 'vertical';
    const legendPosition = LEGEND_POSITIONING[get(options, 'legend.align', initialLegendPosition)];
    const legendLayout = LAYOUT_POSITIONS[get(options, 'legend.layout', initialLegendLayout)];

    const SPACING = 45;
    const HEIGHT = (angular.isUndefined(options.chart) ? 400 : options.chart.height);

    const config = {
      chart: {
        type: ['combo', 'bar'].includes(type) ? null : splinifyChartType(type),
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
        data: [0, 0, 0, 0, 0]
      }],
      yAxis: {
        title: {x: -15}
      },
      xAxis: {
        categories: ['A', 'B', 'C', 'D', 'E'],
        title: {y: 15}
      }
    };

    if (configCustomizer[type]) {
      return configCustomizer[type](config);
    }
    return config;
  };

  /* Provides view related configuration for various chart types */
  function getViewOptionsFor(type) {
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
    default:
      return config;
    }
  }

  /** the mapping between the tree level, and the columName of the field
   * Example:
   * string_field_1: 0 -> SOURCE_OS (marker on the checked attribute)
   * string_field_2: 1 -> SOURCE_MANUFACTURER
   */
  function getNodeFieldMap(nodeFields) {
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
  function parseData(data, sqlBuilder) {
    const nodeFieldMap = getNodeFieldMap(sqlBuilder.nodeFields);
    return parseNode(data, {}, nodeFieldMap, 1);
  }

  function parseNode(node, dataObj, nodeFieldMap, level) {
    if (node.key) {
      dataObj[nodeFieldMap[level - 2]] = node.key;
    }

    const childNode = node[`node_field_${level}`];
    if (childNode) {
      const data = flatMap(childNode.buckets, bucket => parseNode(bucket, dataObj, nodeFieldMap, level + 1));
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

  function splitToSeriesAndCategories(parsedData, fields, {sorts}) {
    let series = [];
    const categories = {};
    const areMultipleYAxes = fields.y.length > 1;
    const isGrouped = fields.g;

    const fieldsArray = compact([fields.x, ...fields.y, fields.z, fields.g]);
    const dateFields = filter(fieldsArray, ({type}) => DATE_TYPES.includes(type));
    formatDatesIfNeeded(parsedData, dateFields);

    if (areMultipleYAxes) {
      series = splitSeriesByYAxes(parsedData, fields);
    } else if (isGrouped) {
      series = splitSeriesByGroup(parsedData, fields);
    } else {
      const axesFieldNameMap = getAxesFieldNameMap(fields);
      const yField = fields.y[0];
      series = [getSerie(yField, 0)];
      series[0].data = map(parsedData,
        dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])
      );
    }
    // split out categories frem the data
    forEach(series, serie => {
      serie.data = map(serie.data, point => {
        const dataPoint = clone(point);
        forEach(dataPoint, (v, k) => {
          if (isCategoryAxis(fields, k)) {
            addToCategory(categories, k, v);
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
          value => getSortValue(field, value),
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
          if (isCategoryAxis(fields, k)) {
            dataPoint[k] = indexOf(categories[k], v);
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

  function splinifyChartType(type) {
    switch (type) {
    case 'line':
      return 'spline';
    case 'area':
      return 'areaspline';
    default:
      return type;

    }
  }

  function getSortValue(field, value) {
    if (!field) {
      return value;
    }

    if (DATE_TYPES.includes(field.type)) {
      return moment(value, field.dateFormat);
    }

    return value;
  }

  function formatDatesIfNeeded(parsedData, dateFields) {
    if (!isEmpty(dateFields)) {
      forEach(parsedData, dataPoint => {
        forEach(dateFields, ({columnName, dateFormat}) => {
          dataPoint[columnName] = moment(dataPoint[columnName]).format(dateFormat);
        });
      });
    }
  }

  function getSerie({alias, displayName, comboType, aggregate}, index) {
    const splinifiedChartType = splinifyChartType(comboType);
    const zIndex = getZIndex(comboType);
    return {
      name: alias || `${AGGREGATE_TYPES_OBJ[aggregate].label} ${displayName}`,
      type: splinifiedChartType,
      yAxis: index,
      zIndex,
      data: []
    };
  }

  function getZIndex(type) {
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

  function splitSeriesByYAxes(parsedData, fields) {
    const axesFieldNameMap = getAxesFieldNameMap(fields, 'y');
    const series = map(fields.y, getSerie);

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

  function splitSeriesByGroup(parsedData, fields) {
    const axesFieldNameMap = getAxesFieldNameMap(fields);
    const comboType = fields.y[0].comboType;

    return fpPipe(
      fpMap(dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])),
      fpGroupBy('g'),
      fpToPairs,
      fpMap(([name, data]) => ({name, data, type: comboType}))
    )(parsedData);
  }

  /**
   * get the map from colmnNames to the axes, or group
   * Ex
   * y -> AVAILABLE_MB
   */
  function getAxesFieldNameMap(fields, exclude) {
    // y axis ommitted because it is added in splitSeriesByYAxes
    const y = exclude === 'y' ? [] : fields.y;
    const fieldsArray = compact([fields.x, ...y, fields.z, fields.g]);
    return reduce(fieldsArray, (accumulator, field) => {
      accumulator[field.checked] = field.columnName;
      return accumulator;
    }, {});
  }

  function addToCategory(categories, key, newCategoryValue) {
    if (!categories[key]) {
      categories[key] = [];
    }
    if (indexOf(categories[key], newCategoryValue) < 0) {
      categories[key].push(newCategoryValue);
    }
  }

  function isCategoryAxis(fields, key) {
    const dataType = get(fields, `${key}.type`);
    const isAxis = key !== 'g';
    // strings should be represented as categories in the chart
    /* eslint-disable angular/typecheck-string */
    const isCategoryAxis = isAxis &&
      (dataType === 'string' || dataType === 'String' || DATE_TYPES.includes(dataType));
    /* eslint-enable angular/typecheck-string */
    return isCategoryAxis;
  }

  function dataToNestedDonut(series, categories) {
    /* Group by option forms the inner circle. X axis option forms the outer region
       This logic is adapted from https://www.highcharts.com/demo/pie-donut */

    const colors = Highcharts.getOptions().colors;
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
          color: Highcharts.Color(data[i].color).brighten(brightness).get()
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

  function customizeSeriesForChartType(series, chartType, categories, fields) {
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
        chartSeries = dataToNestedDonut(series, categories);
      }

      return {chartSeries};

    default:
      throw new Error(`Chart type: ${chartType} is not supported!`);
    }
  }

  function getPieChangeConfig(type, settings, fields, gridData, opts) {
    const changes = [];
    const yField = get(fields, 'y.0', {});
    const yLabel = get(opts, 'labels.y') || `${AGGREGATE_TYPES_OBJ[yField.aggregate].label} ${yField.displayName}`;

    const labelOptions = get(opts, 'labelOptions', {enabled: true, value: 'percentage'});

    if (!isEmpty(gridData)) {
      const {series, categories} = splitToSeriesAndCategories(gridData, fields, opts);
      const {chartSeries} = customizeSeriesForChartType(series, type, categories, fields, opts);

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

  function getBarChangeConfig(type, settings, fields, gridData, opts) {
    const labels = {
      x: get(fields, 'x.alias', get(fields, 'x.displayName', ''))
    };

    const changes = [{
      path: 'xAxis.title.text',
      data: (opts.labels && opts.labels.x) || labels.x
    }];

    changes.push({
      path: 'yAxis',
      data: map(fields.y, (y, k) => ({
        gridLineWidth: 0,
        opposite: k > 0,
        title: {
          text: y.alias || `${AGGREGATE_TYPES_OBJ[y.aggregate].label} ${y.displayName}`,
          style: fields.y.length <= 1 ? null : {
            color: CHART_COLORS[k]
          }
        },
        labels: {
          style: fields.y.length <= 1 ? null : {
            color: CHART_COLORS[k]
          }
        }
      }))
    });

    if (!isEmpty(gridData)) {
      const {series, categories} = splitToSeriesAndCategories(gridData, fields, opts);
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

  const dataToChangeConfig = (type, settings, gridData, opts) => {
    let changes;
    const fields = {
      x: find(settings.xaxis, attr => attr.checked === 'x'),
      y: filter(settings.yaxis, attr => attr.checked === 'y'),
      z: find(settings.zaxis, attr => attr.checked === 'z'),
      g: find(settings.groupBy, attr => attr.checked === 'g')
    };

    switch (type) {
    case 'pie':
      changes = getPieChangeConfig(type, settings, fields, gridData, opts);
      break;
    case 'column':
    case 'bar':
    case 'line':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'bubble':
    default:
      changes = getBarChangeConfig(type, settings, fields, gridData, opts);
      break;
    }

    return concat(
      changes,
      addTooltipsAndLegend(fields, type)
    );
  };

  function addTooltipsAndLegend(fields, type) {
    const changes = [];

    if (!getViewOptionsFor(type).customTooltip) {
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
        'point.x:.2f' : 'point.x';
    const xAxisString = `<tr>
      <th>${fields.x.displayName}:</th>
      <td>{${xStringValue}}</td>
    </tr>`;

    const yIsSingle = fields.y.length === 1;
    const yAxisString = `<tr>
      <th>{series.name}:</th>
      <td>{point.y:.2f}</td>
    </tr>`;
    const zAxisString = fields.z ?
    `<tr><th>${fields.z.displayName}:</th><td>{point.z:.2f}</td></tr>` :
    '';
    const groupString = fields.g ?
    `<tr><th>Group:</th><td>{point.g}</td></tr>` :
    '';

    const tooltipObj = {
      useHTML: true,
      headerFormat: `<table> ${xIsString ? xAxisString : ''}`,
      pointFormat: `${xIsNumber ? xAxisString : ''}
        ${yAxisString}
        ${zAxisString}
        ${groupString}`,
      footerFormat: '</table>',
      followPointer: true
    };

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

  function filterNumberTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      NUMBER_TYPES.includes(attr.type)
    ));
  }

  function filterNonNumberTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      !NUMBER_TYPES.includes(attr.type)
    ));
  }

  function fillSettings(artifacts, model) {
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
    const yaxis = filterNumberTypes(attributes);
    const xaxis = attributes;
    const groupBy = filterNonNumberTypes(attributes);

    switch (model.chartType) {
    case 'bubble':
      zaxis = filterNumberTypes(attributes);
      settingsObj = {
        xaxis,
        yaxis,
        zaxis,
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

  return {
    getChartConfigFor,
    getViewOptionsFor,
    dataToChangeConfig,
    fillSettings,
    parseData,
    updateAnalysisModel,

    LEGEND_POSITIONING,
    LAYOUT_POSITIONS
  };

}