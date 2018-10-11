import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

const template = require('./add-attribute-dialog.component.html');
require('./add-attribute-dialog.component.scss');

@Component({
  selector: 'add-attribute-dialog',
  template
})
export class AddAttributeDialogComponent {
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}
}
