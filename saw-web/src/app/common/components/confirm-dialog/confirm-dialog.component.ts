import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { ConfirmDialogData } from '../../types';

@Component({
  selector: 'confirm-dialog',
  templateUrl: 'confirm-dialog.component.html'
})
export class ConfirmDialogComponent {
  public alias: string;

  constructor(
    public _dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmDialogData
  ) {}
}
