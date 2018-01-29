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

  public tempModel = {
    gte: null,
    lte: null,
    preset: 'NA'
  }

  ngOnInit() {
    if (!this.filterModel) {
      this.filterModel = {};
    } else {
      this.tempModel = {
        gte: moment(this.filterModel.gte),
        lte: moment(this.filterModel.lte),
        preset: 'NA'
      }
    }
  }

  modelChange(value, prop: 'lte' | 'gte') {
    this.tempModel[prop] = value;
    this.filterModel[prop] = value.format(DATE_FORMAT);
    this.filterModelChange.emit(this.filterModel);
  }
}
