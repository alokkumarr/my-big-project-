import 'devextreme/ui/pivot_grid';
import isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs';

import template from './analyze-pivot-detail.component.html';
import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter-sidenav/analyze-filter-sidenav.component';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor(FilterService, PivotService) {
      'ngInject';
      this._isEmpty = isEmpty;
      this._PivotService = PivotService;
      this._FilterService = FilterService;
      this.pivotGridUpdater = new BehaviorSubject({});
    }

    $onInit() {
      const pivot = this.analysis.pivot;
      const artifactAttributes = pivot.artifacts[0].artifactAttributes;

      this.fields = this._PivotService.getBackend2FrontendFieldMapper()(artifactAttributes);
      this.deNormalizedData = this._PivotService.denormalizeData(pivot.data, this.fields);
      this.filters = this._PivotService.mapFieldsToFilters(pivot.data, this.fields);

      this.pivotGridUpdater.next({
        dataSource: {
          store: this.deNormalizedData,
          fields: this.fields
        }
      });
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
