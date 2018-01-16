import {
  Component,
  OnInit,
  Input,
  Output,
  Inject,
  EventEmitter
} from '@angular/core';

const template = require('./analyze-report-query.component.html');
require('./analyze-report-query.component.scss');

const WARN_DIALOG = {
  title: 'Are you sure you want to proceed?',
  content: 'If you save changes to sql query, you will not be able to go back to designer view for this analysis.'
};

@Component({
  selector: 'analyze-report-query',
  template
})
export class AnalyzeReportQueryComponent implements OnInit {
  @Input() model: any;
  @Output() onSave = new EventEmitter<any>();

  constructor(
    @Inject('$mdDialog') private _$mdDialog: any
  ) { }

  ngOnInit() { }

  warnUser() {
    const confirm = this._$mdDialog.confirm()
      .title(WARN_DIALOG.title)
      .textContent(WARN_DIALOG.content)
      .multiple(true)
      .ok('Save')
      .cancel('Cancel');

    return this._$mdDialog.show(confirm);
  }

  doSubmit() {
    this.model.edit = true;
    this.model.queryManual = this.model.query;
    this.onSave.emit(this.model);
  }

  submitQuery() {
    if (!this.model.edit) {
      this.warnUser().then(() => {
        this.doSubmit();
      }, () => {
        // do nothing if user hits cancel
      });
    } else {
      this.doSubmit();
    }
  }
}
