import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as findIndex from 'lodash/findIndex';
import * as groupBy from 'lodash/groupBy';

import { Subject, BehaviorSubject } from 'rxjs';

@Injectable()
export class GlobalFilterService {
  public rawFilters = [];
  public updatedFilters = [];
  public onFilterChange = new Subject();
  public onSidenavStateChange = new Subject();
  public onApplyKPIFilter = new BehaviorSubject(null);
  public onApplyFilter = new Subject();
  public onClearAllFilters = new Subject();

  constructor() {}

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
  getDisplayNameFor(
    artifacts: Array<any>,
    columnName: string,
    tableName: string
  ) {
    const col = find(
      artifacts,
      column =>
        column.columnName === columnName &&
        (column.table || column.tableName) === tableName
    );
    return col ? col.displayName || col.columnName : columnName;
  }

  areFiltersEqual(f1, f2) {
    return (
      f1.semanticId === f2.semanticId &&
      f1.tableName === f2.tableName &&
      f1.columnName === f2.columnName
    );
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
      const exists = findIndex(this.rawFilters, rf =>
        this.areFiltersEqual(rf, f)
      );

      if (!(exists >= 0)) {
        this.rawFilters.push(f);
        validFilter.push(f);
      }
    });
    if (validFilter.length) {
      this.onFilterChange.next(validFilter);
    }
  }

  removeInvalidFilters(filt: Array<any>) {
    this.rawFilters = filter(this.rawFilters, rf =>
      find(filt, f => this.areFiltersEqual(f, rf))
    );
    this.updatedFilters = filter(this.updatedFilters, uf =>
      find(filt, f => this.areFiltersEqual(f, uf))
    );

    this.onFilterChange.next(null);
  }

  /**
   * Store updated filter values in a new array
   *
   * @param {any} {data, valid}
   * @memberof GlobalFilterService
   */
  updateFilter({ data, valid }) {
    const id = findIndex(
      this.updatedFilters,
      f =>
        f.semanticId === data.semanticId &&
        f.columnName === data.columnName &&
        f.tableName === data.tableName
    );

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
