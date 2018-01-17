declare const ace: any;
import {
  Component,
  OnDestroy,
  Input,
  Output,
  Inject,
  ViewChild,
  EventEmitter
} from '@angular/core';

import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';

import 'brace/theme/eclipse';
import 'brace/ext/language_tools';
import 'brace/mode/sql';
import { AceEditorComponent } from 'ng2-ace-editor';

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
export class AnalyzeReportQueryComponent implements OnDestroy {
  @Input() model: any;
  @Output() onSave = new EventEmitter<any>();

  @ViewChild('editor') editor: AceEditorComponent;

  private _artifacts: Array<any>;
  private editorOptions = {
    displayIndentGuides: true,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    fontSize: 16
  };
  private langTools = ace.acequire('ace/ext/language_tools');
  private completions = [];

  constructor(
    @Inject('$mdDialog') private _$mdDialog: any
  ) { }

  /* Before exiting, reset ace completers to default.
     This removes any custom completers added to ace. */
  ngOnDestroy() {
    this.langTools.setCompleters([
      this.langTools.snippetCompleter,
      this.langTools.textCompleter,
      this.langTools.keyWordCompleter
    ]);
  }

  @Input() set artifacts (tables) {
    if (!isEmpty(tables)) {
      this._artifacts = tables;
      this.generateCompletions();
      this.addCompletionsToEditor();
    }
  }

  generateCompletions() {
    this._artifacts.forEach(table => {
      this.completions.push({
        name: table.artifactName,
        value: table.artifactName,
        meta: 'table',
        score: 1001
      });

      table.columns.forEach(column => {
        this.completions.push({
          name: column.columnName,
          value: column.columnName,
          caption: column.alias || column.aliasName || column.displayName || column.columnName,
          meta: 'column',
          score: 1000
        });
      });
    });
  }

  addCompletionsToEditor() {
    var artifactsCompleter = {
      getCompletions: (editor, session, pos, prefix, callback) => {
        if (prefix.length === 0) {
          return callback(null, cloneDeep(this.completions));
        }

        var matchingCompletions = this.completions.filter(
          match => (match.caption || match.name).toLowerCase().indexOf(prefix.toLowerCase()) >= 0
        )

        return callback(null, cloneDeep(matchingCompletions));
      }
    }
    this.langTools.addCompleter(artifactsCompleter);
  }

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
