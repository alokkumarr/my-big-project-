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

export const AnalyzeGenerateQuery = {
  method: 'POST',
  url: '/api/analyze/generateQuery',
  response: () => {
    return [200, generateQuery()];
  }
};

export const AnalyzeSaveReport = {
  method: 'POST',
  url: '/api/analyze/saveReport',
  response: () => {
    return [200, saverReport()];
  }
};

export const Analyses = {
  method: 'GET',
  url: 'api/analyze/analyses',
  response: () => {
    return [200, getAnalyses()];
  }
};

function getAnalyses() {
  return [
    {
      type: 'chart',
      name: 'Order Revenue By Customer',
      metrics: ['Orders', 'Revenue'],
      scheduled: 'Every Friday at 12:00pm',
      chart: {
        options: getTransactionVolumeChartOptions()
      }
    },
    {
      type: 'report',
      name: 'Shipper Usage',
      metrics: ['Orders'],
      scheduled: 'Daily',
      report: {
        options: {
          dataSource: [{
            id: 1,
            shipper: 'Aaron\'s Towing',
            order: '12bc',
            total: '$600'
          }, {
            id: 2,
            shipper: 'Aaron\'s Towing',
            order: '12bd',
            total: '$650'
          }, {
            id: 3,
            shipper: 'Aaron\'s Towing',
            order: '12be',
            total: '$550'
          }, {
            id: 4,
            shipper: 'Aaron\'s Towing',
            order: '12bf',
            total: '$700'
          }],
          columns: ['shipper', 'order', 'total'],
          columnAutoWidth: true,
          showBorders: true,
          showColumnHeaders: true,
          showColumnLines: true,
          showRowLines: true,
          width: 400,
          scrolling: {
            mode: 'virtual'
          },
          sorting: {
            mode: 'none'
          },
          paging: {
            pageSize: 10
          },
          pager: {
            showPageSizeSelector: true,
            showInfo: true
          }
        }
      }
    }
  ];
}

function getTransactionVolumeChartData() {
  return [{
    name: 'Alpha',
    data: [
      [0.3, 5],
      [2.1, 25],
      [3.5, 10],
      [4.5, 11],
      [5.6, 6],
      [6.5, 21],
      [7.1, 20],
      [7.8, 29],
      [8.7, 35],
      [9, 29],
      [9.5, 5],
      [11.1, 20]
    ]
  }, {
    name: 'Bravo',
    data: [
      [0.3, 2],
      [4.8, 13],
      [6.2, 35],
      [8.9, 10],
      [10.6, 22],
      [11.1, 10]
    ]
  }];
}
function getTransactionVolumeChartOptions() {
  return {
    xAxis: {
      categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
      startOnTick: true,
      title: {
        text: 'Months'
      }
    },
    yAxis: {
      title: {
        text: 'Revenue'
      }
    },
    legend: {
      align: 'right',
      verticalAlign: 'top',
      layout: 'vertical',
      x: 0,
      y: 100
    },
    chart: {
      type: 'line',
      marginRight: 120
    },
    plotOptions: {
      line: {
        pointPlacement: -0.5
      }
    },
    series: getTransactionVolumeChartData()
  };
}

function getMenu() {
  return [
    {
      name: 'My Analyses',
      children: [{
        id: 1,
        name: 'Order Fulfillment',
        url: 'analyze/1'
      }, {
        id: 2,
        name: 'Category 2',
        url: 'analyze/2'
      }, {
        id: 3,
        name: 'Category 3',
        url: 'analyze/3'
      }, {
        id: 4,
        name: 'Category 4',
        url: 'analyze/4'
      }, {
        id: 5,
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

  /* eslint-disable camelcase */
  artifacts.push({
    artifact_name: 'Orders',
    artifact_position: [5, 130],
    artifact_attributes: [
      {
        display_name: 'Order ID',
        column_name: 'OrderID',
        alias_name: '',
        type: 'int',
        hide: true,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Shipper',
        column_name: 'Shipper',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Customer',
        column_name: 'Customer',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Total Price',
        column_name: 'TotalPrice',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: false,
        filter_eligible: true,
        checked: false
      },
      {
        display_name: 'Warehouse',
        column_name: 'Warehouse',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Address',
        column_name: 'Address',
        alias_name: 'Order Address',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: false,
        checked: false
      }
    ],
    sql_builder: {
      group_by_columns: [],
      order_by_columns: [],
      joins: [
        {
          type: 'inner',
          criteria: [{
            table_name: 'Orders',
            column_name: 'Shipper',
            side: 'right'
          }, {
            table_name: 'Shippers',
            column_name: 'ShipperID',
            side: 'left'
          }]
        },
        {
          type: 'inner',
          criteria: [{
            table_name: 'Orders',
            column_name: 'Customer',
            side: 'right'
          }, {
            table_name: 'Customers',
            column_name: 'CustomerID',
            side: 'left'
          }]
        },
        {
          type: 'inner',
          criteria: [{
            table_name: 'Orders',
            column_name: 'Warehouse',
            side: 'right'
          }, {
            table_name: 'Warehouses',
            column_name: 'WarehouseID',
            side: 'left'
          }]
        }
      ],
      filters: [
        {
          column_name: 'TotalPrice',
          boolean_criteria: 'AND',
          operator: '>=',
          search_conditions: [1]
        }
      ]
    }
  });

  artifacts.push({
    artifact_name: 'Shippers',
    artifact_position: [400, 5],
    artifact_attributes: [
      {
        display_name: 'Shipper ID',
        column_name: 'ShipperID',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Shipper Name',
        column_name: 'ShipperName',
        alias_name: '',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: true,
        checked: false
      },
      {
        display_name: 'Region',
        column_name: 'Region',
        alias_name: 'Shipper Region',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: false,
        checked: false
      }
    ],
    sql_builder: {
      group_by_columns: [],
      order_by_columns: [],
      joins: []
    }
  });

  artifacts.push({
    artifact_name: 'Customers',
    artifact_position: [400, 200],
    artifact_attributes: [
      {
        display_name: 'Customer ID',
        column_name: 'CustomerID',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Customer Name',
        column_name: 'CustomerName',
        alias_name: '',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: true,
        checked: false
      },
      {
        display_name: 'Address',
        column_name: 'Address',
        alias_name: 'Customer Address',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Phone Number',
        column_name: 'PhoneNumber',
        alias_name: 'Customer Phone Number',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: false,
        checked: false
      }
    ],
    sql_builder: {
      group_by_columns: [],
      order_by_columns: [],
      joins: []
    }
  });

  artifacts.push({
    artifact_name: 'Warehouses',
    artifact_position: [350, 420],
    artifact_attributes: [
      {
        display_name: 'Warehouse ID',
        column_name: 'WarehouseID',
        alias_name: '',
        type: 'int',
        hide: false,
        join_eligible: true,
        filter_eligible: false,
        checked: false
      },
      {
        display_name: 'Warehouse Name',
        column_name: 'WarehouseName',
        alias_name: '',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: true,
        checked: false
      },
      {
        display_name: 'Warehouse Address',
        column_name: 'WarehouseAddress',
        alias_name: '',
        type: 'string',
        hide: false,
        join_eligible: false,
        filter_eligible: false,
        checked: false
      }
    ],
    sql_builder: {
      group_by_columns: [],
      order_by_columns: [],
      joins: []
    }
  });
  /* eslint-enable camelcase */

  return artifacts;
}

function getDataByQuery() {
  return [
    {
      CustomerName: 'Johnson\'s Trucking',
      TotalPrice: 100,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse1'
    },
    {
      CustomerName: 'Lily\'s Trucking',
      TotalPrice: 200,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse1'
    },
    {
      CustomerName: 'Advanced Autos',
      TotalPrice: 400,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse2'
    },
    {
      CustomerName: 'Advanced Autos',
      TotalPrice: 100,
      ShipperName: 'Granger',
      WarehouseName: 'Warehouse1'
    },
    {
      CustomerName: 'Import Tuners',
      TotalPrice: 600,
      ShipperName: 'Granger',
      WarehouseName: 'Warehouse1'
    },
    {
      CustomerName: 'Johnson\'s Trucking',
      TotalPrice: 700,
      ShipperName: 'Granger',
      WarehouseName: 'Warehouse2'
    },
    {
      CustomerName: 'East Side Auto',
      TotalPrice: 400,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse2'
    },
    {
      CustomerName: 'North Raven Auto',
      TotalPrice: 800,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse1'
    },
    {
      CustomerName: 'Bloomburg Auto Shop',
      TotalPrice: 300,
      ShipperName: 'Motion Inc.',
      WarehouseName: 'Warehouse5'
    }
  ];
}

function generateQuery() {
  return {
    query: 'SELECT ORDER_NAME FROM ORDERS'
  };
}

function saverReport() {
  return {
    id: 10
  };
}
