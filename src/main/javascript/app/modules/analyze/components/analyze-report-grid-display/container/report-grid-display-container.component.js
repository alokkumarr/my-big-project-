import groupBy from 'lodash/groupBy';
import map from 'lodash/map';
import omit from 'lodash/fp/omit';
import forEach from 'lodash/forEach';
import find from 'lodash/find';

import template from './report-grid-display-container.component.html';
import style from './report-grid-display-container.component.scss';

export const LAYOUT_MODE = {
  DETAIL: 'detail',
  SUMMARY: 'summary'
};

export const ReportGridDisplayContainerComponent = {
  template,
  bindings: {
    data: '<',
    columns: '<',
    groups: '<'
  },
  styles: [style],
  controller: class ReportGridDisplayContainerController {
    constructor() {
      'ngInject';
      this.LAYOUT_MODE = LAYOUT_MODE;
    }

    $onInit() {
      const groupLabels = this.getGroupLabels(this.groups, this.columns);
      this.groupedData = this.groupData(this.data, this.groups);
      this.groupedByString = groupLabels.join(', ');
      this.settings = {
        layoutMode: LAYOUT_MODE.DETAIL
      };
    }

    getGroupLabels(groups, columns) {
      return map(groups, group => {
        const targetColumns = find(columns, column => column.columnName === group);
        return targetColumns.label;
      });
    }

    groupData(data, groups) {
      let groupedData = data;
      forEach(groups, group => {
        groupedData = this.groupRecursive(groupedData, group);
      });
      return groupedData;
    }

    groupArray(array, columnName) {
      const groupedObj = groupBy(array, columnName);
      const nodes = map(groupedObj, (val, key) => {
        return {
          groupValue: key,
          itemCount: val.length,
          data: map(val, omit(columnName))
        };
      });

      return {
        isGroup: true,
        groupBy: columnName,
        nodes
      };
    }

    groupRecursive(data, columnName) {
      let groupedData = data;
      if (data.isGroup) {
        data.nodes = forEach(data.nodes, node => {
          node.data = this.groupRecursive(node.data, columnName);
        });
      } else {
        groupedData = this.groupArray(data, columnName);
      }
      return groupedData;
    }
  }
};
