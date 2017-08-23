import get from 'lodash/get';
import set from 'lodash/set';
import sum from 'lodash/sum';
import map from 'lodash/map';
import round from 'lodash/round';
import flatMap from 'lodash/flatMap';
import assign from 'lodash/assign';
import find from 'lodash/find';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';
import indexOf from 'lodash/indexOf';
import isEmpty from 'lodash/isEmpty';
import fpPipe from 'lodash/fp/pipe';
import fpOmit from 'lodash/fp/omit';
import fpMapKeys from 'lodash/fp/mapKeys';
import fpMapValues from 'lodash/fp/mapValues';
import groupBy from 'lodash/groupBy';
import fpFlatMap from 'lodash/fp/flatMap';
import fpSortBy from 'lodash/fp/sortBy';
import reduce from 'lodash/reduce';
import concat from 'lodash/concat';

import {NUMBER_TYPES} from '../consts';

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

  /* Returns default chart config for various chart types */
  const getChartConfigFor = (type, options) => {
    const legendPosition = LEGEND_POSITIONING[get(options, 'legend.align', 'right')];
    const legendLayout = LAYOUT_POSITIONS[get(options, 'legend.layout', 'vertical')];

    const SPACING = 45;

    const config = {
      chart: {
        type: (type === 'line' ? 'spline' : type) || 'column',
        spacingLeft: SPACING,
        spacingRight: SPACING,
        spacingBottom: SPACING,
        spacingTop: SPACING,
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

    /** the mapping between the field columnNames, and the chart axes
   * the backend returns the aggregate data in with the fields columnName as property
   * the bubble chart requires x, y, z for the axes if they are of number type
   * Example:
   * AVAILABLE_MB -> x
   */
  function getDataFieldMap(dataFields) {
    return reduce(dataFields, (accumulator, field) => {
      accumulator[field.columnName] = field.checked;
      return accumulator;
    }, {});
  }

  /** the mapping between the tree node names and the chart axes or groupBy names
   * the backend returns the string type data as tree node names
   * the bubble chart requires x, y, z for the axes if they are of type number
   * it can be an array, because the only useful in the tree node is the index
   * Example:
   * string_field_1: 0 -> g (marker on the checked attribute)
   * string_field_2: 1 -> y
   */
  function getNodeFieldMap(nodeFields) {
    return map(nodeFields, 'checked');
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
    const dataFieldMap = getDataFieldMap(sqlBuilder.dataFields);
    return parseNode(data, {}, nodeFieldMap, dataFieldMap, 1);
  }

  function parseNode(node, dataObj, nodeFieldMap, dataFieldMap, level) {
    if (node.key) {
      dataObj[nodeFieldMap[level - 2]] = node.key;
    }

    const childNode = node[`node_field_${level}`];
    if (childNode) {
      const data = flatMap(childNode.buckets, bucket => parseNode(bucket, dataObj, nodeFieldMap, dataFieldMap, level + 1));
      return data;
    }
    const datum = parseLeaf(node, dataObj, dataFieldMap);
    return datum;
  }

  function parseLeaf(node, dataObj, dataFieldsMap) {
    const dataFields = fpPipe(
      fpOmit(['doc_count', 'key']),
      fpMapKeys(k => {
        return dataFieldsMap[k];
      }),
      fpMapValues('value')
    )(node);

    return assign(
      dataFields,
      dataObj
    );
  }

  function splitToSeriesAndCategories(parsedData, fields) {
    const series = [];
    const categories = {};

    // if there is a group by
    // // group the data into multiple series
    if (fields.g) {
      const groupedData = groupBy(parsedData, 'g');
      forEach(groupedData, (group, k) => {
        series.push({
          name: k,
          data: group
        });
      });
    } else {
      series[0] = {
        data: parsedData
      };
    }

    forEach(series, serie => {
      forEach(serie.data, dataPoint => {
        forEach(dataPoint, (v, k) => {
          if (isCategoryAxis(fields, k)) {
            addToCategory(categories, k, v);
            dataPoint[k] = indexOf(categories[k], v);
          }
        });
      });
    });

    return {
      series,
      categories
    };
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
    const isCategoryAxis = isAxis && (dataType === 'string' || dataType === 'String');
    /* eslint-enable angular/typecheck-string */
    return isCategoryAxis;
  }

  function dataToNestedDonut(series, categories, fields) {
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

    /* eslint-disable */
    const chartSeries = [{
      name: get(fields, 'y.displayName'),
      data: innerData,
      dataLabels: {
        formatter: function () {
          return this.percentage > 5 ? `${this.point.name}: ${round(this.percentage, 2)}%` : null;
        },
        color: '#ffffff',
        distance: -30
      },
      size: '60%'
    }, {
      name: get(fields, 'y.displayName'),
      data: outerData,
      dataLabels: {
        formatter: function () {
          return `${this.point.name}: ${round(this.percentage, 2)}%`;
        }
      },
      size: '100%',
      innerSize: '60%',
      id: 'outerData'
    }];
    /* eslint-enable */

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
        mapperFn = ({x, y}) => [x, y];
        forEach(series, serie => {
          serie.data = map(serie.data, mapperFn);
        });
        return {chartSeries: series};

      case 'pie':
        if (!fields.g) {
          set(series, '0.name', get(fields, 'y.displayName'));
          set(series, '0.dataLabels.format', '{point.name}: {point.percentage:.2f}%');
          mapperFn = ({x, y}) => {
            const category = get(categories, `x.${x}`);
            return {name: category, y, drilldown: category};
          };
          forEach(series, serie => {
            serie.data = map(serie.data, mapperFn);
          });

          chartSeries = series;

        } else {
          chartSeries = dataToNestedDonut(series, categories, fields);
        }

        return {chartSeries};

      case 'bubble':
        // the bubble chart already supports the parsed data
        return {chartSeries: series};

      default:
        throw new Error(`Chart type: ${chartType} is not supported!`);
    }
  }

  function getPieChangeConfig(type, settings, fields, gridData) {
    const changes = [];

    if (!isEmpty(gridData)) {
      const {series, categories} = splitToSeriesAndCategories(gridData, fields);
      const {chartSeries} = customizeSeriesForChartType(series, type, categories, fields);
      changes.push({
        path: 'series',
        data: chartSeries
      });
    }

    return changes;
  }

  function getBarChangeConfig(type, settings, fields, gridData, opts) {
    const labels = {
      x: get(fields, 'x.displayName', ''),
      y: get(fields, 'y.displayName', '')
    };

    const changes = [{
      path: 'xAxis.title.text',
      data: (opts.labels && opts.labels.x) || labels.x
    }, {
      path: 'yAxis.title.text',
      data: (opts.labels && opts.labels.y) || labels.y
    }];

    if (!isEmpty(gridData)) {
      const {series, categories} = splitToSeriesAndCategories(gridData, fields);
      const {chartSeries} = customizeSeriesForChartType(series, type, categories, fields);
      changes.push({
        path: 'series',
        data: chartSeries
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
      y: find(settings.yaxis, attr => attr.checked === 'y'),
      z: find(settings.zaxis, attr => attr.checked === 'z'),
      g: find(settings.groupBy, attr => attr.checked === 'g')
    };

    switch (type) {
      case 'column':
      case 'bar':
      case 'line':
      case 'spline':
      case 'stack':
      case 'scatter':
      case 'bubble':
        changes = getBarChangeConfig(type, settings, fields, gridData, opts);
        break;

      case 'pie':
        changes = getPieChangeConfig(type, settings, fields, gridData, opts);
        break;

      default:
        changes = getBarChangeConfig(type, settings, fields, gridData, opts);
        break;
    }

    return concat(
      changes,
      addSpecificChartConfig(type, fields)
    );
  };

  function addSpecificChartConfig(chartType, fields) {
    const changes = [];
    if (chartType === 'bubble') {
      const groupString = `<tr><th colspan="2"><h3>{point.g}</h3></th></tr>`;
      const xIsNumber = NUMBER_TYPES.includes(fields.x.type);
      const yIsNumber = NUMBER_TYPES.includes(fields.y.type);
      // z is always a number
      changes.push({
        path: 'tooltip',
        data: {
          useHTML: true,
          headerFormat: '<table>',
          pointFormat: `${fields.g ? groupString : ''}
              <tr><th>${fields.x.displayName}:</th><td>{point.x${xIsNumber ? ':,.2f' : ''}}</td></tr>
              <tr><th>${fields.y.displayName}:</th><td>{point.y${yIsNumber ? ':,.2f' : ''}}</td></tr>
              <tr><th>${fields.z.displayName}:</th><td>{point.z:,.2f}</td></tr>`,
          footerFormat: '</table>',
          followPointer: true
        }
      });
    }

    // if there is no grouping disable the legend
    // because there is only one data series
    changes.push({
      path: 'legend.enabled',
      data: Boolean(fields.g)
    });
    return changes;
  }

  function filterNumberTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      NUMBER_TYPES.includes(attr.type)
    ));
  }

  function filterStringTypes(attributes) {
    return filter(attributes, attr => (
      attr.columnName &&
      (attr.type === 'string' || attr.type === 'String')
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

    let xaxis;
    let yaxis;
    let zaxis;
    let settingsObj;
    const groupBy = filterStringTypes(attributes);

    switch (model.chartType) {
      case 'bubble':
        xaxis = filterStringTypes(attributes);
        yaxis = attributes;
        zaxis = filterNumberTypes(attributes);
        settingsObj = {
          xaxis,
          yaxis,
          zaxis,
          groupBy
        };
        break;
      default:
        xaxis = filterStringTypes(attributes);
        yaxis = filterNumberTypes(attributes);
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
    dataToChangeConfig,
    fillSettings,
    parseData,

    LEGEND_POSITIONING,
    LAYOUT_POSITIONS
  };

}
