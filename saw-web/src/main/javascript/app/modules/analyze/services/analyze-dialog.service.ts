import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material';
import Analysis from '../models/analysis.model';
import {
  DesignerMode,
  AnalysisDialogData,
  AnalysisStarter
} from '../types';

import DesignerDialogComponent from '../components/designer/dialog/designer-dialog.component';


@Injectable()
export class AnalyzeDialogService {
  constructor(public dialog: MatDialog) {}

  openNewAnalysisDialog(analysisStarter: AnalysisStarter) {
    const data: AnalysisDialogData = {
      analysisStarter,
      designerMode: 'new'
    };
    this.openAnalysisDialog(data);
  }

  openEditAdnalysisDialog(analysis: Analysis) {
    const data: AnalysisDialogData = {
      analysis,
      designerMode: 'edit'
    };
    this.openAnalysisDialog(data);
  }

  openAnalysisDialog(data: AnalysisDialogData) {
    this.dialog.open(DesignerDialogComponent, {
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data
    } as MatDialogConfig);
  }
}
