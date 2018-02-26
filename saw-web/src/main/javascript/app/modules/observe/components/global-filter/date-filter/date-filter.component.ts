declare const require: any;
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import * as get from 'lodash/get';
import * as moment from 'moment';

import { Subscription } from 'rxjs/Subscription';

import { ObserveService } from '../../../services/observe.service';
import { GlobalFilterService } from '../../../services/global-filter.service';

import {CUSTOM_DATE_PRESET_VALUE, DATE_PRESETS} from '../../../../analyze/consts';
const template = require('./date-filter.component.html');

@Component({
  selector: 'g-date-filter',
  template
})

export class GlobalDateFilterComponent implements OnInit, OnDestroy {
  private _filter;
  private model: any = {};
  private presets = DATE_PRESETS;
  private defaults: {min, max};
  private showDateFields: boolean;
  private clearFiltersListener: Subscription;

  @Output() onModelChange = new EventEmitter();

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

    this.model.preset = this._filter.preset || CUSTOM_DATE_PRESET_VALUE;
    this.model.gte = moment(get(this._filter, 'model.gte'));
    this.model.lte = moment(get(this._filter, 'model.lte'));

    this.onPresetChange({value: this.model.preset});
    this.loadDateRange();
  }

  onPresetChange(data) {
    this.model.preset = data.value;
    this.showDateFields = this.model.preset === CUSTOM_DATE_PRESET_VALUE;
    this.onFilterChange();
  }

  onDateChange(field, data) {
    this.model[field] = data.value;
    this.onFilterChange();
  }

  isValid(filt) {
    return filt.model.preset !== CUSTOM_DATE_PRESET_VALUE ||
      (
        filt.model.lte &&
        filt.model.gte
      );
  }

  /**
   * Resets the date and preset value to default state
   *
   * @returns
   * @memberof GlobalDateFilterComponent
   */
  loadDefaults() {
    if (!this.defaults) {
      return;
    }

    this.onPresetChange({value: CUSTOM_DATE_PRESET_VALUE});
    this.onDateChange('gte', {
      value: this.defaults.min
    });
    this.onDateChange('lte', {
      value: this.defaults.max
    });
  }

  /**
   * Queries the min and max dates present in db for a field and updates the model with it
   *
   * @memberof GlobalDateFilterComponent
   */
  loadDateRange() {
    this.observe.getModelValues(this._filter).subscribe((data: {_min: string, _max: string}) => {
      this.defaults = {
        min: moment(parseInt(data._min)),
        max: moment(parseInt(data._max))
      };

      this.loadDefaults();
    });
  }

  onFilterChange() {
    const payload = {
      ...this._filter,
      ...{
        model: {
          preset: this.model.preset
        }
      }
    };

    if (this.model.preset === CUSTOM_DATE_PRESET_VALUE) {
      payload.model.lte = this.model.lte.format('YYYY-MM-DD');
      payload.model.gte = this.model.gte.format('YYYY-MM-DD');
    } else {
      delete payload.model.lte;
      delete payload.model.gte;
    }

    this.onModelChange.emit({data: payload, valid: this.isValid(payload)});
  }
}
