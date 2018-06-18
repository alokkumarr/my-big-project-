import 'devextreme/ui/pivot_grid';
import * as isEmpty from 'lodash/isEmpty';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import * as template from './analyze-pivot-detail.component.html';

let self;
export const AnalyzePivotDetailComponent = {
  template,
  bindings: {
    analysis: '<',
    requester: '<'
  },
  controller: class AnalyzePivotDetailController {
    constructor(FilterService, PivotService, $timeout) {
      'ngInject';
      this._isEmpty = isEmpty;
      this._PivotService = PivotService;
      this._FilterService = FilterService;
      this.pivotGridUpdater = new BehaviorSubject({});
      this.dataSource = {};
      this._$timeout = $timeout;
      self = this;
    }

    $onInit() {
      this.requester.subscribe(requests => this.onRequest(requests));
    }

    onRequest({data, exportAnalysis}) {
      /* eslint-disable no-unused-expressions */
      exportAnalysis && this.onExport();

      if (!data) {
        return;
      }

      this._$timeout(() => {
        self.data = this._PivotService.parseData(data, this.analysis.sqlBuilder);
      });
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this.pivotGridUpdater.next({
        export: true
      });
    }
  }
};
