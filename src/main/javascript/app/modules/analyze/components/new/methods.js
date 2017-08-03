const methods = [
  {
    label: 'TABLES',
    category: 'table',
    children: [
      {
        label: 'Report',
        icon: {font: 'icon-report'},
        type: 'table:report'
      },
      {
        label: 'Pivot',
        icon: {font: 'icon-pivot'},
        type: 'table:pivot'
      }
    ]
  },
  {
    label: 'CHARTS',
    category: 'charts',
    children: [
      {
        label: 'Column Chart',
        icon: {font: 'icon-vert-bar-chart'},
        type: 'chart:column'
      },
      {
        label: 'Bar Chart',
        icon: {font: 'icon-hor-bar-chart'},
        type: 'chart:bar'
      },
      {
        label: 'Stacked Chart',
        icon: {font: 'icon-vert-bar-chart'},
        type: 'chart:stack'
      },
      {
        label: 'Line Chart',
        icon: {font: 'icon-chart-line'},
        type: 'chart:line'
      },
      {
        label: 'Scatter Plot',
        icon: {font: 'icon-chart-scatter'},
        type: 'chart:scatter'
      },
      {
        label: 'Bubble Chart',
        icon: {font: 'icon-chart-bubble'},
        type: 'chart:bubble'
      }
    ]
  }
];

export default methods;
