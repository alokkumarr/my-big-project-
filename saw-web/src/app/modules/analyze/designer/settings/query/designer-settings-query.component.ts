import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import * as isUndefined from 'lodash/isUndefined';
import { AnalyzeDialogService } from '../../../services/analyze-dialog.service';
import { Artifact, AnalysisReport } from '../../types';

@Component({
  selector: 'designer-settings-query',
  templateUrl: './designer-settings-query.component.html',
  styleUrls: ['./designer-settings-query.component.scss']
})
export class DesignerSettingsQueryComponent implements OnInit {
  @Output() save = new EventEmitter<any>();
  @Output() change = new EventEmitter<string>();
  @Output() submit = new EventEmitter<any>();
  @Input() analysis: AnalysisReport;
  @Input() artifacts: Artifact[];
  constructor(private _analyzeDialogService: AnalyzeDialogService) {}

  ngOnInit() {
    if (isUndefined(this.analysis.queryManual)) {
      this.analysis.queryManual = this.analysis.query;
    }
  }

  onQueryChange(query) {
    this.analysis.queryManual = query;
    this.change.emit(query);
  }

  doSubmit() {
    const analysis = this.analysis;
    if (analysis.queryManual) {
      analysis.query = analysis.queryManual;
    } else {
      analysis.queryManual = analysis.query;
    }
    this.save.emit();
  }

  submitQuery() {
    if (!this.analysis.edit) {
      this._analyzeDialogService
        .openQueryConfirmationDialog()
        .afterClosed()
        .subscribe(result => {
          if (result) {
            this.doSubmit();
          }
        });
    } else {
      this.doSubmit();
    }
  }
}
