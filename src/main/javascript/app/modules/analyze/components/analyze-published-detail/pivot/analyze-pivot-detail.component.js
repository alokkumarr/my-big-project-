import 'devextreme/ui/pivot_grid';

import template from './analyze-pivot-detail.component.html';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor() {
      const gridData = [
        {
          id: 1,
          region: 'North America',
          country: 'USA',
          city: 'New York',
          amount: 1740,
          date: '2013/01/06'
        },
        {
          id: 2,
          region: 'North America',
          country: 'USA',
          city: 'Los Angeles',
          amount: 850,
          date: '2013/01/13'
        },
        {
          id: 3,
          region: 'North America',
          country: 'USA',
          city: 'Denver',
          amount: 2235,
          date: '2013/01/07'
        },
        {
          id: 4,
          region: 'North America',
          country: 'CAN',
          city: 'Vancouver',
          amount: 1965,
          date: '2013/01/03'
        },
        {
          id: 5,
          region: 'North America',
          country: 'CAN',
          city: 'Edmonton',
          amount: 880,
          date: '2013/01/10'
        },
        {
          id: 6,
          region: 'South America',
          country: 'BRA',
          city: 'Rio de Janeiro',
          amount: 5260,
          date: '2013/01/17'
        },
        {
          id: 7,
          region: 'South America',
          country: 'ARG',
          city: 'Buenos Aires',
          amount: 2790,
          date: '2013/01/21'
        }
      ];
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
          store: gridData
        }
      };
    }
  }
};
