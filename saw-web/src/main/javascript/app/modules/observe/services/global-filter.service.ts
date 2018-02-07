import { Injectable } from '@angular/core';
import * as has from 'lodash/has';
import * as find from 'lodash/find';
import * as findIndex from 'lodash/findIndex';
import * as groupBy from 'lodash/groupBy';

import { Subject } from 'rxjs/Subject';

@Injectable()
export class GlobalFilterService {

  private rawFilters = [];
  private updatedFilters = [];
  public onFilterChange = new Subject();
  public onApplyFilter = new Subject();

  constructor() { }

  initialise() {
    this.rawFilters = [];
    this.updatedFilters = [];
    this.onFilterChange.next(null);
  }

  get rawGlobalFilters() {
    return this.rawFilters;
  }

  addFilter(filt) {
    this.rawFilters = this.rawFilters.concat(filt);
    // TODO: Only allow unique filters to be added to this
    // find(this.filters, f => f.columnName === filt.columnName);
    this.onFilterChange.next(filt);
  }

  get globalFilters() {
    return groupBy(this.updatedFilters, 'semanticId');
  }

  /* Store updated filter values in a new array */
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
