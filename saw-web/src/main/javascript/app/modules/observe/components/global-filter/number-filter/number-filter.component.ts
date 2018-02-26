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
  private step = 1;
  private defaults: {min, max} = {min: 1, max: 100};
  private value: Array<number>;
  private config = {
    tooltips: true
  };
  private clearFiltersListener: Subscription;

  constructor(
    private observe: ObserveService,
    private filters: GlobalFilterService
  ) { }

  ngOnInit() {
    this.clearFiltersListener = this.filters.onClearAllFilters.subscribe(() => {
      this.loadDefaults();
    });
  }

  ngOnDestroy() {
    this.clearFiltersListener.unsubscribe();
  }

  @Input() set filter(data) {
    this._filter = data;

    this.loadMinMax();
    this.value = [this.defaults.min, this.defaults.max];
  }

  loadDefaults() {
    this.value = [this.defaults.min, this.defaults.max];
    this.onSliderChange(this.value);
  }

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
      }, 10);
    });
  }

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

