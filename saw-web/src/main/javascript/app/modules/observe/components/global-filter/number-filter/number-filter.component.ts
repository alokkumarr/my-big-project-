import { Component, OnInit, Input } from '@angular/core';

const template = require('./number-filter.component.html');

@Component({
  selector: 'g-number-filter',
  template
})

export class GlobalNumberFilterComponent implements OnInit {
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
  }
}

