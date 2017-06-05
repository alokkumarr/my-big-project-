import 'devextreme/ui/pivot_grid';
import isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-pivot-detail.component.html';

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
      this.filters = [];
    }

    $onInit() {

      this.requester.subscribe(requests => this.request(requests));

      const pivot = this.analysis.pivot;
      const artifactAttributes = pivot.artifacts[0].columns;

      this.fields = this._PivotService.getBackend2FrontendFieldMapper()(artifactAttributes);
      this.deNormalizedData = this._PivotService.denormalizeData(pivot.data, this.fields);
      this.filters = map(pivot.filters, this._FilterService.backend2FrontendFilter(pivot.artifacts));
      // TODO runtime filters in SAW-634

      this.openFilterSidenav();

      this.pivotGridUpdater.next({
        dataSource: {
          store: this.deNormalizedData,
          fields: this.fields
        }
      });
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
  }
};
