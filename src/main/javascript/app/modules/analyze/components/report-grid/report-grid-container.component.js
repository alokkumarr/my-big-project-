import template from './report-grid-container.component.html';

export const ReportGridContainerComponent = {
  template,
  bindings: {
    data: '<'
  },
  controller: class ReportGridContainerController {
    constructor(ReportGridService) {
      this._ReportGridService = ReportGridService;
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
