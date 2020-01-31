import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import * as set from 'lodash/set';
import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'dataset-filters',
  templateUrl: './dataset-filters.component.html',
  styleUrls: ['./dataset-filters.component.scss']
})
export class DatasetFilterComponent implements OnInit {
  private filterPayload = {};
  public categoryType;

  @Input('typeFilterList') set setFilterList(data) {
    this.categoryType = data;
  }
  @Output() change = new EventEmitter<any>();
  constructor() {}

  ngOnInit() {}

  filterChange(event) {
    set(this.filterPayload, 'dstype', [event.data]);
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
}
