declare const require: any;
declare const ace: any;

import {
  Component,
  OnDestroy,
  AfterViewInit,
  Input,
  Output,
  ViewChild,
  EventEmitter
} from '@angular/core';

import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';
import * as map from 'lodash/map';

import 'brace/theme/eclipse';
import 'brace/ext/language_tools';
import 'brace/mode/sql';
import { AceEditorComponent } from 'ng2-ace-editor';

const template = require('./analyze-report-query.component.html');
require('./analyze-report-query.component.scss');

@Component({
  selector: 'analyze-report-query',
  template
})
export class AnalyzeReportQueryComponent implements OnDestroy, AfterViewInit {
  @Input() query: string;
  @Output() change = new EventEmitter<string>();

  @ViewChild('editor') editor: AceEditorComponent;

  private _artifacts: Array<any>;
  private editorOptions = { // tslint:disable-line
    displayIndentGuides: true,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    wrap: 'free',
    wrapBehavioursEnabled: true,
    fontSize: 16
  };
  private langTools = ace.acequire('ace/ext/language_tools');
  private completions = [];

  ngAfterViewInit() {
    setTimeout(() => {
      this.editor.getEditor().focus();
      this.editor.getEditor().resize();
    }, 100);
  }

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
        const caption = column.alias || column.aliasName || column.displayName || column.columnName;
        this.completions.push({
          name: caption,
          value: caption,
          caption: caption,
          meta: 'column',
          score: 1000,

          /* Custom attribute stores column name.
          This is used to insert this string when matched instead
          of 'value' attribute of this completion. */
          insertValue: column.columnName
        });
      });
    });
  }

  addCompletionsToEditor() {
    const self = this;
    var artifactsCompleter = {
      getCompletions: (editor, session, pos, prefix, callback) => {
        /* Add reference to this completer in each match. Ace editor
        uses this reference to call the custom 'insertMatch' method of
        this completer. */
        const withCompleter = map(self.completions, completion => {
          completion.completer = artifactsCompleter;
          return completion;
        });

        if (prefix.length === 0) {
          return callback(null, cloneDeep(withCompleter));
        }

        var matchingCompletions = withCompleter.filter(
          match => (match.caption || match.name).toLowerCase().indexOf(prefix.toLowerCase()) >= 0
        )

        return callback(null, cloneDeep(matchingCompletions));
      },

      insertMatch: (editor, data) => {
        editor.completer.insertMatch({value: data.insertValue || data.value || data});
      }
    }
    this.langTools.addCompleter(artifactsCompleter);
  }

  queryUpdated(query) {
    this.change.emit(query);
  }
}
