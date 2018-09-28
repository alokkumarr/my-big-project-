import { Component } from '@angular/core';

const style = require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  templateUrl: './workbench-page.component.html',
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
