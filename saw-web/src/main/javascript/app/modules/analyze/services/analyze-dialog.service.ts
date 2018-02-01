import { Injectable } from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';

import {
  AnalysisDialogData,
  AnalysisStarter,
  Analysis,
  DesignerMode,
  Sort,
  Filter,
  IToolbarActionData,
  ArtifactColumns,
  Artifact
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

  openEditAdnalysisDialog(analysis: Analysis, mode: DesignerMode = 'edit') {
    const data: AnalysisDialogData = {
      analysis,
      designerMode: mode
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

  openFilterDialog(filters: Filter[], artifacts: Artifact[], booleanCriteria) {
    const data: IToolbarActionData = {
      action: 'filter',
      filters,
      artifacts,
      booleanCriteria
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

  openDescriptionDialog(description: string) {
    const data: IToolbarActionData = {
      action: 'description',
      description
    }
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openSaveDialog(analysis: Analysis) {
    const data: IToolbarActionData = {
      action: 'save',
      analysis
    }
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }
}
