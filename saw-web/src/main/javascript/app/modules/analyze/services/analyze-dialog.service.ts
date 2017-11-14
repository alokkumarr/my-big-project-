import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material';
import Analysis from '../models/analysis.model';
import {DesignerMode} from '../constsTS';

import DesignerDialogComponent from '../components/designer/dialog/designer-dialog.component';

export type NewAnalysisDialogData = {analysis: Analysis, designerMode: DesignerMode};
@Injectable()
export class AnalyzeDialogService {
  constructor(public dialog: MatDialog) {}

  openNewAnalysisDialog(analysis: Analysis, designerMode: DesignerMode) {
    const data: NewAnalysisDialogData = {analysis, designerMode};

    this.dialog.open(DesignerDialogComponent, {
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data
    } as MatDialogConfig);
  }
}
