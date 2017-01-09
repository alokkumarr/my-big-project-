import pipe from 'lodash/fp/pipe';
import groupBy from 'lodash/groupBy';
import map from 'lodash/map';
import forEach from 'lodash/forEach';
import omit from 'lodash/fp/omit';
import isEmpty from 'lodash/isEmpty';

export function ReportGridService() {
  'ngInject';

  let groupedBy = '';

  return {
    group,
    unGroup,
    getGroupedBy: () => groupedBy
  };

  function unGroup() {
    groupedBy = '';
  }

  function group(data, columnName) {
    groupedBy = `${groupedBy}${isEmpty(groupedBy) ? '' : ','} ${columnName}`;
    return groupRecursive(data, columnName);
  }

  function groupRecursive(data, columnName) {
    // if it is a node
    let groupedData;
    if (data.isGroup) {
      forEach(data.groupNodes, groupNode => {
        groupNode.data = groupRecursive(groupNode.data, columnName);
      });
      groupedData = data;
    } else {
      // if it is a leaf
      groupedData = groupArray(data, columnName);
    }
    return groupedData;
  }

  function groupArray(array, columnName) {

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
