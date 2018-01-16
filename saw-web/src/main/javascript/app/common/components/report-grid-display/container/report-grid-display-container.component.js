import * as groupBy from 'lodash/groupBy';
import * as map from 'lodash/map';
import * as omit from 'lodash/fp/omit';
import * as forEach from 'lodash/forEach';
import * as keys from 'lodash/keys';
import * as find from 'lodash/find';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';

import * as template from './report-grid-display-container.component.html';
import style from './report-grid-display-container.component.scss';

export const LAYOUT_MODE = {
  DETAIL: 'detail',
  SUMMARY: 'summary'
};

export const ReportGridDisplayContainerComponent = {
  template,
  bindings: {
    data: '<',
    source: '&',
    columns: '<',
    groups: '<'
  },
  styles: [style],
  controller: class ReportGridDisplayContainerController {
    constructor(fileService) {
      'ngInject';
      this._fileService = fileService;
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

    loadData(options) {
      return this.source({options});
    }

    $onChanges() {
      if (!isUndefined(this.data)) {
        this.groupedData = this.groupData(this.data, this.groups);
      }
    }

    // formatDates(data) {
    //   const ks = keys(data[0] || {});
    //   const formats = [
    //     moment.ISO_8601,
    //     'MM/DD/YYYY  :)  HH*mm*ss'
    //   ];
    //   forEach(data, data => {
    //     forEach(ks, key => {
    //       if (moment(data[key], formats, true).isValid()) {
    //         data[key] = moment(data[key]).format('MM/DD/YYYY');
    //       }
    //     });
    //   });
    //   return data;
    // }

    getGroupLabels(groups, columns) {
      return map(groups, group => {
        const targetColumns = find(columns, column => column.columnName === group.columnName) || {};
        return targetColumns.aliasName || targetColumns.displayName;
      });
    }

    groupData(data, groups) {
      if (!data) {
        return [];
      }

      let groupedData = data;
      forEach(groups, group => {
        groupedData = this.groupRecursive(groupedData, group);
      });
      return groupedData;
    }

    groupArray(array, {columnName}) {
      const groupedObj = groupBy(array, columnName);
      const nodes = map(groupedObj, (val, key) => {
        return {
          groupValue: key,
          columnName,
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

    groupRecursive(data, group) {
      let groupedData = data;
      if (data.isGroup) {
        data.nodes = forEach(data.nodes, node => {
          node.data = this.groupRecursive(node.data, group);
        });
      } else {
        groupedData = this.groupArray(data, group);
      }
      return groupedData;
    }
  }
};
