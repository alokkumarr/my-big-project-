import template from './snapshotkpi.component.html';

export const SnapshotKpiComponent = {
  template,
  controller: class SnapshotKpiController {
    constructor() {
      this.snapshotBarChartData = {
        Jane: [2, 2, 3, 7, 1]
      };
    }
  }
};
