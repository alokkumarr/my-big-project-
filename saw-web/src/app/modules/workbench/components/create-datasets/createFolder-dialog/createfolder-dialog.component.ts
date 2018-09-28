
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';

const style = require('./createfolder-dialog.component.scss');

@Component({
  selector: 'createfolder-dialog',
  templateUrl: './createfolder-dialog.component.html',
  styles: [style]
})

export class CreatefolderDialogComponent {
  form: FormGroup;
  folNamePattern = '[A-Za-z0-9]+';

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreatefolderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) { }

  ngOnInit() {
    this.form = new FormGroup({
      folderNameControl: new FormControl('', [
        Validators.required, Validators.pattern(this.folNamePattern)
      ])
    });
  }

  submit(form) {
    this.dialogRef.close(`${this.form.value.folderNameControl}`);
  }
}
