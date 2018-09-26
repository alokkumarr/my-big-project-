import { Component } from '@angular/core';

const template = require('./workbench-page.component.html');
require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  template: template
})
export class WorkbenchPageComponent {
  constructor() { }
}
