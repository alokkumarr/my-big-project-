declare const require: any;

import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ObserveService } from '../../../services/observe.service';
import { GlobalFilterService } from '../../../services/global-filter.service';

import { Subscription } from 'rxjs/Subscription';
import * as get from 'lodash/get';

const template = require('./number-filter.component.html');

@Component({
  selector: 'g-number-filter',
  template
})
export class GlobalNumberFilterComponent implements OnInit, OnDestroy {
  @Output() onModelChange = new EventEmitter();

  private _filter;
  private step = 1; // tslint:disable-line
  private defaults: {min, max} = {min: 1, max: 100};
  private filterCache: {operator?, start?, end?};
  private value: Array<number>;
  private config = { // tslint:disable-line
    tooltips: true
  };
  private clearFiltersListener: Subscription;
  private applyFiltersListener: Subscription;
  private closeFiltersListener: Subscription;

  constructor(
    private observe: ObserveService,
    private filters: GlobalFilterService
  ) { }

  ngOnInit() {
    this.clearFiltersListener = this.filters.onClearAllFilters.subscribe(() => {
      this.loadDefaults();
      this.cacheFilters();
    });

    this.applyFiltersListener = this.filters.onApplyFilter.subscribe(() => {
      this.cacheFilters();
    });

    this.closeFiltersListener = this.filters.onSidenavStateChange.subscribe(state => {
      if (!state) {
        this.loadDefaults(true); // load cached filter data since last apply
      }
    });
  }

  ngOnDestroy() {
    this.clearFiltersListener.unsubscribe();
    this.applyFiltersListener.unsubscribe();
    this.closeFiltersListener.unsubscribe();
  }

  @Input() set filter(data) {
    this._filter = data;

    this.loadMinMax();
    this.value = [this.defaults.min, this.defaults.max];
  }

  /**
   * Loads minimum and maximum values for this number field from backend.
   *
   * @memberof GlobalNumberFilterComponent
   */
  loadMinMax() {
    this.observe.getModelValues(this._filter).subscribe(data => {
      this.defaults.min = parseFloat(get(data, `_min`, this.defaults.min));
      this.defaults.max = parseFloat(get(data, `_max`, this.defaults.max));

      /* Give time for changes to min/max to propagate properly. The
        nouislider library uses a settimeout to update changes in min/max.
        https://github.com/tb/ng2-nouislider/blob/master/src/nouislider.ts#L154
      */
      setTimeout(() => {
        this.loadDefaults();
        this.cacheFilters();
      }, 10);
    });
  }

  /**
   * Caches filter in-memory. Cache is used to revert to last
   * applied / default values.
   *
   * @memberof GlobalNumberFilterComponent
   */
  cacheFilters() {
    this.filterCache = {
      operator: 'BTW',
      start: this.value[0],
      end: this.value[1]
    };
  }

  /**
   * Resets the slider start and end to default state or
   * last cached state as needed.
   *
   * @param {boolean} [fromCache=false]
   * @returns
   * @memberof GlobalNumberFilterComponent
   */
  loadDefaults(fromCache = false) {
    if ((fromCache && !this.filterCache)) {
      return;
    }

    const loadData = fromCache ? this.filterCache : {
      start: this.defaults.min,
      end: this.defaults.max
    };

    this.value = [loadData.start, loadData.end];
    this.onSliderChange(this.value);
  }

  /**
   * Gets the filter model together and communicates the
   * updated filter to the parent.
   *
   * @param {any} data
   * @memberof GlobalNumberFilterComponent
   */
  onSliderChange(data) {
    this.value = data;
    const payload = {
      ...this._filter,
      ...{
        model: {
          value: this.value[1],
          otherValue: this.value[0],
          operator: 'BTW'
        }
      }
    };

    this.onModelChange.emit({data: payload, valid: true});
  }
}

