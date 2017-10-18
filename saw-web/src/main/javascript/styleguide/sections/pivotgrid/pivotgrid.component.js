import 'angular-sanitize';
import 'devextreme/ui/pivot_grid';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';
import template from './pivotgrid.component.html';
import gridData1 from './gridData1.json';
import gridData2 from './gridData2.json';
import gridData3 from './gridData3.json';
import gridData4 from './gridData4.json';

export const PivotgridComponent = {
  template,
  controller: class PivotgridController {
    constructor() {
      this.pivotGridOptions1 = {
        allowSortingBySummary: true,
        allowSorting: true,
        allowFiltering: true,
        allowExpandAll: true,
        fieldChooser: {
          enabled: false
        },
        export: {
          enabled: true,
          fileName: 'Sales'
        },
        dataSource: {
          fields: [{
            caption: 'Region',
            width: 120,
            dataField: 'region',
            area: 'row'
          }, {
            caption: 'City',
            dataField: 'city',
            width: 150,
            area: 'row',
            selector(data) {
              return `${data.city} (${data.country})`;
            }
          }, {
            dataField: 'date',
            dataType: 'date',
            area: 'column'
          }, {
            caption: 'Sales',
            dataField: 'amount',
            dataType: 'number',
            summaryType: 'sum',
            format: 'currency',
            area: 'data'
          }],
          store: gridData1
        }
      };

      this.pivotGridOptions2 = {
        allowSortingBySummary: true,
        allowSorting: true,
        allowFiltering: true,
        allowExpandAll: true,
        fieldChooser: {
          enabled: false
        },
        dataSource: {
          fields: [{
            caption: 'Shipper',
            width: 120,
            dataField: 'shipper',
            area: 'row'
          }, {
            caption: 'Region',
            dataField: 'region',
            width: 150,
            area: 'row'
          }, {
            dataField: 'product',
            area: 'column'
          }, {
            dataField: 'manufacturer',
            area: 'column'
          }, {
            caption: 'Sales',
            dataField: 'sum',
            dataType: 'number',
            summaryType: 'sum',
            format: 'currency',
            area: 'data'
          }],
          store: gridData2
        }
      };

      this.pivotGridOptions3 = {
        allowSortingBySummary: true,
        allowSorting: true,
        allowFiltering: true,
        allowExpandAll: true,
        dataFieldArea: 'column',
        height: 440,
        fieldChooser: {
          enabled: true
        },
        hideEmptySummaryCells: true,
        export: {
          enabled: true,
          fileName: 'PivotGrid'
        },
        scrolling: {
          mode: 'virtual'
        },
        dataSource: {
          fields: [{
            area: 'row',
            caption: 'Region',
            dataField: 'REGION',
            expanded: true,
            width: 120
          }, {
            area: 'row',
            caption: 'State',
            dataField: 'STATE_NAME',
            width: 120,
            selector: data => {
              return `${data.STATE_NAME} (${data.SERVICE_STATE})`;
            }
          }, {
            area: 'row',
            caption: 'City',
            dataField: 'SERVICE_CITY',
            width: 120
          }, {
            area: 'column',
            dataField: 'PRODUCT_STATUS_DATE',
            dataType: 'date'
          }, {
            area: 'data',
            caption: 'Count',
            dataField: 'PRODUCT_CNT',
            dataType: 'number',
            summaryType: 'sum'
          }],
          store: gridData3.hits.hits.map(item => {
            return item._source;
          })
        }
      };

      this.pivotGridOptions4 = {
        allowSortingBySummary: true,
        allowSorting: true,
        allowFiltering: true,
        allowExpandAll: true,
        dataFieldArea: 'column',
        height: 400,
        fieldChooser: {
          enabled: true
        },
        hideEmptySummaryCells: true,
        export: {
          enabled: true,
          fileName: 'PivotGrid'
        },
        scrolling: {
          mode: 'virtual'
        },
        dataSource: {
          fields: [{
            area: 'row',
            caption: 'Company',
            dataField: 'company',
            expanded: true,
            width: 120
          }, {
            area: 'column',
            caption: 'Age',
            dataField: 'age',
            dataType: 'number',
            width: 120
          }, {
            area: 'row',
            caption: 'Gender',
            dataField: 'gender',
            width: 120,
            selector: data => {
              return data.gender === 'male' ? 'Male' : 'Female';
            }
          }, {
            area: 'data',
            caption: 'Balance',
            dataField: 'balance',
            dataType: 'number',
            format: 'currency',
            summaryType: 'sum'
          }],
          store: gridData4
        }
      };
    }
  }
};
