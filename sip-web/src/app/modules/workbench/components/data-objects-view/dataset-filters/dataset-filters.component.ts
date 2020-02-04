import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import * as set from 'lodash/set';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';

@Component({
  selector: 'dataset-filters',
  templateUrl: './dataset-filters.component.html',
  styleUrls: ['./dataset-filters.component.scss']
})
export class DatasetFilterComponent implements OnInit {
  private filterPayload = {};
  public filterList;

  @Input('filterList') set setFilterList(data) {
    this.filterList = data;
  }
  @Output() change = new EventEmitter<any>();
  constructor() {}

  ngOnInit() {}

  filterChange(event) {
    forEach(this.filterList, filter => {
      const obj = find(filter.data, {
        value: event.data
      });
      if (!isEmpty(obj)) {
        set(this.filterPayload, filter.filterName, [obj.value]);
      }
    });
  }

  isFilterSelected() {
    return isEmpty(this.filterPayload);
  }

  applyOrResetFilters(str) {
    if (str === 'reset') {
      this.filterPayload = {};
    }
    this.change.emit({
      data: this.filterPayload
    });
  }

  /**
  Use this function to remove/reset individual filter.
  filterRemoved(event) {
    delete this.filterPayload[event.filterType];
    if (this.isFilterSelected()) {
      this.applyOrResetFilters('reset');
    }
  } */

  trackByIndex(index) {
    return index;
  }
}
