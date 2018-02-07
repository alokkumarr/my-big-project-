import { Component, AfterViewInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { GlobalFilterService } from '../../services/global-filter.service';
import { Subscription } from 'rxjs/Subscription'

import * as isArray from 'lodash/isArray';
import * as map from 'lodash/map';

import {NUMBER_TYPES, DATE_TYPES} from '../../../../common/consts';

const template = require('./global-filter.component.html');
require('./global-filter.component.scss');

@Component({
  selector: 'global-filter',
  template
})

export class GlobalFilterComponent implements AfterViewInit, OnDestroy {
  @Output() onApplyFilter = new EventEmitter();

  private globalFilters = [];
  private filterChangeSubscription: Subscription;

  constructor(
    private filters: GlobalFilterService
  ) { }

  ngAfterViewInit() {
    this.globalFilters = this.filters.rawGlobalFilters;
    this.filterChangeSubscription =  this.filters.onFilterChange
      .subscribe(this.onFilterChange.bind(this))
  }

  addFilterType(filt) {
    let uiType = 'string';
    if (this.isType('number', filt.type)) {
      uiType = 'number';
    } else if (this.isType('date', filt.type)) {
      uiType = 'date';
    }
    return {...filt, ...{ uiType }};
  }

  onFilterChange(data) {
    if (!data) {
      this.globalFilters = [];
    } else if (isArray(data)) {
      this.globalFilters.push.apply(
        this.globalFilters,
        map(data, this.addFilterType.bind(this))
      );
    } else {
      this.globalFilters.push(this.addFilterType(data));
    }
  }

  isType(type, input) {
    switch(type){
    case 'number':
      return NUMBER_TYPES.includes(input);

    case 'date':
      return DATE_TYPES.includes(input);

    case 'string':
    default:
      return type === 'string';
    }
  }

  onFilterUpdate(data) {
    this.filters.updateFilter(data);
  }

  ngOnDestroy() {
    this.filterChangeSubscription.unsubscribe();
  }

  stringify(data) {
    return JSON.stringify(data, null, 2);
  }

  onApply() {
    this.onApplyFilter.emit(this.filters.globalFilters);
  }

  onCancel() {
    this.onApplyFilter.emit(false);
  }

  onClearFilters() {
    this.onApplyFilter.emit({});
  }
}
