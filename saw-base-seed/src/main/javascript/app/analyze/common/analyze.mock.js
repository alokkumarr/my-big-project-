import range from 'lodash/range';
import cloneDeep from 'lodash/cloneDeep';

export const AnalyzeMenu = {
  method: 'GET',
  url: '/api/menu/analyze',
  response: () => {
    return [200, getMenu()];
  }
};

export const AnalyzeMethods = {
  method: 'GET',
  url: '/api/analyze/methods',
  response: () => {
    return [200, getMethods()];
  }
};

export const AnalyzeMetrics = {
  method: 'GET',
  url: '/api/analyze/metrics',
  response: () => {
    return [200, getMetrics()];
  }
};

export const AnalyzeTables = {
  method: 'GET',
  url: '/api/analyze/tables',
  response: () => {
    return [200, getTables()];
  }
};

export const AnalyzeDataByQuery = {
  method: 'GET',
  url: '/api/analyze/dataByQuery',
  response: () => {
    return [200, getDataByQuery()];
  }
};

function getMenu() {
  return [
    {
      name: 'My Analyses',
      children: [{
        name: 'Order Fulfillment',
        url: '#/analyze/1'
      }, {
        name: 'Category 2',
        url: '#/analyze/2'
      }, {
        name: 'Category 3',
        url: '#/analyze/3'
      }, {
        name: 'Category 4',
        url: '#/analyze/4'
      }, {
        name: 'Category 5',
        url: '#/analyze/5'
      }]
    }, {
      name: 'Folder 2'
    }, {
      name: 'Folder 3'
    }];
}

function getMethods() {
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
  const metrics = range(1, 17).map(key => {
    return {
      name: `Metric ${key}`,
      checked: false,
      disabled: false,
      supports: cloneDeep(getMethods())
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

function getTables() {
  return [
    {
      name: 'Customers',
      fields: [{
        name: 'Customer ID',
        type: 'int',
        checked: false,
        endpoints: [{
          uuid: 11,
          anchor: 'RightMiddle',
          connections: [{
            target: 24
          }]
        }]
      }, {
        name: 'Customer Name',
        type: 'string',
        checked: true
      }, {
        name: 'Address',
        type: 'string',
        checked: false
      }, {
        name: 'Phone Number',
        type: 'string',
        checked: false
      }],
      x: 5,
      y: 5
    }, {
      name: 'Orders',
      fields: [{
        name: 'Order ID',
        type: 'int',
        checked: false
      }, {
        name: 'Shipper',
        type: 'string',
        checked: false,
        endpoints: [{
          uuid: 54,
          anchor: 'RightMiddle',
          connections: [{
            source: 54
          }]
        }]
      }, {
        name: 'Customer',
        type: 'string',
        checked: false,
        endpoints: [{
          uuid: 24,
          anchor: 'LeftMiddle',
          connections: [{
            source: 24
          }]
        }]
      }, {
        name: 'Total Price',
        type: 'int',
        checked: true
      }, {
        name: 'Warehouse',
        type: 'string',
        checked: false,
        endpoints: [{
          uuid: 84,
          anchor: 'RightMiddle',
          connections: [{
            source: 84
          }]
        }]
      }, {
        name: 'Address',
        type: 'string',
        checked: false
      }],
      x: 275,
      y: 5
    }, {
      name: 'Shippers',
      fields: [{
        name: 'Shipper ID',
        type: 'int',
        checked: false,
        endpoints: [{
          uuid: 33,
          anchor: 'LeftMiddle',
          connections: [{
            target: 54
          }]
        }]
      }, {
        name: 'Shipper Name',
        type: 'string',
        checked: true
      }, {
        name: 'Region',
        type: 'string',
        checked: false
      }],
      x: 525,
      y: 5
    }, {
      name: 'Warehouses',
      fields: [{
        name: 'Warehouse ID',
        type: 'int',
        checked: false,
        endpoints: [{
          uuid: 55,
          anchor: 'LeftMiddle',
          connections: [{
            target: 84
          }]
        }]
      }, {
        name: 'Warehouse Name',
        type: 'string',
        checked: true
      }, {
        name: 'Warehouse Address',
        type: 'string',
        checked: false
      }],
      x: 525,
      y: 200
    }];
}

function getDataByQuery() {
  return [
    {
      customerName: 'Johnson\'s Trucking',
      price: '$100',
      name: 'Motion Inc.'
    },
    {
      customerName: 'Lily\'s Trucking',
      price: '$200',
      name: 'Motion Inc.'
    },
    {
      customerName: 'Advanced Autos',
      price: '$400',
      name: 'Motion Inc.'
    },
    {
      customerName: 'Advanced Autos',
      price: '$100',
      name: 'Granger'
    },
    {
      customerName: 'Import Tuners',
      price: '$600',
      name: 'Granger'
    },
    {
      customerName: 'Johnson\'s Trucking',
      price: '$700',
      name: 'Granger'
    },
    {
      customerName: 'East Side Auto',
      price: '$400',
      name: 'Motion Inc.'
    },
    {
      customerName: 'North Raven Auto',
      price: '$800',
      name: 'Motion Inc.'
    },
    {
      customerName: 'Bloomburg Auto Shop',
      price: '$300',
      name: 'Motion Inc.'
    }
  ];
}
