import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import {
  getFilterValue
} from './../../../../analyze/consts';

@Component({
  selector: 'widget-filters',
  templateUrl: './widget-filters.component.html',
  styleUrls: ['./widget-filters.component.scss']
})
export class WidgetFiltersComponent implements OnInit {
  filters: any;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    this.filters = this.data.filters;
    this.filters = fpPipe(
        fpFilter(({ primaryKpiFilter }) => {
          return !primaryKpiFilter;
        })
      )(this.data.filters);
  }

  getDisplayName(filter) {
    return filter.columnName;
  }

  getFilterValue(filter) {
    return getFilterValue(filter);
  }
}
