import find from 'lodash/find';
import get from 'lodash/get';
import flatMap from 'lodash/flatMap';
import filter from 'lodash/filter';

import {TableModel} from './tableModel';
import {JoinModel} from './joinModel';

export class CanvasModel {
  constructor() {
    this.tables = [];
    this.joins = [];
    this.sorts = [];
    this.groups = [];
    this.filters = [];
  }

  clear() {
    this.tables.length = 0;
    this.joins.length = 0;
    this.sorts.length = 0;

    this.clearGroups();
    this.clearFilters();
  }

  clearGroups() {
    this.groups.length = 0;
  }

  clearFilters() {
    this.filters.length = 0;
  }

  addTable(name) {
    const table = new TableModel(this, name);

    this.tables.push(table);

    return table;
  }

  findTable(tableName) {
    return find(this.tables, item => {
      return item.name === tableName;
    });
  }

  convertJoinToRef(sideObj) {
    sideObj.table = this.findTable(sideObj.table);
    sideObj.field = sideObj.table && sideObj.table.findField(sideObj.field);
  }

  addJoin(type, leftSide, rightSide) {
    if (!type) {
      return;
    }

    this.convertJoinToRef(leftSide);
    this.convertJoinToRef(rightSide);

    if (!leftSide.table || !leftSide.field || !rightSide.table || !rightSide.field) {
      return;
    }

    const join = new JoinModel(this, type, leftSide, rightSide);
    this.joins.push(join);

    return join;
  }

  findJoin(leftTable, leftField, rightTable, rightField) {
    return find(this.joins, join => {
      return join.leftSide.table.name === leftTable && join.leftSide.field.name === leftField &&
        join.rightSide.table.name === rightTable && join.rightSide.field.name === rightField;
    });
  }

  removeJoin(join) {
    const idx = this.joins.indexOf(join);

    if (idx !== -1) {
      this.joins.splice(idx, 1);
    }
  }

  getSelectedFields() {
    return flatMap(this.tables, table => {
      return filter(table.fields, field => {
        return field.checked;
      });
    });
  }

  /* Removes sorts and groups for fields that aren't selected anymore. */
  updateFields() {
    const fields = this.getSelectedFields();
    this.sorts = filter(this.sorts, sort => fields.indexOf(sort.field) >= 0);
    this.groups = filter(this.groups, group => fields.indexOf(group.field) >= 0);
  }

  addSort(sortObj) {
    sortObj.table = this.findTable(sortObj.table);
    sortObj.field = sortObj.table && sortObj.table.findField(sortObj.field);

    this.sorts.push(sortObj);

    return sortObj;
  }

  addGroup(groupObj) {
    groupObj.table = this.findTable(groupObj.table);
    groupObj.field = groupObj.table && groupObj.table.findField(groupObj.field);

    this.groups.push(groupObj);

    return groupObj;
  }

  removeGroup(group) {
    const idx = this.groups.indexOf(group);

    if (idx !== -1) {
      this.groups.splice(idx, 1);
    }
  }

  addFilter(filterObj) {
    filterObj.table = this.findTable(get(filterObj, 'column.table', ''));
    filterObj.field = filterObj.table && filterObj.table.findField(get(filterObj, 'column.columnName', ''));

    this.filters.push(filterObj);

    return filterObj;
  }

  removeFilter(filter) {
    const idx = this.filters.indexOf(filter);

    if (idx !== -1) {
      this.filters.splice(idx, 1);
    }
  }
}
