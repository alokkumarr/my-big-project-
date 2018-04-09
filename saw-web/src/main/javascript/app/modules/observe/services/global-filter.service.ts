import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as findIndex from 'lodash/findIndex';
import * as groupBy from 'lodash/groupBy';

import { Subject } from 'rxjs/Subject';

@Injectable()
export class GlobalFilterService {

  private rawFilters = [];
  private updatedFilters = [];
  public onFilterChange = new Subject();
  public onSidenavStateChange = new Subject();
  public onApplyFilter = new Subject();
  public onClearAllFilters = new Subject();

  constructor() { }

  initialise() {
    this.rawFilters = [];
    this.updatedFilters = [];
    this.onFilterChange.next(null);
  }

  get rawGlobalFilters() {
    return this.rawFilters;
  }

  get globalFilters() {
    return groupBy(this.updatedFilters, 'semanticId');
  }

  /**
   * Returns display name for a column from a list of columns
   *
   * @param {Array<any>} artifacts
   * @param {string} columnName
   * @param {string} tableName
   * @returns
   * @memberof GlobalFilterService
   */
  getDisplayNameFor(artifacts: Array<any>, columnName: string, tableName: string) {
    const col = find(artifacts, column =>
      column.columnName === columnName &&
      (column.table || column.tableName) === tableName
    );
    return col ? (col.aliasName || col.displayName || col.columnName) : columnName;
  }

  /**
   * Merges the existing raw filters with input array of filters.
   * Only adds filters which aren't already present.
   *
   * @param {Array<any>} filt
   * @memberof GlobalFilterService
   */
  addFilter(filt: Array<any>) {
    const validFilter = [];
    forEach(filt, f => {
      const exists = findIndex(this.rawFilters, rf => (
        rf.semanticId === f.semanticId &&
        rf.columnName === f.columnName &&
        rf.tableName === f.tableName
      ));

      if (!(exists >= 0)) {
        this.rawFilters.push(f);
        validFilter.push(f);
      }
    })
    if (validFilter.length) {
      this.onFilterChange.next(validFilter);
    }
  }

  /**
   * Store updated filter values in a new array
   *
   * @param {any} {data, valid}
   * @memberof GlobalFilterService
   */
  updateFilter({data, valid}) {
    const id = findIndex(this.updatedFilters, f => (
      f.semanticId === data.semanticId &&
      f.columnName === data.columnName &&
      f.tableName === data.tableName
    ));

    /* Push or replace existing filter */
    if (id >= 0 && !valid) {
      this.updatedFilters.splice(id, 1);
    } else if (id >= 0) {
      this.updatedFilters.splice(id, 1, data);
    } else {
      valid && this.updatedFilters.push(data);
    }
  }
}
