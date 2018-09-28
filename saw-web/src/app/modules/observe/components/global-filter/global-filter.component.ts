import {
  Component,
  AfterViewInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { GlobalFilterService } from '../../services/global-filter.service';
import { Subscription } from 'rxjs/Subscription';

import * as isArray from 'lodash/isArray';
import * as map from 'lodash/map';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';

import { NUMBER_TYPES, DATE_TYPES } from '../../../../common/consts';

const style = require('./global-filter.component.scss');

@Component({
  selector: 'global-filter',
  templateUrl: './global-filter.component.html',
  styles: [
    `:host {
      background-color: whitesmoke;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      height: 100%;
      width: 400px;
      padding: 10px;
    }`,
    style
  ]
})
export class GlobalFilterComponent implements AfterViewInit, OnDestroy {
  @Output() onApplyFilter = new EventEmitter();
  @Input() showKPIFilter: boolean;
  private globalFilters = [];
  private kpiFilter;
  private filterChangeSubscription: Subscription;

  constructor(private filters: GlobalFilterService) {}

  ngAfterViewInit() {
    this.globalFilters = [];
    this.kpiFilter = {};
    this.filterChangeSubscription = this.filters.onFilterChange.subscribe(
      this.onFilterChange.bind(this)
    );
  }

  addFilterType(filt) {
    let uiType = 'string';
    if (this.isType('number', filt.type)) {
      uiType = 'number';
    } else if (this.isType('date', filt.type)) {
      uiType = 'date';
    }
    return { ...filt, ...{ uiType } };
  }

  onFilterChange(data) {
    if (!data) {
      this.globalFilters = filter(this.globalFilters, gf =>
        find(this.filters.rawGlobalFilters, rf =>
          this.filters.areFiltersEqual(rf, gf)
        )
      );
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
    /* prettier-ignore */
    switch (type) {
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

  onKPIFilterUpdate(data) {
    this.kpiFilter = data;
  }

  tableNameFor(f) {
    return f.tableName + (f.metricName ? ` (${f.metricName})` : '');
  }

  ngOnDestroy() {
    this.filterChangeSubscription.unsubscribe();
  }

  stringify(data) {
    return JSON.stringify(data, null, 2);
  }

  onApply() {
    this.onApplyFilter.emit({
      analysisFilters: this.filters.globalFilters,
      kpiFilters: this.kpiFilter
    });
  }

  onCancel() {
    this.onApplyFilter.emit(false);
  }

  onClearFilters() {
    this.onApplyFilter.emit({ analysisFilters: {}, kpiFilters: {} });
    this.filters.onClearAllFilters.next(true);
  }
}
