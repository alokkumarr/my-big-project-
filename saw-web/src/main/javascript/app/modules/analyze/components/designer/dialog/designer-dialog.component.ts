import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { AnalysisStarter } from '../../../types'
const template = require('./designer-dialog.component.html');
require('./designer-dialog.component.scss');

@Component({
  selector: 'designer-dialog',
  template
})
export class DesignerDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DesignerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AnalysisStarter) { }

  onBack() {
    this.dialogRef.close();
  }
}
