import { Component, OnInit, Input } from '@angular/core';

const template = require('./date-filter.component.html');

@Component({
  selector: 'g-date-filter',
  template
})

export class GlobalDateFilterComponent implements OnInit {
  private _filter;
  constructor() { }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;
  }
}
