import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

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
  constructor() { }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;

    this.value = [this.min, this.max];
  }

  onSliderChange(data) {
    this.value = data;
    this.onModelChange.emit({
      ...this._filter,
      ...{
        model: {
          value: this.value[1],
          otherValue: this.value[0],
          operator: 'BTW'
        }
      }
    })
  }
}

