import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';

import DesignerDialogComponent from '../components/designer-dialog/designer-dialog.component';

@Injectable()
export default class AnalyzeDialogService {
  constructor(public dialog: MatDialog) {}

  openNewAnalysisDialog() {
    console.log('sfafsas');
    this.dialog.open(DesignerDialogComponent, {
      width: '100 vw',
      height: '100 vh'
    });
  }
}
