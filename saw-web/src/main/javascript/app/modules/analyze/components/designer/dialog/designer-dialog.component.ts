import { Component, Inject } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogConfig
} from '@angular/material';
import { AnalysisStarter } from '../../../types'
import { ConfirmDialogComponent } from '../../../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../../../common/types';

const template = require('./designer-dialog.component.html');
require('./designer-dialog.component.scss');

const CONFIRM_DIALOG_DATA: ConfirmDialogData = {
  title: 'There are unsaved changes',
  content: 'Do you want to discard unsaved changes and go back?',
  positiveActionLabel: 'Discard',
  negativeActionLabel: 'Cancel'
};
@Component({
  selector: 'designer-dialog',
  template
})
export class DesignerDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DesignerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AnalysisStarter,
    private _dialog: MatDialog
  ) { }

  onBack(isInDraftMode) {
    if (isInDraftMode) {
      this.warnUser().afterClosed().subscribe(shouldDiscard => {
        if (shouldDiscard) {
          this.dialogRef.close();
        }
      });
    } else {
      this.dialogRef.close();
    }
  }

  onSave(isSaveSuccesful) {
    this.dialogRef.close(isSaveSuccesful);
  }

  warnUser() {
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: CONFIRM_DIALOG_DATA
    } as MatDialogConfig);
  }
}
