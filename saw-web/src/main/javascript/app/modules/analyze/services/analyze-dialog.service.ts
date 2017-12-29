import { Injectable } from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';

import {
  AnalysisDialogData,
  AnalysisStarter,
  Analysis,
  Sort,
  Filter,
  IToolbarActionData,
  ArtifactColumns
} from '../types';
import { DesignerDialogComponent } from '../components/designer/dialog';
import { ToolbarActionDialogComponent } from '../components/designer/toolbar-action-dialog';
import { DesignerPreviewDialogComponent } from '../components/designer/preview-dialog';


@Injectable()
export class AnalyzeDialogService {
  constructor(public dialog: MatDialog) {}

  openNewAnalysisDialog(analysisStarter: AnalysisStarter) {
    const data: AnalysisDialogData = {
      analysisStarter,
      designerMode: 'new'
    };
    return this.openAnalysisDialog(data);
  }

  openEditAdnalysisDialog(analysis: Analysis) {
    const data: AnalysisDialogData = {
      analysis,
      designerMode: 'edit'
    };
    return this.openAnalysisDialog(data);
  }

  openAnalysisDialog(data: AnalysisDialogData) {
    return this.dialog.open(DesignerDialogComponent, {
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data
    } as MatDialogConfig);
  }

  openSortDialog(sorts: Sort[], artifactColumns: ArtifactColumns) {
    const data: IToolbarActionData = {
      action: 'sort',
      sorts,
      artifactColumns
    }
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openFilterDialog(filters: Filter[], artifactColumns: ArtifactColumns) {
    const data: IToolbarActionData = {
      action: 'filter',
      filters,
      artifactColumns
    }
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openPreviewDialog(analysis: Analysis) {
    const data = {
      analysis
    }
    return this.dialog.open(DesignerPreviewDialogComponent, {
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data
    } as MatDialogConfig);
  }
}
