declare const ace: any;

import {
  Component,
  ViewChild,
  OnDestroy,
  Inject,
  Optional
} from '@angular/core';
import { AceEditorComponent } from 'ng2-ace-editor';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import * as startCase from 'lodash/startCase';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as cloneDeep from 'lodash/cloneDeep';
import * as lowerCase from 'lodash/lowerCase';
import { of, Observable } from 'rxjs';

import {
  SUPPORTED_AGGREGATES,
  Operator as SupportedOperator,
  parseExpression
} from 'src/app/common/utils/expression-parser';
import { isUnique } from 'src/app/common/validators';

enum MODE {
  edit,
  create
}
@Component({
  selector: 'derived-metric',
  templateUrl: './derived-metric.component.html',
  styleUrls: ['./derived-metric.component.scss']
})
export class DerivedMetricComponent implements OnDestroy {
  @ViewChild('editor') editor: AceEditorComponent;

  editorOptions = {
    displayIndentGuides: true,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true,
    wrap: 'free',
    wrapBehavioursEnabled: true,
    fontSize: 16
  };

  expressionForm: FormGroup;
  expressionError = '';
  langTools = ace.require('ace/ext/language_tools');
  mode = MODE.create;
  originalColumnName: string;
  allModes = MODE;
  allAggregatesSupported = SUPPORTED_AGGREGATES;
  allOperatorsSupported = Object.values(SupportedOperator);
  completions: any[] = [];

  get columnName(): FormControl {
    return this.expressionForm.get('columnName') as FormControl;
  }

  get formula(): FormControl {
    return this.expressionForm.get('formula') as FormControl;
  }

  constructor(
    private fb: FormBuilder,
    @Optional()
    @Inject(MAT_DIALOG_DATA)
    private data: { artifactColumn: any; columns: any[] },
    public dialogRef: MatDialogRef<DerivedMetricComponent>
  ) {
    if (get(this.data, 'artifactColumn.columnName')) {
      this.originalColumnName = this.data.artifactColumn.columnName;
      this.mode = MODE.edit;
    }
    this.createForm();
    this.generateCompletions();
    this.addCompletionsToEditor();
  }

  ngOnDestroy() {
    /* Set ace editor autocompletion to default to not
       leak suggestions to other places where ace is
       being used. */
    this.langTools.setCompleters([
      this.langTools.snippetCompleter,
      this.langTools.textCompleter,
      this.langTools.keyWordCompleter
    ]);
  }

  /*
         ███████╗ ██████╗ ██████╗ ███╗   ███╗
         ██╔════╝██╔═══██╗██╔══██╗████╗ ████║
         █████╗  ██║   ██║██████╔╝██╔████╔██║
         ██╔══╝  ██║   ██║██╔══██╗██║╚██╔╝██║
         ██║     ╚██████╔╝██║  ██║██║ ╚═╝ ██║
         ╚═╝      ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝
         ███╗   ███╗███████╗████████╗██╗  ██╗ ██████╗ ██████╗ ███████╗
         ████╗ ████║██╔════╝╚══██╔══╝██║  ██║██╔═══██╗██╔══██╗██╔════╝
         ██╔████╔██║█████╗     ██║   ███████║██║   ██║██║  ██║███████╗
         ██║╚██╔╝██║██╔══╝     ██║   ██╔══██║██║   ██║██║  ██║╚════██║
         ██║ ╚═╝ ██║███████╗   ██║   ██║  ██║╚██████╔╝██████╔╝███████║
         ╚═╝     ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝
*/

  createForm() {
    this.expressionForm = this.fb.group({
      columnName: [
        {
          value: get(this.data, 'artifactColumn.columnName', ''),
          disabled: this.mode === MODE.edit
        },
        Validators.required,
        isUnique(
          this.isDuplicateColumnName.bind(this),
          val => val,
          this.originalColumnName
        )
      ],
      formula: [
        get(this.data, 'artifactColumn.formula', ''),
        Validators.required
      ], // formula string entered by user
      expression: [
        get(this.data, 'artifactColumn.expression', ''),
        Validators.required
      ] // stringified json expression corresponding to formula
    });
  }

  /**
   * Called everytime text changes in ace editor.
   *
   * @param {string} expression
   * @memberof DerivedMetricComponent
   */
  expressionChanged(expression: string) {
    this.expressionForm.get('formula').setValue(expression);
    this.expressionError = '';
  }

  isDuplicateColumnName(columnName: string): Observable<boolean> {
    const existingColumnNames = this.data.columns.map(col =>
      lowerCase(col.columnName)
    );
    return of(existingColumnNames.includes(lowerCase(columnName)));
  }

  /**
   * Check if the formula entered by user doesn't give
   * error on json conversion. If it doesn't, close the
   * dialog successfully.
   *
   * @memberof DerivedMetricComponent
   */
  submit() {
    try {
      const expressionJSON = parseExpression(
        this.expressionForm.get('formula').value
      );
      this.expressionError = '';
      this.expressionForm
        .get('expression')
        .setValue(JSON.stringify(expressionJSON));
    } catch (e) {
      this.expressionError = e.toString();
      /* In case of error, set expression to empty to make
      sure form is invalid since expression is a required field */
      this.expressionForm.get('expression').setValue('');
    } finally {
      if (this.expressionForm.valid) {
        this.dialogRef.close({
          ...this.expressionForm.getRawValue(),
          displayName: startCase(this.expressionForm.getRawValue().columnName)
        });
      }
    }
  }

  /*
         ███████╗██████╗ ██╗████████╗ ██████╗ ██████╗
         ██╔════╝██╔══██╗██║╚══██╔══╝██╔═══██╗██╔══██╗
         █████╗  ██║  ██║██║   ██║   ██║   ██║██████╔╝
         ██╔══╝  ██║  ██║██║   ██║   ██║   ██║██╔══██╗
         ███████╗██████╔╝██║   ██║   ╚██████╔╝██║  ██║
         ╚══════╝╚═════╝ ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
         ███╗   ███╗███████╗████████╗██╗  ██╗ ██████╗ ██████╗ ███████╗
         ████╗ ████║██╔════╝╚══██╔══╝██║  ██║██╔═══██╗██╔══██╗██╔════╝
         ██╔████╔██║█████╗     ██║   ███████║██║   ██║██║  ██║███████╗
         ██║╚██╔╝██║██╔══╝     ██║   ██╔══██║██║   ██║██║  ██║╚════██║
         ██║ ╚═╝ ██║███████╗   ██║   ██║  ██║╚██████╔╝██████╔╝███████║
         ╚═╝     ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚══════╝
*/

  /**
   * Adds @text to editor at current cursor position.
   * Additionally, it may set the cursor back by
   * @backtrack number of characters. Handy for
   * inserting templates, where we want the cursor
   * to be somewhere in middle upon finish.
   *
   * @param {string} text
   * @param {number} backtrack
   * @memberof DerivedMetricComponent
   */
  addText(text: string, backtrack = 0) {
    const editor = this.editor.getEditor();
    editor.session.insert(editor.getCursorPosition(), text);

    if (backtrack) {
      /* Move cursor back by the specified number of characters. */
      const position: {
        row: number;
        column: number;
      } = editor.getCursorPosition();
      editor.moveCursorToPosition({
        ...position,
        column: Math.max(position.column - backtrack, 0)
      });
    }

    this.expressionError = '';
    editor.focus();
  }

  /**
   * Generates auto complete suggestion objects from list of
   * columns.
   *
   * @memberof DerivedMetricComponent
   */
  generateCompletions() {
    const columns = get(this.data, 'columns', []);
    columns.forEach(({ expression, columnName, displayName }) => {
      if (expression) {
        /* Don't add derived metrics to autocompletions.
        We don't support adding derived metrics to other
        derived metrics yet. */
        return;
      }

      const caption = displayName || columnName;
      this.completions.push({
        name: caption,
        value: caption,
        caption: caption,
        meta: 'column',
        score: 1000,

        /* Custom attribute stores column name.
        This is used to insert this string when matched instead
        of 'value' attribute of this completion. */
        insertValue: columnName
      });
    });
  }

  /**
   * Adds generated completions to ace editor. Uses custom attribute
   * 'insertValue' from completions if present to add to insert in
   * editor.
   *
   * @memberof DerivedMetricComponent
   */
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
}
