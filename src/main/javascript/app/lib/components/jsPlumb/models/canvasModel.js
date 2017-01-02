import find from 'lodash/find';

import {TableModel} from './tableModel';
import {JoinModel} from './joinModel';

export class CanvasModel {
  constructor() {
    this.tables = [];
    this.joins = [];
  }

  addTable(tableName) {
    if (!tableName) {
      return;
    }

    const table = new TableModel(this, tableName);
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
}
