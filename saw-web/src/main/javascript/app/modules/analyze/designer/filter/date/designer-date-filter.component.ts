import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';
import * as moment from 'moment';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { FilterModel } from '../../types';
import { CUSTOM_DATE_PRESET_VALUE, DATE_PRESETS } from '../../../consts';

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

export const isValid = (model: FilterModel): boolean => {
  model = model || {};
  return model.preset === CUSTOM_DATE_PRESET_VALUE
    ? Boolean(model.lte && model.gte)
    : Boolean(model.preset);
};

@Component({
  selector: 'designer-date-filter',
  template,
  providers: [{ provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }]
})
export class DesignerDateFilterComponent {
  @Output()
  public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  lteFC = new FormControl({ value: null, disabled: true });
  gteFC = new FormControl({ value: null, disabled: true });
  datePreset = new FormControl({ value: null, disabled: false });
  showDateFields = false;
  presets = DATE_PRESETS;

  public tempModel;

  ngOnInit() {
    if (this.filterModel) {
      this.tempModel = {
        gte: moment(this.filterModel.gte),
        lte: moment(this.filterModel.lte),
        preset: this.filterModel.preset || CUSTOM_DATE_PRESET_VALUE
      };
      this.lteFC.setValue(this.tempModel.lte);
      this.gteFC.setValue(this.tempModel.gte);
    } else {
      this.tempModel = {
        gte: null,
        lte: null,
        preset: CUSTOM_DATE_PRESET_VALUE
      };
    }
    this.datePreset.setValue(this.tempModel.preset);
    this.showDateFields = this.tempModel.preset === CUSTOM_DATE_PRESET_VALUE;
  }

  onPresetChange(change) {
    this.tempModel.preset = change.value;
    if (change.value !== CUSTOM_DATE_PRESET_VALUE) {
      this.tempModel.gte = null;
      this.tempModel.lte = null;
    }

    this.showDateFields = change.value === CUSTOM_DATE_PRESET_VALUE;
    this.filterModelChange.emit(this.getFormattedModel(this.tempModel));
  }

  modelChange(value, prop: 'lte' | 'gte') {
    this.tempModel[prop] = value;
    this.filterModelChange.emit(this.getFormattedModel(this.tempModel));
  }

  getFormattedModel({ lte, gte, preset }) {
    /* prettier-ignore */
    return {
      preset,
      ...(preset === CUSTOM_DATE_PRESET_VALUE ? {
        lte: lte ? lte.format(DATE_FORMAT) : null,
        gte: gte ? gte.format(DATE_FORMAT) : null
      } : {})
    };
  }
}
