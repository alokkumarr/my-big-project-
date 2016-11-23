import R from 'ramda';

export const analysisMethods = {
  method: 'GET',
  url: '/api/analyze/methods',
  response: () => {
    return [200, getAnalysisMethods()];
  }
};

export const metrics = {
  method: 'GET',
  url: '/api/analyze/metrics',
  response: () => {
    return [200, getMetrics()];
  }
};

function getAnalysisMethods() {
  return [
    {
      label: 'TABLES',
      category: 'table',
      children: [{
        label: 'Report',
        icon: 'icon-report',
        type: 'table:report'
      }, {
        label: 'Pivot',
        icon: 'icon-pivot',
        type: 'table:pivot'
      }]
    }, {
      label: 'BAR CHARTS',
      category: 'bar-chart',
      children: [{
        label: 'Bar Chart',
        icon: 'icon-hor-bar-chart',
        type: 'bar-chart:simple'
      }, {
        label: 'Stacked Bar Chart',
        icon: 'icon-hor-bar-chart',
        type: 'bar-chart:stacked'
      }, {
        label: 'Bar Chart variation',
        icon: 'icon-hor-bar-chart',
        type: 'bar-chart:variation'
      }]
    }, {
      label: 'COLUMN CHARTS',
      category: 'column-chart',
      children: [{
        label: 'Column Chart',
        icon: 'icon-vert-bar-chart',
        type: 'column-chart:simple'
      }, {
        label: 'Column Chart Var',
        icon: 'icon-vert-bar-chart',
        type: 'column-chart:var'
      }]
    }
  ];
}

function getMetrics() {
  const metrics = R.range(1, 17).map(key => {
    return {
      name: `Metric ${key}`,
      checked: false,
      disabled: false,
      supports: R.clone(getAnalysisMethods())
    };
  });

  metrics[0].name = 'Metric a 1';
  metrics[0].supports = [
    {
      category: 'table',
      children: [{
        type: 'table:pivot'
      }]
    }
  ];

  metrics[1].name = 'Metric b 2';
  metrics[1].supports = [
    {
      category: 'bar-chart',
      children: [{
        type: 'bar-chart:simple'
      }, {
        type: 'bar-chart:variation'
      }]
    }
  ];

  metrics[2].name = 'Metric c 3';
  metrics[2].supports = [
    {
      category: 'column-chart',
      children: [{
        type: 'column-chart:simple'
      }]
    }
  ];

  metrics[3].name = 'Metric ac 4';
  metrics[3].supports = [
    {
      category: 'table',
      children: [{
        type: 'table:pivot'
      }]
    }, {
      category: 'column-chart',
      children: [{
        type: 'column-chart:simple'
      }]
    }
  ];

  return metrics;
}
