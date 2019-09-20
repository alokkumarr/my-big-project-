declare const ace: any;

import {
  Component,
  ViewChild,
  OnDestroy,
  Inject,
  Optional
} from '@angular/core';
import { AceEditorComponent } from 'ng2-ace-editor';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { parseExpression } from 'src/app/common/utils/expression-parser';
import * as startCase from 'lodash/startCase';

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
}
