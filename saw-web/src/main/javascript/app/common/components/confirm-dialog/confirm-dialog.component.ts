declare const require: any;
import {
  Component,
  Inject
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { ConfirmDialogData } from '../../types';

const template = require('./confirm-dialog.component.html');
// require('./confirm-dialog.component.scss');

@Component({
  selector: 'confirm-dialog',
  template
})
export class ConfirmDialogComponent {

  public alias: string;

  constructor(
    private _dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}

  onNegativeAction() {
    this._dialogRef.close();
  }

  onPositiveAction() {
    this._dialogRef.close(true);
  }
}
