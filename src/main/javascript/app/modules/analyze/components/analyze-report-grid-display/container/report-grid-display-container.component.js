import groupBy from 'lodash/groupBy';
import map from 'lodash/map';
import omit from 'lodash/fp/omit';
import forEach from 'lodash/forEach';
import find from 'lodash/find';
import flatMap from 'lodash/flatMap';
import keys from 'lodash/keys';
import isArray from 'lodash/isArray';
import reduce from 'lodash/reduce';
import join from 'lodash/join';
import json2csv from 'json2csv';

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
        const targetColumns = find(columns, column => column.columnName === group.columnName);
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
