import 'devextreme/ui/pivot_grid';

import template from './analyze-pivot-detail.component.html';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor() {
      this.pivotGridOptions1 = {
        allowSortingBySummary: false,
        allowSorting: false,
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
          store: this.analysis.pivot.data
        }
      };
    }
  }
};
