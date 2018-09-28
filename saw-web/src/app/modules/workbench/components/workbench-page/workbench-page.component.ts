import { Component } from '@angular/core';

const template = require('./workbench-page.component.html');
const style = require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  template: template,
  styles: [
    `:host {
      width: 100%;
      max-width: 100% !important;
      height: 100%;
      max-height: 100%;
      overflow: auto;
    }`,
    style
  ]
})
export class WorkbenchPageComponent {
  constructor() { }
}
