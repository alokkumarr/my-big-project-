declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  Inject
} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';

import { ConfirmDialogComponent } from '../../../../../../common/components/confirm-dialog';
import { ConfirmDialogData } from '../../../../../../common/types';
import {
  Artifact,
  AnalysisReport
} from '../../types';

const template = require('./designer-settings-query.component.html');
require('./designer-settings-query.component.scss');

const CONFIRM_DIALOG_DATA: ConfirmDialogData = {
  title: 'Are you sure you want to proceed?',
  content: 'If you save changes to sql query, you will not be able to go back to designer view for this analysis.',
  positiveActionLabel: 'Save',
  negativeActionLabel: 'Cancel'
};
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
    private _dialog: MatDialog
  ) {}

  onQueryChange(query) {
    this.change.emit(query);
    this.analysis.queryManual = query;
  }

  warnUser() {
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: CONFIRM_DIALOG_DATA
    } as MatDialogConfig);
  }

  doSubmit() {
    this.analysis.edit = true;
    this.analysis.query = this.analysis.queryManual;
    this.save.emit();
  }

  submitQuery() {
    if (!this.analysis.edit) {
      this.warnUser().afterClosed().subscribe((result) => {
        if (result) {
          this.doSubmit();
        }
      });
    } else {
      this.doSubmit();
    }
  }
}
