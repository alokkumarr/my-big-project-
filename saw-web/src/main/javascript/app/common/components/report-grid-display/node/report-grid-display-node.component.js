import * as template from './report-grid-display-node.component.html';
import style from './report-grid-display-node.component.scss';

import {LAYOUT_MODE} from '../container/report-grid-display-container.component';

export const ReportGridDisplayNodeComponent = {
  template,
  style: [style],
  bindings: {
    columns: '<',
    data: '<',
    source: '&',
    settings: '<'
  },
  controller: class ReportGridDisplayNodeController {
    constructor() {
      this.LAYOUT_MODE = LAYOUT_MODE;
    }

    loadData(options) {
      console.log(this.data);
      return this.source({options});
    }
  }
};
