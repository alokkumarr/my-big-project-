declare const require: any;
import {
  Component,
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
  public analysis: Analysis;
  public dataLoader: (options: {}) => Promise<{data: any[], totalCount: number}>;

  constructor(
    private _dialogRef: MatDialogRef<DesignerPreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {analysis: Analysis},
    private _designerService: DesignerService
  ) {
    this.analysis = data.analysis;
    switch (this.analysis.type) {
    case 'pivot':
      this.artifactColumns = this.analysis.artifacts[0].columns;
      break;
    case 'report':
    case 'esReport':
      this.dataLoader = (options = {}) => this._designerService.getDataForAnalysisPreview(this.analysis, options)
        .then(({data, count}) => ({data: data, totalCount: count}));
      break;
    }
  }

  ngOnInit() {
    const analysis = this.data.analysis;
    switch (analysis.type) {
    case 'pivot':
      this._designerService.getDataForAnalysisPreview(analysis, {})
        .then(data => {
          this.previewData = this._designerService.parseData(data.data, analysis.sqlBuilder);
        });
      break;
    }

  }

  close() {
    this._dialogRef.close();
  }
}
