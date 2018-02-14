declare const require: any;
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as get from 'lodash/get';
import * as moment from 'moment';

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

  constructor() { }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;

    this.model.preset = this._filter.preset || CUSTOM_DATE_PRESET_VALUE;
    this.model.gte = moment(get(this._filter, 'model.gte'));
    this.model.lte = moment(get(this._filter, 'model.lte'));

    this.onPresetChange({value: this.model.preset});
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
