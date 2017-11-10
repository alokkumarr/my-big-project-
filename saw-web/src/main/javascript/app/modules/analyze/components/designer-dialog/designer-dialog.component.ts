import {Component, Inject} from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
const template = require('./designer-dialog.component.html');
require('./designer-dialog.component.scss');

@Component({
  selector: 'designer-dialog',
  template
})
export default class DesignerDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DesignerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

}
