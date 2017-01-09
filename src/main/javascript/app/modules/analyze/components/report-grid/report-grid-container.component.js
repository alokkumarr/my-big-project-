import template from './report-grid-container.component.html';

export const LAYOUT_MODE = {
  DETAIL: 'detail',
  SUMMARY: 'summary'
}
export const ReportGridContainerComponent = {
  template,
  bindings: {
    data: '<'
  },
  controller: class ReportGridContainerController {
    constructor(ReportGridService) {
      this._ReportGridService = ReportGridService;
      this.LAYOUT_MODE = LAYOUT_MODE;
      this.layoutMode = LAYOUT_MODE.DETAIL;
    }

    $onInit() {
      this.modifiedData = this.data;
    }

    groupData(columnName) {
      this.modifiedData = this._ReportGridService.group(this.modifiedData, columnName);
    }

    undoGrouping() {
      this.modifiedData = this.data;
    }
  }
};
