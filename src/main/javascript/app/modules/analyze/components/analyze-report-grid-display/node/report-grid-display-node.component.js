import template from './report-grid-display-node.component.html';
import style from './report-grid-display-node.component.scss';

import {LAYOUT_MODE} from '../container/report-grid-display-container.component';

export const ReportGridDisplayNodeComponent = {
  template,
  style: [style],
  bindings: {
    columns: '<',
    data: '<',
    settings: '<'
  },
  controller: class ReportGridDisplayNodeController {
    constructor() {
      this.LAYOUT_MODE = LAYOUT_MODE;
    }
  }
};
