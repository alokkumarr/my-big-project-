import 'devextreme/ui/pivot_grid';
import isEmpty from 'lodash/isEmpty';
import map from 'lodash/map';
import forEach from 'lodash/forEach';
import find from 'lodash/find';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-pivot-detail.component.html';
import {ANALYZE_FILTER_SIDENAV_IDS} from '../../analyze-filter/analyze-filter-sidenav.component';

export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor(FilterService, PivotService) {
      'ngInject';
      this._isEmpty = isEmpty;
      this._PivotService = PivotService;
      this._FilterService = FilterService;
      this.pivotGridUpdater = new BehaviorSubject({});
      this.filters = {};
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      this.requester.subscribe(requests => this.request(requests));

      const pivot = this.analysis.pivot;
      const artifactAttributes = pivot.artifacts[0].columns;

      this.fields = this._PivotService.getBackend2FrontendFieldMapper()(artifactAttributes);
      this.deNormalizedData = this._PivotService.denormalizeData(pivot.data, this.fields);
      this.filters.possible = this.getFilters(pivot.data, this.fields, pivot.filters);
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);

      this.openFilterSidenav();

      this.pivotGridUpdater.next({
        dataSource: {
          store: this.deNormalizedData,
          fields: this.fields
        }
      });
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

    request(requests) {
      /* eslint-disable no-unused-expressions */
      requests.export && this.onExport();
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this.pivotGridUpdater.next({
        export: true
      });
    }

    getFilters(data, fields, pivotFilters) {
      const filters = this._PivotService.mapFieldsToFilters(data, fields);
      const selectedFilters = map(pivotFilters, this._FilterService.getBackEnd2FrontEndFilterMapper());

      forEach(selectedFilters, selectedFilter => {
        const targetFilter = find(filters, ({name}) => name === selectedFilter.name);
        selectedFilter.items = targetFilter.items;
      });
      return selectedFilters;
    }

    openFilterSidenav() {
      if (!isEmpty(this.filters.possible)) {
        this._FilterService.openFilterSidenav(this.filters.possible, ANALYZE_FILTER_SIDENAV_IDS.detailPage);
      }
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.pivotGridUpdater.next({
        filters: this.filters.possible
      });
    }

    onClearAllFilters() {
      this.filters.possible = this._FilterService.getFilterClearer()(this.filters.possible);
      this.filters.selected = [];
      this.pivotGridUpdater.next({
        filters: this.filters.possible
      });
    }

    onFilterRemoved(filter) {
      filter.model = null;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.pivotGridUpdater.next({
        filters: this.filters.possible
      });
    }
  }
};
