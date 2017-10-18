import * as groupBy from 'lodash/groupBy';
import * as map from 'lodash/map';
import * as omit from 'lodash/fp/omit';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import * as keys from 'lodash/keys';
import * as isArray from 'lodash/isArray';
import * as reduce from 'lodash/reduce';
import * as join from 'lodash/join';
import json2csv from 'json2csv';

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
    groups: '<',
    requester: '<'
  },
  styles: [style],
  controller: class ReportGridDisplayContainerController {
    constructor(fileService) {
      'ngInject';
      this._fileService = fileService;
      this.LAYOUT_MODE = LAYOUT_MODE;
    }

    $onInit() {
      this.requester.subscribe(requests => this.request(requests));

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

    request(requests) {
      /* eslint-disable no-unused-expressions */
      requests.export && this.onExport();
      /* eslint-disable no-unused-expressions */
    }

    onExport() {
      this._fileService.exportCSV(this.groupData2CSV(this.groupedData));
    }

    groupData2CSV(data) {
      if (isArray(data)) {
        return json2csv({data, fields: keys(data[0])});
      }
      const dataForCSV = this.groupData2CSVRecursive({node: data, groupValues: []});
      const csv = reduce(dataForCSV, (aggregator, datum) => {
        aggregator.push(`\n${join(datum.groupValues, '->')}\n\n${json2csv({data: datum.data, fields: datum.fields})}`);
        return aggregator;
      }, []);

      return join(csv, '\n');
    }

    groupData2CSVRecursive({node, groupValues}) {
      if (node.isGroup) {
        return flatMap(node.nodes, node => this.groupData2CSVRecursive({
          node,
          groupValues
        }));
      }
      if (node.data && !isArray(node.data)) {
        return this.groupData2CSVRecursive({
          node: node.data,
          groupValues: [...groupValues, node.groupValue]
        });
      }
      return {
        fields: keys(node.data[0]),
        data: node.data,
        groupValues: [...groupValues, node.groupValue]
      };
    }

    $onChanges() {
      this.groupedData = this.groupData(this.data, this.groups);
    }

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
