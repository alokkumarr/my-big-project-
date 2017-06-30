import get from 'lodash/get';
import set from 'lodash/set';
import map from 'lodash/map';
import flatMap from 'lodash/flatMap';
import sortBy from 'lodash/sortBy';
import reduce from 'lodash/reduce';
import keys from 'lodash/keys';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';
import fpPipe from 'lodash/fp/pipe';
import fpToPairs from 'lodash/fp/toPairs';
import fpMap from 'lodash/fp/map';
import fpGroupBy from 'lodash/fp/groupBy';
import fpGet from 'lodash/fp/get';
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

  const hasNoData = categories => {
    return (angular.isArray(categories) &&
            categories[0] === 'undefined');
  };

  const gridToPie = (x, y, g, grid) => {
    const result = {
      name: x.displayName,
      colorByPoint: true,
      data: []
    };

    const defaultSeriesName = x ? x.displayName : 'Series 1';
    const series = reduce(grid, (obj, row) => {
      const category = row[x.columnName] || defaultSeriesName;
      obj[category] = obj[category] || 0;
      obj[category] += row[y.columnName];
      return obj;
    }, {});

    forEach(keys(series), key => {
      result.data.push({name: key, y: series[key]});
    });

    return [
      {
        path: 'series',
        data: [result]
      }
    ];
  };

  const gridToChart = (x, y, l, data) => {
    const defaultSeriesName = x ? x.displayName : 'Series 1';

    const categories = map(
      get(data, 'group_by.buckets'),
      bucket => bucket.key
    );

    const defaultSeries = () => reduce(categories, (obj, cat) => {
      obj[cat] = 0;
      return obj;
    }, {});

    /* Produces an object where each key is a unique value for @l column.
       The corresponding value is another object, where each key value pair
       is a value for @x column, and @y.
       */
    const res = reduce(
      get(data, 'group_by.buckets'),
      (obj, bucket) => {
        forEach(
          get(bucket, 'split_by.buckets', [{
            key: defaultSeriesName,
            [y.columnName]: {value: get(bucket[y.columnName], 'value', 0)}
          }]),
          subBucket => {
            obj[subBucket.key] = obj[subBucket.key] || defaultSeries();
            obj[subBucket.key][bucket.key] = subBucket[y.columnName].value;
          }
        );

        return obj;
      },
      {}
    );

    const xCategories = keys(defaultSeries());
    return [
      {
        path: 'xAxis.categories',
        data: hasNoData(xCategories) ? ['X-Axis'] : xCategories
      },
      {
        path: 'series',
        data: keys(res).map(k => ({
          name: k,
          data: xCategories.map(c => res[k][c])
        }))
      }
    ];
  };

  /* backend                 --- frontend
   * group_by                --- x (x-axis)
   * split_by                --- g (groupBy on fontend)
   * values in lowest bucket --- y,z (y-axis, z-axis)
  */
  const gridToBubble = (x, y, g, data, z) => {
    console.log('x: ', x);
    console.log('y: ', y);
    console.log('z: ', z);
    console.log('group: ', g);
    console.log('data: ', data);

    const series = fpPipe(
      fpGet('group_by.buckets'),
      fpFlatMap(xBucket => map(get(xBucket, 'split_by.buckets'),
        gBucket => {
          return {
            x: xBucket.key,
            y: gBucket[y.columnName].value,
            z: gBucket[z.columnName].value,
            g: gBucket.key
          };
        }
      )),
      fpGroupBy('g'),
      fpToPairs,
      fpMap(([groupName, groupValues]) => {
        return {
          name: groupName,
          data: groupValues
        };
      })
    )(data);

    console.log('series', series);

    return [{
      path: 'series',
      data: series
    }];
  };

  /* Functions to convert grid data to chart options forEach
     various chart types */
  const dataCustomizer = {
    column: gridToChart,
    bar: gridToChart,
    line: gridToChart,
    spline: gridToChart,
    stack: gridToChart,
    scatter: gridToChart,
    bubble: gridToBubble,
    pie: gridToPie,
    donut: gridToPie
  };

  const dataToChangeConfig = (type, settings, gridData, opts) => {
    const xaxis = filter(settings.xaxis, attr => attr.checked === 'x')[0] || {};
    const yaxis = filter(settings.yaxis, attr => attr.checked === 'y')[0] || {};
    const group = filter(settings.groupBy, attr => attr.checked === 'g')[0] || {};

    const zaxis = type === 'bubble' ? filter(settings.zaxis, attr => attr.checked === 'z')[0] || {} : null;
    const changes = dataCustomizer[type](xaxis, yaxis, group, gridData, zaxis);

    return changes.concat([
      {
        path: 'xAxis.title.text',
        data: get(opts, 'labels.x', xaxis.displayName) || xaxis.displayName
      },
      {
        path: 'yAxis.title.text',
        data: get(opts, 'labels.y', yaxis.displayName) || yaxis.displayName
      }
    ]);
  };

  function mergeArtifactsWithSettings(artifacts, record = {}, marker) {
    forEach(artifacts, a => {
      if (a.columnName === record.columnName &&
          a.tableName === record.tableName) {
        a.checked = marker;
      }
    });

    return artifacts;
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
        mergeArtifactsWithSettings(xaxis, get(model, 'sqlBuilder.dataFields.[0]'), 'x');
        mergeArtifactsWithSettings(yaxis, get(model, 'sqlBuilder.dataFields.[1]'), 'y');
        mergeArtifactsWithSettings(zaxis, get(model, 'sqlBuilder.dataFields.[2]'), 'z');
        mergeArtifactsWithSettings(groupBy, get(model, 'sqlBuilder.splitBy'), 'g');
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
        mergeArtifactsWithSettings(xaxis, get(model, 'sqlBuilder.groupBy'), 'x');
        mergeArtifactsWithSettings(yaxis, get(model, 'sqlBuilder.dataFields.[0]'), 'y');
        mergeArtifactsWithSettings(groupBy, get(model, 'sqlBuilder.splitBy'), 'g');
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

    LEGEND_POSITIONING,
    LAYOUT_POSITIONS
  };

}
