import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';

const template = require('./content.component.html');

@Component({
  selector: 'layout-content',
  template
})
export class LayoutContentComponent {
  constructor(
    private _title: Title
  ) {}

  ngOnInit() {
    this._title.setTitle(`Login`);
  }
}
