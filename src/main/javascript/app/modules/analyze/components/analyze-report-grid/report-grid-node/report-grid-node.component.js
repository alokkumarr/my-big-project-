import template from './report-grid-node.component.html';
import style from './report-grid-node.component.scss';
import {LAYOUT_MODE} from '../report-grid-container/report-grid-container.component';

export const ReportGridNodeComponent = {
  template,
  style: [style],
  bindings: {
    data: '<',
    layoutMode: '<',
    columns: '<'
  },
  controller: class ReportGridNodeController {
    constructor() {
      this.LAYOUT_MODE = LAYOUT_MODE;
    }
  }
};
