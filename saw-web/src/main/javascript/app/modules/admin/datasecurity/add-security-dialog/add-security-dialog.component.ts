import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

const template = require('./add-security-dialog.component.html');
require('./add-security-dialog.component.scss');

@Component({
  selector: 'add-secuirty-dialog',
  template
})
export class AddSecurityDialogComponent {
  constructor(
    private _dialogRef: MatDialogRef<AddSecurityDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}
}
