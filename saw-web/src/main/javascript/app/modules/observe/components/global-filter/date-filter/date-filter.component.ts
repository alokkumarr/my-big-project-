import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as get from 'lodash/get';
import * as moment from 'moment';

const template = require('./date-filter.component.html');

@Component({
  selector: 'g-date-filter',
  template
})

export class GlobalDateFilterComponent implements OnInit {
  private _filter;
  private value: any= {};

  @Output() onModelChange = new EventEmitter();

  constructor() { }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;

    this.value.gte = moment(get(this._filter, 'model.gte'));
    this.value.lte = moment(get(this._filter, 'model.lte'));
  }

  onDateChange(field, data) {
    this.value[field] = data.value;
    this.onModelChange.emit({...this._filter, ...{
      model: {
        preset: 'NA',
        lte: this.value.lte.format('YYYY-MM-DD'),
        gte: this.value.gte.format('YYYY-MM-DD')
      }
    }})
  }
}
