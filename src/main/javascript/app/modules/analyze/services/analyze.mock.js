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

export const AnalyzeArtifacts = {
  method: 'GET',
  url: '/api/analyze/artifacts',
  response: () => {
    return [200, getArtifacts()];
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
        url: 'analyze/1'
      }, {
        name: 'Category 2',
        url: 'analyze/2'
      }, {
        name: 'Category 3',
        url: 'analyze/3'
      }, {
        name: 'Category 4',
        url: 'analyze/4'
      }, {
        name: 'Category 5',
        url: 'analyze/5'
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
      name: 'Orders',
      fields: [
        {
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
              source: 54,
              type: 'many'
            }]
          }]
        }, {
          name: 'Customer',
          type: 'string',
          checked: false,
          endpoints: [{
            uuid: 24,
            anchor: 'RightMiddle',
            connections: [{
              source: 24,
              type: 'many'
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
              source: 84,
              type: 'many'
            }]
          }]
        }, {
          name: 'Address',
          type: 'string',
          checked: false
        }],
      x: 5,
      y: 130
    }, {
      name: 'Shippers',
      fields: [
        {
          name: 'Shipper ID',
          type: 'int',
          checked: false,
          endpoints: [{
            uuid: 33,
            anchor: 'LeftMiddle',
            connections: [{
              target: 54,
              type: 'one'
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
      x: 400,
      y: 5
    }, {
      name: 'Customers',
      fields: [
        {
          name: 'Customer ID',
          type: 'int',
          checked: false,
          endpoints: [{
            uuid: 11,
            anchor: 'LeftMiddle',
            connections: [{
              target: 24,
              type: 'one'
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
      x: 400,
      y: 200
    }, {
      name: 'Warehouses',
      fields: [
        {
          name: 'Warehouse ID',
          type: 'int',
          checked: false,
          endpoints: [{
            uuid: 55,
            anchor: 'LeftMiddle',
            connections: [{
              target: 84,
              type: 'one'
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
      x: 350,
      y: 420
    }];
}

function getArtifacts() {
  const artifacts = [];

  artifacts.push({
    _artifact_name: 'Orders',
    _artifact_attributes: [
      {
        _display_name: 'Order ID',
        '_actual_col-name': 'OrderID',
        _alias_name: '',
        _type: 'int',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Shipper',
        '_actual_col-name': 'Shipper',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Customer',
        '_actual_col-name': 'Customer',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Total Price',
        '_actual_col-name': 'TotalPrice',
        _alias_name: '',
        _type: 'int',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Warehouse',
        '_actual_col-name': 'Warehouse',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Address',
        '_actual_col-name': 'Address',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      }
    ],
    _artifact_position: [5, 130],
    _sql_builder: {
      _group_by_columns: [],
      _order_by_columns: [{
        'col-name': 'OrderID',
        order: 'asc'
      }],
      joins: [
        {
          type: 'inner',
          criteria: [{
            'table-name': 'Orders',
            'column-name': 'Shipper',
            side: 'right'
          }, {
            'table-name': 'Shippers',
            'column-name': 'ShipperID',
            side: 'left'
          }]
        },
        {
          type: 'inner',
          criteria: [{
            'table-name': 'Orders',
            'column-name': 'Customer',
            side: 'right'
          }, {
            'table-name': 'Customers',
            'column-name': 'CustomerID',
            side: 'left'
          }]
        },
        {
          type: 'inner',
          criteria: [{
            'table-name': 'Orders',
            'column-name': 'Warehouse',
            side: 'right'
          }, {
            'table-name': 'Warehouses',
            'column-name': 'WarehouseID',
            side: 'left'
          }]
        }
      ]
    }
  });

  artifacts.push({
    _artifact_name: 'Shippers',
    _artifact_attributes: [
      {
        _display_name: 'Shipper ID',
        '_actual_col-name': 'ShipperID',
        _alias_name: '',
        _type: 'int',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Shipper Name',
        '_actual_col-name': 'ShipperName',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Region',
        '_actual_col-name': 'Region',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      }
    ],
    _artifact_position: [400, 5],
    _sql_builder: {
      _group_by_columns: [],
      _order_by_columns: [{
        'col-name': 'ShipperID',
        order: 'asc'
      }],
      joins: []
    }
  });

  artifacts.push({
    _artifact_name: 'Customers',
    _artifact_attributes: [
      {
        _display_name: 'Customer ID',
        '_actual_col-name': 'CustomerID',
        _alias_name: '',
        _type: 'int',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Customer Name',
        '_actual_col-name': 'CustomerName',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Address',
        '_actual_col-name': 'Address',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Phone Number',
        '_actual_col-name': 'PhoneNumber',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      }
    ],
    _artifact_position: [400, 200],
    _sql_builder: {
      _group_by_columns: [],
      _order_by_columns: [{
        'col-name': 'CustomerID',
        order: 'asc'
      }],
      joins: []
    }
  });

  artifacts.push({
    _artifact_name: 'Warehouses',
    _artifact_attributes: [
      {
        _display_name: 'Warehouse ID',
        '_actual_col-name': 'WarehouseID',
        _alias_name: '',
        _type: 'int',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Warehouse Name',
        '_actual_col-name': 'WarehouseName',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      },
      {
        _display_name: 'Warehouse Address',
        '_actual_col-name': 'WarehouseAddress',
        _alias_name: '',
        _type: 'string',
        _hide: false,
        _join_eligible: false
      }
    ],
    _artifact_position: [350, 420],
    _sql_builder: {
      _group_by_columns: [],
      _order_by_columns: [{
        'col-name': 'WarehouseID',
        order: 'asc'
      }],
      joins: []
    }
  });

  return artifacts;
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
