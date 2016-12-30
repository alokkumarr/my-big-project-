export const AnalyzeArtifacts = {
  method: 'GET',
  url: '/api/analyze/reportArtifacts',
  response: () => {
    return [200, getArtifacts()];
  }
};

/* eslint-disable */
function getArtifacts() {
  return [{
    x: 5,
    y: 5,
    _artifact_name: 'Customers',
    _sql_builder: {
      query: '',
      _group_by_columns: [],
      _order_by_columns: [],
      // _order_by_columns : [{'col-name' : '', order : 'asc'},{col-name : '', order : 'desc'}],
      joins: [{
        type: 'right', // right | left | inner
        criteria: {
          'table-name': '',
          'column-name': '',
          side: 'left'
        }
      }]
    },
    _artifact_attributes: [{
      _display_name: 'Customer id', // This name should be used on the UI
      '_actual_col-name': 'CUSTOMER_ID', // This name is the actual column name
      _alias_name: 'Customer id', // This will support the rename feature,
      _hide: false,
      _join_eligible: true,
      _type: 'int', // 'string | int | double' This will define the type of the column which help to identify what type of column is that
      checked: true,
      endpoints: [{
        uuid: 11,
        anchor: 'RightMiddle',
        connections: [{
          target: 24
        }]
      }]
    }, {
      _display_name: 'Customer name',
      '_actual_col-name': 'CUSTOMER_NAME',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string'
    }, {
      _display_name: 'Customer address',
      '_actual_col-name': 'CUSTOMER_ADDRESS',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string'
    }, {
      _display_name: 'Customer phone number',
      '_actual_col-name': 'CUSTOMER_PHONE_NUMBER',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string'
    }]
  }, {
    x: 275,
    y: 5,
    _artifact_name: 'Orders',
    _sql_builder: {
      query: '',
      _group_by_columns: [],
      _order_by_columns: [],
      joins: [{
        type: 'right', // right | left | inner
        criteria: {
          'table-name': '',
          'column-name': '',
          side: 'left'
        }
      }]
    },
    _artifact_attributes: [{
      _display_name: 'Order id', // This name should be used on the UI
      '_actual_col-name': 'ORDER_ID', // This name is the actual column name
      _alias_name: '', // This will support the rename feature,
      _hide: false,
      _join_eligible: true,
      _type: 'int' // 'string | int | double' This will define the type of the column which help to identify what type of column is that
    }, {
      _display_name: 'Customer',
     '_actual_col-name': 'CUSTOMER',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'int',
      checked: true,
      endpoints: [{
        uuid: 24,
        anchor: 'LeftMiddle',
        connections: [{
          source: 24
        }]
      }]
    }, {
      _display_name: 'Shipper',
      '_actual_col-name': 'SHIPPER',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'int',
      checked: true,
      endpoints: [{
        uuid: 54,
        anchor: 'RightMiddle',
        connections: [{
          source: 54
        }]
      }]
    }, {
      _display_name: 'Warehouse',
      '_actual_col-name': 'WAREHOUSE',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'int',
      checked: true,
      endpoints: [{
        uuid: 84,
        anchor: 'RightMiddle',
        connections: [{
          source: 84
        }]
      }]
    }, {
      _display_name: 'Address',
      '_actual_col-name': 'ADDRESS',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string'
    }]
  }, {
    x: 525,
    y: 5,
    _artifact_name: 'Shippers',
    _sql_builder: {
      query: '',
      _group_by_columns: [],
      _order_by_columns: [],
      'joins': [{
        'type': 'right', // right | left | inner
        'criteria': {
          'table-name': '',
          'column-name': '',
          'side': 'left'
        }
      }]
    },
    _artifact_attributes: [{
      _display_name: 'Shipper id', // This name should be used on the UI
      '_actual_col-name': 'SHIPPER_ID', // This name is the actual column name
      _alias_name: '', // This will support the rename feature,
      _hide: false,
      _join_eligible: true,
      _type: 'int', // 'string | int | double' This will define the type of the column which help to identify what type of column is that
      checked: true,
      endpoints: [{
        uuid: 33,
        anchor: 'LeftMiddle',
        connections: [{
          target: 54
        }]
      }]
    }, {
      _display_name: 'Shipper name',
      '_actual_col-name': 'SHIPPER_NAME',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string',
      checked: false
    }, {
      _display_name: 'Region',
      '_actual_col-name': 'REGION',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string',
      checked: false
    }]
  }, {
    x: 525,
    y: 200,
    _artifact_name: 'Warehouses',
    _sql_builder: {
      'query': '',
      _group_by_columns: [],
      _order_by_columns: [],
      joins: [{
        type: 'right', // right | left | inner
        criteria: {
          'table-name': '',
          'column-name': '',
          'side': 'left'
        }
      }]
    },
    _artifact_attributes: [{
      _display_name: 'Warehouse id', // This name should be used on the UI
      '_actual_col-name': 'WAREHOUSE_ID', // This name is the actual column name
      _alias_name: '', // This will support the rename feature,
      _hide: false,
      _join_eligible: true,
      _type: 'int', // 'string | int | double' This will define the type of the column which help to identify what type of column is that
      checked: true,
      endpoints: [{
        uuid: 55,
        anchor: 'LeftMiddle',
        connections: [{
          target: 84
        }]
      }]
    }, {
      _display_name: 'Warehouse name',
      '_actual_col-name': 'WAREHOUSE_NAME',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string',
      checked: false
    }, {
      _display_name: 'Warehouse address',
      '_actual_col-name': 'WAREHOUSE_ADDRESS',
      _alias_name: '',
      _hide: false,
      _join_eligible: true,
      _type: 'string',
      checked: false
    }]
  }];
  /* eslint-enable */
}
