declare const require: any;

import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {FormControl} from '@angular/forms';
import * as moment from 'moment';
import {MAT_DATE_FORMATS} from '@angular/material/core';
import {
  FilterModel
} from '../../types';
import {CUSTOM_DATE_PRESET_VALUE, DATE_PRESETS} from '../../../../consts';

const template = require('./designer-date-filter.component.html');

const DATE_FORMAT = 'YYYY-MM-DD';

export const MY_FORMATS = {
  parse: {
    dateInput: DATE_FORMAT
  },
  display: {
    dateInput: DATE_FORMAT,
    monthYearLabel: 'MM-YYYY',
    dateA11yLabel: DATE_FORMAT,
    monthYearA11yLabel: 'MM-YYYY'
  }
};

@Component({
  selector: 'designer-date-filter',
  template,
  providers: [
    {provide: MAT_DATE_FORMATS, useValue: MY_FORMATS}
  ]
})
export class DesignerDateFilterComponent {
  @Output() public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  date = new FormControl({value: moment(), disabled: true});
  datePreset = new FormControl({value: null, disabled: false})
  showDateFields = false;
  presets = DATE_PRESETS;

  public tempModel = {
    gte: null,
    lte: null,
    preset: CUSTOM_DATE_PRESET_VALUE
  }

  init() {
    if (!this.filterModel) {
      this.filterModel = {};
    } else {
      this.datePreset.setValue(this.filterModel.preset || CUSTOM_DATE_PRESET_VALUE);
      this.tempModel = {
        gte: moment(this.filterModel.gte),
        lte: moment(this.filterModel.lte),
        preset: this.filterModel.preset || CUSTOM_DATE_PRESET_VALUE
      }
    }

    this.showDateFields = (this.tempModel || this.filterModel).preset === CUSTOM_DATE_PRESET_VALUE;
  }

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    this.init();
  }

  onPresetChange(change) {
    this.filterModel.preset = change.value;
    if (change.value !== CUSTOM_DATE_PRESET_VALUE) {
      delete this.filterModel.gte;
      delete this.filterModel.lte;
    }

    this.showDateFields = change.value === CUSTOM_DATE_PRESET_VALUE;
    this.filterModelChange.emit(this.filterModel);
  }

  modelChange(value, prop: 'lte' | 'gte') {
    this.tempModel[prop] = value;
    this.filterModel[prop] = value.format(DATE_FORMAT);
    this.filterModelChange.emit(this.filterModel);
  }
}
