import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';

import {
  AnalysisDialogData,
  AnalysisStarter,
  Analysis,
  DesignerMode,
  Sort,
  Filter,
  IToolbarActionData,
  ArtifactColumns,
  Artifact,
  Format
} from '../types';
import { DesignerDialogComponent } from '../components/designer/dialog';
import { ToolbarActionDialogComponent } from '../components/designer/toolbar-action-dialog';
import {
  DesignerFilterDialogComponent,
  DesignerFilterDialogData
} from '../components/designer/filter';
import { DesignerPreviewDialogComponent } from '../components/designer/preview-dialog';
import { DataFormatDialogComponent } from '../../../common/components/data-format-dialog';
import { DateFormatDialogComponent } from '../../../common/components/date-format-dialog';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../common/types';

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

  openEditAnalysisDialog(analysis: Analysis, mode: DesignerMode = 'edit') {
    const data: AnalysisDialogData = {
      analysis,
      designerMode: mode
    };
    return this.openAnalysisDialog(data);
  }

  openAnalysisDialog(data: AnalysisDialogData) {
    return this.dialog.open(DesignerDialogComponent, {
      panelClass: 'designer-dialog',
      width: '100vw',
      maxWidth: '100vw',
      height: '100vh',
      data
    } as MatDialogConfig);
  }

  openSortDialog(sorts: Sort[], artifacts: Artifact[]) {
    const data: IToolbarActionData = {
      action: 'sort',
      sorts,
      artifacts
    };

    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  openFilterDialog(filters: Filter[], artifacts: Artifact[], booleanCriteria, supportsGlobalFilters = false) {
    const data: DesignerFilterDialogData = {
      filters,
      artifacts,
      booleanCriteria,
      supportsGlobalFilters,
      isInRuntimeMode: false
    }
    return this.dialog.open(DesignerFilterDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  openFilterPromptDialog(filters, analysis) {
    const data: DesignerFilterDialogData = {
      filters,
      artifacts: analysis.artifacts,
      isInRuntimeMode: true
    }
    return this.dialog.open(DesignerFilterDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  openPreviewDialog(analysis: Analysis) {
    const data = {
      analysis
    };
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
    };
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: '500px',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openDataFormatDialog(format: Format, type) {
    return this.dialog.open(DataFormatDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: {
        format,
        type
      }
    } as MatDialogConfig);
  }

  openDateFormatDialog(format: string, availableFormats) {
    return this.dialog.open(DateFormatDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: { format, availableFormats }
    } as MatDialogConfig);
  }

  openSaveDialog(analysis: Analysis) {
    const data: IToolbarActionData = {
      action: 'save',
      analysis
    };
    return this.dialog.open(ToolbarActionDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }

  openDeleteConfirmationDialog() {
    const deleteConfirmation = {
      title: 'Are you sure you want to delete this analysis?',
      content: 'Any published analyses will also be deleted.',
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    };
    return this.openConfirmationDialog(deleteConfirmation);
  }

  openDiscardConfirmationDialog() {
    const discardConfirmation = {
      title: 'There are unsaved changes',
      content: 'Do you want to discard unsaved changes and go back?',
      positiveActionLabel: 'Discard',
      negativeActionLabel: 'Cancel'
    };
    return this.openConfirmationDialog(discardConfirmation);
  }

  openQueryConfirmationDialog() {
    const queryConfirmation = {
      title: 'Are you sure you want to proceed?',
      content: 'If you save changes to sql query, you will not be able to go back to designer view for this analysis.',
      positiveActionLabel: 'Save',
      negativeActionLabel: 'Cancel'
    };
    return this.openConfirmationDialog(queryConfirmation);
  }

  openConfirmationDialog(data: ConfirmDialogData) {
    return this.dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig);
  }
}
