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
    this.groups = [];
    this.filters = [];
  }

  fill(data) {
    this.tables.length = 0;
    this.joins.length = 0;
    this.sorts.length = 0;
    this.cleanGroups();
    this.cleanFilters();

    /* eslint-disable camelcase */
    forEach(data, itemA => {
      const table = this.addTable(itemA.artifact_name);

      table.setMeta(itemA);
      table.setPosition(itemA.artifact_position[0], itemA.artifact_position[1]);

      forEach(itemA.artifact_attributes, itemB => {
        const field = table.addField(itemB.column_name);

        field.setMeta(itemB);
        field.displayName = itemB.display_name;
        field.alias = itemB.alias_name;
        field.type = itemB.type;
        field.checked = itemB.checked;
        field.isHidden = Boolean(itemB.hide);
        field.isJoinEligible = Boolean(itemB.join_eligible);
        field.isFilterEligible = Boolean(itemB.filter_eligible);
      });
    });

    forEach(data, itemA => {
      forEach(itemA.sql_builder.joins, itemB => {
        const tableA = itemB.criteria[0].table_name;
        const tableB = itemB.criteria[1].table_name;

        if (tableA !== tableB) {
          this.addJoin(itemB.type, {
            table: tableA,
            field: itemB.criteria[0].column_name,
            side: itemB.criteria[0].side
          }, {
            table: tableB,
            field: itemB.criteria[1].column_name,
            side: itemB.criteria[1].side
          });
        }
      });

      forEach(itemA.sql_builder.order_by_columns, itemB => {
        this.addSort({
          table: itemA.artifact_name,
          field: itemB.column_name,
          order: itemB.order
        });
      });

      forEach(itemA.sql_builder.group_by_columns, itemB => {
        this.addGroup({
          table: itemA.artifact_name,
          field: itemB
        });
      });

      forEach(itemA.sql_builder.filters, itemB => {
        this.addFilter({
          table: itemA.artifact_name,
          field: itemB.column_name,
          booleanCriteria: itemB.boolean_criteria,
          operator: itemB.operator,
          searchConditions: itemB.search_conditions
        });
      });
    });
    /* eslint-enable camelcase */
  }

  cleanGroups() {
    this.groups.length = 0;
  }

  cleanFilters() {
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
    filterObj.table = this.findTable(filterObj.table);
    filterObj.field = filterObj.table && filterObj.table.findField(filterObj.field);

    this.filters.push(filterObj);

    return filterObj;
  }

  removeFilter(filter) {
    const idx = this.filters.indexOf(filter);

    if (idx !== -1) {
      this.filters.splice(idx, 1);
    }
  }

  generatePayload() {
    const tableArtifacts = [];

    /* eslint-disable camelcase */
    forEach(this.tables, table => {
      const tableArtifact = {
        artifact_name: table.name,
        artifact_position: [table.x, table.y],
        artifact_attributes: [],
        sql_builder: {
          group_by_columns: [],
          order_by_columns: [],
          joins: [],
          filters: []
        },
        data: []
      };

      tableArtifacts.push(tableArtifact);

      forEach(table.fields, field => {
        const fieldArtifact = {
          column_name: field.meta.column_name,
          display_name: field.meta.display_name,
          alias_name: field.alias,
          type: field.meta.type,
          hide: field.isHidden,
          join_eligible: field.meta.join_eligible,
          filter_eligible: field.meta.filter_eligible,
          checked: field.checked
        };

        tableArtifact.artifact_attributes.push(fieldArtifact);
      });

      const joins = filter(this.joins, join => {
        return join.leftSide.table === table;
      });

      forEach(joins, join => {
        const joinArtifact = {
          type: join.type,
          criteria: []
        };

        joinArtifact.criteria.push({
          table_name: join.leftSide.table.name,
          column_name: join.leftSide.field.name,
          side: join.leftSide.side
        });

        joinArtifact.criteria.push({
          table_name: join.rightSide.table.name,
          column_name: join.rightSide.field.name,
          side: join.rightSide.side
        });

        tableArtifact.sql_builder.joins.push(joinArtifact);
      });

      const sorts = filter(this.sorts, sort => {
        return sort.table === table;
      });

      forEach(sorts, sort => {
        const sortArtifact = {
          column_name: sort.field.name,
          order: sort.order
        };

        tableArtifact.sql_builder.order_by_columns.push(sortArtifact);
      });

      const groups = filter(this.groups, group => {
        return group.table === table;
      });

      forEach(groups, group => {
        tableArtifact.sql_builder.group_by_columns.push(group.field.name);
      });

      const filters = filter(this.filters, filter => {
        return filter.table === table;
      });

      forEach(filters, filter => {
        const filterArtifact = {
          column_name: filter.field.name,
          boolean_criteria: filter.booleanCriteria,
          operator: filter.operator,
          search_conditions: filter.searchConditions
        };

        tableArtifact.sql_builder.filters.push(filterArtifact);
      });
    });
    /* eslint-enable camelcase */

    return {
      _artifacts: tableArtifacts
    };
  }
}
