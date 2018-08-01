import { HostBinding } from '@angular/core';
require('./base-dialog.component.scss');

export class BaseDialogComponent {
  @HostBinding('class.base-dialog') someField: boolean = true;
}
