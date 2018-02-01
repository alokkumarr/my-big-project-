import { Component, OnInit, OnDestroy } from '@angular/core';
import { GlobalFilterService } from '../../services/global-filter.service';
import { Subscription } from 'rxjs/Subscription'

import * as isArray from 'lodash/isArray';

const template = require('./global-filter.component.html');
require('./global-filter.component.scss');

@Component({
  selector: 'global-filter',
  template
})

export class GlobalFilterComponent implements OnInit, OnDestroy {
  private globalFilters = [];
  private filterChangeSubscription: Subscription;

  constructor(
    private filters: GlobalFilterService
  ) { }

  ngOnInit() {
    this.globalFilters = this.filters.globalFilters;
    this.filterChangeSubscription =  this.filters.onFilterChange
      .subscribe(this.onFilterChange.bind(this))
  }

  onFilterChange(data) {
    if (!data) {
      this.globalFilters = [];
    } else if (isArray(data)) {
      this.globalFilters.push.apply(this.globalFilters, data);
    } else {
      this.globalFilters.push(data);
    }
  }

  ngOnDestroy() {
    this.filterChangeSubscription.unsubscribe();
  }

  stringify(data) {
    return JSON.stringify(data, null, 2);
  }
}
