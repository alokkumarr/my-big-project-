import { Component } from '@angular/core';

const template = require('./workbench-page.component.html');
const style = require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  template: template,
  styles: [style]
})
export class WorkbenchPageComponent {
  constructor() { }
}
