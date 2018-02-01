import {
  Component,
  Input,
  Inject
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  Analysis,
  ArtifactColumns
} from '../types';
import { DesignerService } from '../designer.service';

const template = require('./designer-preview-dialog.component.html');
require('./designer-preview-dialog.component.scss');

@Component({
  selector: 'designer-preview-dialog',
  template
})
export class DesignerPreviewDialogComponent {

  public previewData = null;
  public artifactColumns: ArtifactColumns;

  constructor(
    private _dialogRef: MatDialogRef<DesignerPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {analysis: Analysis},
    private _designerService: DesignerService
  ) {
    const analysis = data.analysis;
    switch (data.analysis.type) {
    case 'pivot':
      this.artifactColumns = analysis.artifacts[0].columns;
      break;
    default:
      break;
    }
  }

  ngOnInit() {
    const analysis = this.data.analysis;
    this._designerService.getDataForAnalysisPreview(analysis)
      .then(data => {
        switch (analysis.type) {
        case 'pivot':
          this.previewData = this._designerService.parseData(data.data, analysis.sqlBuilder);
          break;
        }
      });
  }

  close() {
    this._dialogRef.close();
  }
}
