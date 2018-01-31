import { Component, OnInit } from '@angular/core';

const template = require('./global-filter.component.html');
require('./global-filter.component.scss');

@Component({
  selector: 'global-filter',
  template
})

export class GlobalFilterComponent implements OnInit {
  constructor() { }

  ngOnInit() { }
}
