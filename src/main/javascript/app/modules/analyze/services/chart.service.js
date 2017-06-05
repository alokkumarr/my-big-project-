import get from 'lodash/get';
import set from 'lodash/set';
import uniq from 'lodash/uniq';
import reduce from 'lodash/reduce';
import keys from 'lodash/keys';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';

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
        type: type || 'column',
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

  const gridToChart = (x, y, g, grid) => {
    const defaultSeriesName = x ? x.displayName : 'Series 1';
    const categories = uniq(grid.map(row => row[x.columnName]));

    const defaultSeries = () => reduce(categories, (obj, c) => {
      obj[c] = 0;
      return obj;
    }, {});

    const res = reduce(grid, (obj, row) => {
      const category = row[x.columnName];
      const series = row[g.columnName] || defaultSeriesName;
      obj[series] = obj[series] || defaultSeries();
      obj[series][category] += row[y.columnName];
      return obj;
    }, {});

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

  /* Functions to convert grid data to chart options forEach
     various chart types */
  const dataCustomizer = {
    column: gridToChart,
    line: gridToChart,
    spline: gridToChart,
    stack: gridToChart,
    pie: gridToPie,
    donut: gridToPie
  };

  const dataToChangeConfig = (type, settings, gridData, opts) => {
    const xaxis = filter(settings.xaxis, attr => attr.checked)[0] || {};
    const yaxis = filter(settings.yaxis, attr => attr.checked)[0] || {};
    const group = filter(settings.groupBy, attr => attr.checked)[0] || {};

    const changes = dataCustomizer[type](xaxis, yaxis, group, gridData);
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

  return {
    getChartConfigFor,
    dataToChangeConfig,

    LEGEND_POSITIONING,
    LAYOUT_POSITIONS
  };

}
