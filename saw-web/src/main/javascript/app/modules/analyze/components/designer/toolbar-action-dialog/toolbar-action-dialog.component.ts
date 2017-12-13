import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { IToolbarActionData } from '../types'
const template = require('./toolbar-action-dialog.component.html');
// require('./toolbar-action-dialog.component.scss');

@Component({
  selector: 'designer-dialog',
  template
})
export class ToolbarActionDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ToolbarActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IToolbarActionData) { }

  onBack() {
    this.dialogRef.close();
  }
}
