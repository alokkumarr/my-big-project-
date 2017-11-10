import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material';

import DesignerDialogComponent from '../components/designer-dialog/designer-dialog.component';

@Injectable()
export default class AnalyzeDialogService {
  constructor(public dialog: MatDialog) {}

  openNewAnalysisDialog(analysis) {
    this.dialog.open(DesignerDialogComponent, {
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data: {analysis}
    } as MatDialogConfig);
  }
}
