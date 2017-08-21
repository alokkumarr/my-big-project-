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
import fpToPairs from 'lodash/fp/toPairs';
import fpJoin from 'lodash/fp/join';
import fpMap from 'lodash/fp/map';
import fpMapValues from 'lodash/fp/mapValues';
import fpFlatMap from 'lodash/fp/flatMap';
import fpSortBy from 'lodash/fp/sortBy';
import reduce from 'lodash/reduce';
import concat from 'lodash/concat';
import compact from 'lodash/compact';
import fpGroupBy from 'lodash/fp/groupBy';
import mapValues from 'lodash/mapValues';

import {NUMBER_TYPES, AGGREGATE_TYPES_OBJ} from '../consts';

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

  function splitToSeriesAndCategories(parsedData, fields) {
    let series = [];
    const categories = {};
    const areMultipleYAxes = fields.y.length > 1;
    const isGrouped = fields.g;

    if (areMultipleYAxes) {
      series = splitSeriesByYAxes(parsedData, fields);
    } else if (isGrouped) {
      series = splitSeriesByGroup(parsedData, fields);
    } else {
      const axesFieldNameMap = getAxesFieldNameMap(fields);
      const yField = fields.y[0];
      series = [{
        name: yField.alias || yField.displayName,
        data: map(parsedData,
          dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val]))
      }];
    }
    // split out categories form the data
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

  function splitSeriesByGroup(parsedData, fields) {
    const axesFieldNameMap = getAxesFieldNameMap(fields);

    return fpPipe(
      fpMap(dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])),
      fpGroupBy('g'),
      fpToPairs,
      fpMap(([name, data]) => ({name, data}))
    )(parsedData);
  }

  function splitSeriesByYAxes(parsedData, fields) {
    const axesFieldNameMap = getAxesFieldNameMap(fields, 'y');
    const series = map(fields.y, ({alias, displayName}) => (
      {name: alias || displayName, data: []})
    );

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
    const isCategoryAxis = isAxis && (dataType === 'string' || dataType === 'String');
    /* eslint-enable angular/typecheck-string */
    return isCategoryAxis;
  }

  const dataToChangeConfig = (type, settings, gridData, opts) => {
    const fields = {
      x: find(settings.xaxis, attr => attr.checked === 'x'),
      y: filter(settings.yaxis, attr => attr.checked === 'y'),
      z: find(settings.zaxis, attr => attr.checked === 'z'),
      g: find(settings.groupBy, attr => attr.checked === 'g')
    };

    const labels = {
      x: get(fields, 'x.displayName', ''),
      y: fpPipe(
        fpMap(field => `${AGGREGATE_TYPES_OBJ[field.aggregate].label} ${field.displayName}`),
        fpJoin(' | ')
      )(fields.y)
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

    return concat(
      changes,
      addTooltipsAndLegend(fields)
    );
  };

  function addTooltipsAndLegend(fields) {
    const changes = [];

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
      <th>${fields.x.displayName}:</th>
      <td>{${xStringValue}}</td>
    </tr>`;

    const yIsSingle = fields.y.length === 1;
    const yIsNumber = NUMBER_TYPES.includes(fields.y.type);

    const yAxisName = `${yIsSingle || fields.g ? fields.y[0].displayName : '{series.name}'}`;
    const yAxisString = `<tr>
      <th>${yAxisName}:</th>
      <td>{point.y${yIsNumber ? ':,.2f' : ''}}</td>
    </tr>`;
    const zAxisString = fields.z ?
    `<tr><th>${fields.z.displayName}:</th><td>{point.z:,.2f}</td></tr>` :
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
