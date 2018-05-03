import {
  Component,
  Input,
  Output
} from '@angular/core';

const template = require('./analyze-actions-menu.component.html');

@Component({
  selector: 'analyze-actions-menu-u',
  template
})

export class AnalyzeActionsMenuComponent {
  @Input() analysis;

  actions = [{
    label: 'EXECUTE',
    value: 'execute',
    fn: this.execute.bind(this)
  }, {
    label: 'FORK_AND_EDIT',
    value: 'fork',
    fn: this.fork.bind(this)
  }, {
    label: 'EDIT',
    value: 'edit',
    fn: this.edit.bind(this)
  }, {
    label: 'PUBLISH',
    value: 'publish',
    fn: this.publish.bind(this)
  }, {
  /* gui-cleanup-2.0 */
  //   label: 'PRINT',
  //   value: 'print',
  //   fn: this.print.bind(this)
  // }, {
    label: 'EXPORT',
    value: 'export',
    fn: this.export.bind(this)
  }, {
    label: 'DELETE',
    value: 'delete',
    fn: this.delete.bind(this),
    color: 'warn'
  }]

  constructor() {}

}
