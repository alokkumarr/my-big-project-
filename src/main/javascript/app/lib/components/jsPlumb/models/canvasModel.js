import find from 'lodash/find';
import flatMap from 'lodash/flatMap';
import filter from 'lodash/filter';
import forEach from 'lodash/forEach';

import {TableModel} from './tableModel';
import {JoinModel} from './joinModel';

export class CanvasModel {
  constructor() {
    this.tables = [];
    this.joins = [];
    this.sorts = [];
  }

  precess(data) {
    // hard coded columns to show by default
    // these are the columns in the mock data
    const defaultColumns = ['CustomerName', 'TotalPrice', 'ShipperName', 'WarehouseName'];

    this.tables.length = 0;
    this.joins.length = 0;
    this.sorts.length = 0;

    forEach(data, itemA => {
      const table = this.addTable(itemA._artifact_name);

      table.setPosition(itemA._artifact_position[0], itemA._artifact_position[1]);

      forEach(itemA._artifact_attributes, itemB => {
        const field = table.addField(itemB['_actual_col-name'], itemB._display_name, itemB._alias_name);

        field.setType(itemB._type);
        field.selected = Boolean(find(defaultColumns, columnName => columnName === field.name));
      });
    });

    forEach(data, itemA => {
      forEach(itemA._sql_builder.joins, itemB => {
        const tableA = itemB.criteria[0]['table-name'];
        const tableB = itemB.criteria[1]['table-name'];

        if (tableA !== tableB) {
          this.addJoin(itemB.type, {
            table: tableA,
            field: itemB.criteria[0]['column-name'],
            side: itemB.criteria[0].side
          }, {
            table: tableB,
            field: itemB.criteria[1]['column-name'],
            side: itemB.criteria[1].side
          });
        }
      });

      forEach(itemA._sql_builder._order_by_columns, itemB => {
        const sort = this.addSort({
          table: itemA._artifact_name,
          field: itemB['col-name'],
          order: itemB.order
        });
      });
    });
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

  getSelectedFields() {
    return flatMap(this.tables, table => {
      return filter(table.fields, field => {
        return field.selected === true;
      });
    });
  }

  addSort(sortObj) {
    sortObj.table = this.findTable(sortObj.table);
    sortObj.field = sortObj.table && sortObj.table.findField(sortObj.field);

    this.sorts.push(sortObj);

    return sortObj;
  }

  generateQuery() {
    let sql = 'SELECT ';

    const fields = [];
    const tables = [];

    forEach(this.tables, (table, idx) => {
      let hasSelectedFields = false;

      forEach(table.fields, field => {
        if (field.selected) {
          fields.push(`t${idx}.${field.name}`);
          hasSelectedFields = true;
        }
      });

      if (hasSelectedFields) {
        tables.push(`\nFROM ${table.name} as t${idx}`)
      }
    });

    if (fields.length) {
      sql += `\n\t${fields.join(', ')}`;
    }

    if (tables.length) {
      sql += tables.join(', ');
    }

    const orders = [];

    forEach(this.sorts, (sort) => {
      const tableIdx = this.tables.indexOf(sort.table);

      orders.push(`t${tableIdx}.${sort.field.name} ${(sort.order || 'ASC').toUpperCase()}`);
    });

    if (orders.length) {
      sql += `\nORDER BY ${orders.join(', ')}`;
    }

    return sql;
  }
}
