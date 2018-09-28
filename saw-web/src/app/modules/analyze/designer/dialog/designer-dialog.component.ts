import { Component, Inject } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogConfig
} from '@angular/material';
import * as cloneDeep from 'lodash/cloneDeep';
import { AnalysisDialogData, DesignerSaveEvent, Analysis } from '../types';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../../common/types';

const template = require('./designer-dialog.component.html');

const CONFIRM_DIALOG_DATA: ConfirmDialogData = {
  title: 'There are unsaved changes',
  content: 'Do you want to discard unsaved changes and go back?',
  positiveActionLabel: 'Discard',
  negativeActionLabel: 'Cancel'
};
@Component({
  selector: 'designer-dialog',
  template,
  styles: [`:host {
    display: block;
    margin: -20px;
    overflow: hidden;
  }`]
})
export class DesignerDialogComponent {
  analysis: Analysis;
  savedAnalysis: Analysis;
  constructor(
    public dialogRef: MatDialogRef<DesignerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AnalysisDialogData,
    private _dialog: MatDialog
  ) {
    this.analysis = cloneDeep(data.analysis);
  }

  onBack(isInDraftMode) {
    if (isInDraftMode) {
      this.warnUser().afterClosed().subscribe(shouldDiscard => {
        if (shouldDiscard) {
          this.dialogRef.close({
            requestExecution: false,
            analysis: this.savedAnalysis
          });
        }
      });
    } else {
      this.dialogRef.close({
        requestExecution: false,
        analysis: this.savedAnalysis
      });
    }
  }

  onSave({analysis, requestExecution}: DesignerSaveEvent) {
    if (requestExecution) {
      this.dialogRef.close({
        requestExecution,
        analysis
      });
    } else {
      this.savedAnalysis = cloneDeep(analysis);
    }
  }

  warnUser() {
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: CONFIRM_DIALOG_DATA
    } as MatDialogConfig);
  }
}
