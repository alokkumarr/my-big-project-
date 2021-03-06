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
import { AceEditorComponent } from 'ng2-ace-editor';
import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'sql-script',
  templateUrl: './sql-script.component.html',
  styleUrls: ['./sql-script.component.scss']
})
export class SqlScriptComponent implements OnDestroy, AfterViewInit {
  @Output() onExecute = new EventEmitter<any>();
  @Output() onCreate = new EventEmitter<any>();

  @ViewChild('editor', { static: true }) editor: AceEditorComponent;

  public _artifacts: Array<any>;
  public query: string;
  public readOnlyMode: boolean = false; // tslint:disable-line

  public editorOptions = {
    // tslint:disable-line
    displayIndentGuides: true,
    enableBasicAutocompletion: true, // the editor completes the statement when you hit Ctrl + Space
    enableLiveAutocompletion: true, // the editor completes the statement while you are typing
    showPrintMargin: false, // hides the vertical limiting strip
    maxLines: Infinity,
    fontSize: '100%', // ensures that the editor fits in the environme
    wrap: 'free',
    wrapBehavioursEnabled: true,
    cursorStyle: 'ace'
  };
  public langTools = ace.require('ace/ext/language_tools');
  public completions = [];

  constructor(private workBench: WorkbenchService) {}

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

  @Input()
  set artifacts(tables) {
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
        const caption = column.name;
        this.completions.push({
          name: caption,
          value: caption,
          caption: caption,
          meta: 'column',
          score: 1000,

          /* Custom attribute stores column name.
          This is used to insert this string when matched instead
          of 'value' attribute of this completion. */
          insertValue: caption
        });
      });
    });
  }

  addCompletionsToEditor() {
    const self = this;
    const artifactsCompleter = {
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

        const matchingCompletions = withCompleter.filter(
          match =>
            (match.caption || match.name)
              .toLowerCase()
              .indexOf(prefix.toLowerCase()) >= 0
        );

        return callback(null, cloneDeep(matchingCompletions));
      },

      insertMatch: (editor, data) => {
        editor.completer.insertMatch({
          value: data.insertValue || data.value || data
        });
      }
    };
    this.langTools.addCompleter(artifactsCompleter);
  }

  queryUpdated(query) {}

  onCreateEmitter() {
    this.onCreate.emit(this.query);
  }

  /**
   * Executes the queryy against the datset and emits the result to preview component.
   *
   * @memberof SqlScriptComponent
   */
  executeQuery() {
    this.workBench.executeSqlQuery(this.query).subscribe(data => {
      this.onExecute.emit(data);
    });
  }

  /**
   * Shows the preview of an action from list in editor.
   * shifts the editor in readonly mode.
   *
   * @param {string} action
   * @memberof SqlScriptComponent
   */
  viewAction(action: string) {
    this.readOnlyMode = true;
    this.query = action;
  }
}
