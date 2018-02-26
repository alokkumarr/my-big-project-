declare const require: any;
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as get from 'lodash/get';
import * as moment from 'moment';

import { ObserveService } from '../../../services/observe.service';

import {CUSTOM_DATE_PRESET_VALUE, DATE_PRESETS} from '../../../../analyze/consts';
const template = require('./date-filter.component.html');

@Component({
  selector: 'g-date-filter',
  template
})

export class GlobalDateFilterComponent implements OnInit {
  private _filter;
  private model: any = {};
  private presets = DATE_PRESETS;
  private showDateFields: boolean;

  @Output() onModelChange = new EventEmitter();

  constructor(private observe: ObserveService) { }

  ngOnInit() { }

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
   * Queries the min and max dates present in db for a field and updates the model with it
   *
   * @memberof GlobalDateFilterComponent
   */
  loadDateRange() {
    this.observe.getModelValues(this._filter).subscribe((data: {_min: string, _max: string}) => {
      this.onDateChange('gte', {
        value: moment(parseInt(data._min))
      });
      this.onDateChange('lte', {
        value: moment(parseInt(data._max))
      });
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
