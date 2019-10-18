import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpFind from 'lodash/fp/find';
import * as fpGet from 'lodash/fp/get';
import { Store } from '@ngxs/store';

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
    @Inject(MAT_DIALOG_DATA) public data,
    private _store: Store
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
    return fpPipe(
      fpFlatMap(artifact => artifact.columns),
      fpFind(({ columnName }) => {
        return columnName === filter.columnName;
      }),
      fpGet('displayName')
    )(this._store.selectSnapshot
      (
      state => state.common.metrics[this.data.semanticId]).artifacts
      );
  }

  getFilterValue(filter) {
    return getFilterValue(filter);
  }
}
