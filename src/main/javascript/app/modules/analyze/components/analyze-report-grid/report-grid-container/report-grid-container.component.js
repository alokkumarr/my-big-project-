import pipe from 'lodash/fp/pipe';
import groupBy from 'lodash/groupBy';
import map from 'lodash/map';
import forEach from 'lodash/forEach';
import omit from 'lodash/fp/omit';
import isEmpty from 'lodash/isEmpty';

import find from 'lodash/find';
import template from './report-grid-container.component.html';

export const LAYOUT_MODE = {
  DETAIL: 'detail',
  SUMMARY: 'summary'
}

export const ReportGridContainerComponent = {
  template,
  bindings: {
    id: '@',
    data: '<',
    columns: '<',
    settings: '<'
  },
  controller: class ReportGridContainerController {
    constructor($componentHandler, $timeout) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$timeout = $timeout;

      this.LAYOUT_MODE = LAYOUT_MODE;
      this.layoutMode = LAYOUT_MODE.DETAIL;
      this.groupedBy = '';

      this.render = true;
    }

    $onInit() {
      this._unregister = this._$componentHandler.register(this.id, this);

      this.modifiedData = this.data;
      // TODO add grouping data to somewhere
      // not sure where untill final json structure
    }

    $onDestroy() {
      this._unregister();
    }

    reload(columns, data) {
      this.render = false;

      this._$timeout(() => {
        this.columns = columns;
        this.data = data;
        this.modifiedData = data;
        this.render = true;
      }, 250);
    }

    groupData(columnName) {
      this.modifiedData = this.group(this.modifiedData, columnName);
    }

    undoGrouping() {
      this.modifiedData = this.data;
      this.groupedBy = '';
    }

    rename(columnName, newName) {
      const columnToRename = find(this.columns, column => column.name === columnName);

      columnToRename.alias = newName;
    }

    group(data, columnName) {
      this.groupedBy = `${this.groupedBy}${isEmpty(this.groupedBy) ? '' : ','} ${columnName}`;

      return this.groupRecursive(data, columnName);
    }

    groupRecursive(data, columnName) {
      // if it is a node
      let groupedData;

      if (data.isGroup) {
        forEach(data.groupNodes, groupNode => {
          groupNode.data = this.groupRecursive(groupNode.data, columnName);
        });

        groupedData = data;
      } else {
        // if it is a leaf
        groupedData = this.groupArray(data, columnName);
      }

      return groupedData;
    }

    groupArray(array, columnName) {
      const groupedObj = groupBy(array, columnName);
      const groupNodes = map(groupedObj, (val, key) => {
        return {
          groupValue: key,
          itemCount: val.length,
          data: map(val, omit(columnName))
        };
      });

      return {
        isGroup: true,
        groupBy: columnName,
        groupNodes: groupNodes
      }
    }
  }
};
