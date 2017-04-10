import 'devextreme/ui/pivot_grid';
import isEmpty from 'lodash/isEmpty';

import template from './analyze-pivot-detail.component.html';
import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter-sidenav/analyze-filter-sidenav.component';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor(FilterService) {
      'ngInject';
      this._isEmpty = isEmpty;
      this._FilterService = FilterService;
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

    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
    }

    onApplyFilters(filters) {
      this.filters = filters;
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters)(this.analysis.pivot.data);
    }

    onClearAllFilters() {
      this.filters = this._FilterService.getFilterClearer()(this.filters);
      this.filteredGridData = this.analysis.pivot.data;
    }
  }
};
