import {
  Component,
  Input,
  Output,
  EventEmitter,
  Inject
} from '@angular/core';
import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';
import {
  Artifact,
  AnalysisReport
} from '../../types';

const template = require('./designer-settings-query.component.html');
require('./designer-settings-query.component.scss');

@Component({
  selector: 'designer-settings-query',
  template
})
export class DesignerSettingsQueryComponent {
  @Output() save = new EventEmitter<any>();
  @Output() change = new EventEmitter<string>();
  @Output() submit = new EventEmitter<any>();
  @Input() analysis: AnalysisReport;
  @Input() artifacts: Artifact[];

  constructor(
    private _analyzeDialogService: AnalyzeDialogService
  ) {}

  onQueryChange(query) {
    this.change.emit(query);
    this.analysis.queryManual = query;
  }

  doSubmit() {
    const analysis = this.analysis;
    analysis.edit = true;
    if (analysis.queryManual) {
      analysis.query = analysis.queryManual;
    } else {
      analysis.queryManual = analysis.query;
    }
    this.save.emit();
  }

  submitQuery() {
    if (!this.analysis.edit) {
      this._analyzeDialogService.openQueryConfirmationDialog().afterClosed().subscribe(result => {
        if (result) {
          this.doSubmit();
        }
      });
    } else {
      this.doSubmit();
    }
  }
}
