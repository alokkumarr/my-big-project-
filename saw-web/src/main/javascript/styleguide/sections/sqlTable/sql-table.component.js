import template from './sql-table.component.html';

export const SqlTableComponent = {
  template,
  controller: class SqlTableController {
    constructor() {
      this.sqlTable = {
        tables: [{
          name: 'Customers',
          fields: [{
            name: 'ID',
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
            name: 'Name',
            type: 'string',
            checked: true
          }, {
            name: 'Address',
            type: 'string',
            checked: false
          }, {
            name: 'Phone',
            type: 'string',
            checked: false
          }],
          x: 4,
          y: 4
        }, {
          name: 'Orders',
          fields: [{
            name: 'ID',
            type: 'int',
            checked: false
          }, {
            name: 'Shipper',
            type: 'string',
            checked: true
          }, {
            name: 'Total',
            type: 'int',
            checked: false
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
          }],
          x: 346,
          y: 11
        }],
        settings: {
          endpoints: {
            source: {
              endpoint: 'Dot',
              isSource: true,
              isTarget: true,
              maxConnections: -1,
              connector: ['Flowchart', {
                cornerRadius: 10
              }],
              endpointStyle: {
                radius: 9,
                stroke: '#B0BFC8',
                strokeWidth: 3
              },
              connectorStyle: {
                stroke: '#B0BFC8',
                strokeWidth: 3,
                outlineStroke: 'white',
                outlineWidth: 2
              },
              connectorHoverStyle: {
                stroke: '#B0BFC8'
              },
              endpointHoverStyle: {
                stroke: '#B0BFC8'
              }
            },
            target: {
              endpoint: 'Dot',
              isTarget: true,
              maxConnections: -1,
              endpointStyle: {
                radius: 9,
                stroke: '#B0BFC8',
                strokeWidth: 3
              },
              endpointHoverStyle: {
                stroke: '#B0BFC8'
              }
            }
          }
        }
      };
    }
  }
};
