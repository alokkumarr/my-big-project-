import { Injectable } from '@angular/core';
import * as has from 'lodash/has';
import * as find from 'lodash/find';

import { Subject } from 'rxjs/Subject';

@Injectable()
export class GlobalFilterService {

  private filters = [];
  public onFilterChange = new Subject();

  constructor() { }

  initialise() {
    this.filters = [];
    this.onFilterChange.next(null);
  }

  get globalFilters() {
    return this.filters;
  }

  addFilter(filt) {
    this.filters = this.filters.concat(filt);
    // find(this.filters, f => f.columnName === filt.columnName);
    this.onFilterChange.next(filt);
  }
}
