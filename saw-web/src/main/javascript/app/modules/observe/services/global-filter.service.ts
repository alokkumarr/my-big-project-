import { Injectable } from '@angular/core';
import * as has from 'lodash/has';
import * as find from 'lodash/find';

@Injectable()
export class GlobalFilterService {

  private filters = [];

  constructor() { }

  initialise() {
    this.filters = [];
  }

  addFilter(filt) {
    find(this.filters, f => f.columnName === filt.columnName);
  }
}
