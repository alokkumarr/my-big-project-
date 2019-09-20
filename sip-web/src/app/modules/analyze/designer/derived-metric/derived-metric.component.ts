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
import { parseExpression } from 'src/app/common/utils/expression-parser';
import * as startCase from 'lodash/startCase';
import {
  SUPPORTED_AGGREGATES,
  Operator as SupportedOperator
} from 'src/app/common/utils/expression-parser';

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
    // tslint:disable-line
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
  allModes = MODE;
  allAggregatesSupported = SUPPORTED_AGGREGATES;
  allOperatorsSupported = Object.values(SupportedOperator);

  constructor(
    private fb: FormBuilder,
    @Optional()
    @Inject(MAT_DIALOG_DATA)
    private data,
    public dialogRef: MatDialogRef<DerivedMetricComponent>
  ) {
    if (this.data && this.data.columnName) {
      this.mode = MODE.edit;
    } else {
      this.data = { columnName: '', formula: '', expression: '' };
    }
    this.createForm();
  }

  createForm() {
    this.expressionForm = this.fb.group({
      columnName: [
        {
          value: this.data.columnName || '',
          disabled: this.mode === MODE.edit
        },
        Validators.required
      ],
      formula: [this.data.formula || '', Validators.required], // formula string entered by user
      expression: [this.data.expression || '', Validators.required] // stringified json expression corresponding to formula
    });
  }

  get columnName(): FormControl {
    return this.expressionForm.get('columnName') as FormControl;
  }

  get formula(): FormControl {
    return this.expressionForm.get('formula') as FormControl;
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
}
