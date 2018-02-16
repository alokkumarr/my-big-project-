import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ObserveService } from '../../../services/observe.service';

import * as get from 'lodash/get';

const template = require('./number-filter.component.html');

@Component({
  selector: 'g-number-filter',
  template
})

export class GlobalNumberFilterComponent implements OnInit {
  @Output() onModelChange = new EventEmitter();

  private _filter;
  private max = 100;
  private min = 1;
  private step = 1;
  private value: Array<number>;
  private config = {
    tooltips: true
  };
  constructor(private observe: ObserveService) { }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;

    this.loadMinMax();
    this.value = [this.min, this.max];
  }

  loadMinMax() {
    this.observe.getModelValues(this._filter).subscribe(data => {
      this.min = parseFloat(get(data, `_min`, this.min));
      this.max = parseFloat(get(data, `_max`, this.max));
      this.value = [this.min, this.max];
      this.onSliderChange(this.value);
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

