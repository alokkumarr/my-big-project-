import get from 'lodash/get';
import set from 'lodash/set';
import map from 'lodash/map';
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

export function ChartService() {
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
    set(config, 'plotOptions.pie.showInLegend', true);
    set(config, 'series', [{
      name: 'Brands',
      colorByPoint: true,
      data: []
    }]);
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
        layout: legendLayout.layout
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

  /** parse the tree structure data and return a flattened array:
   * [{
   *   x: ..,
   *   y: ..,
   *   g: ..,
   *   z: ..
   * }, ..]
   */
  function parseData(data, nodeFieldMap, dataFieldMap) {
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

  function customizeSeriesForChartType(series, chartType) {
    let mapperFn;
    switch (chartType) {
      case 'column':
      case 'bar':
      case 'line':
      case 'spline':
      case 'stack':
      case 'scatter':
        mapperFn = ({x, y}) => [x, y];
        break;
      case 'bubble':
        // the bubble chart already supports the parsed data
        return;
      default:
        throw new Error(`Chart type: ${chartType} is not supported!`);
    }
    forEach(series, serie => {
      serie.data = map(serie.data, mapperFn);
    });
  }

  const dataToChangeConfig = (type, settings, gridData, opts) => {
    const fields = {
      x: find(settings.xaxis, attr => attr.checked === 'x'),
      y: find(settings.yaxis, attr => attr.checked === 'y'),
      z: find(settings.zaxis, attr => attr.checked === 'z'),
      g: find(settings.groupBy, attr => attr.checked === 'g')
    };

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
      customizeSeriesForChartType(series, type);
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
  };

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
      fpSortBy('columnName')
    )(artifacts);

    let xaxis;
    let yaxis;
    let zaxis;
    let settingsObj;
    const groupBy = filterStringTypes(attributes);

    switch (model.chartType) {
      case 'bubble':
        xaxis = attributes;
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
