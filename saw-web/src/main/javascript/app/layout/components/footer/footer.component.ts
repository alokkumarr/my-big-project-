import { Component } from '@angular/core';
const template = require('./footer.component.html');

@Component({
  selector: 'layout-footer',
  template
})

export class LayoutFooterComponent {
  constructor() {
    this.version = __VERSION__;
  }
}