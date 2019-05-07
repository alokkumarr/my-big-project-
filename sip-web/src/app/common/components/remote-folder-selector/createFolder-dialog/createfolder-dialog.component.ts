import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

@Component({
  selector: 'createfolder-dialog',
  templateUrl: './createfolder-dialog.component.html',
  styleUrls: ['./createfolder-dialog.component.scss']
})
export class CreatefolderDialogComponent implements OnInit {
  form: FormGroup;
  folNamePattern = '[A-Za-z0-9]+';

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<CreatefolderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.form = new FormGroup({
      folderNameControl: new FormControl('', [
        Validators.required,
        Validators.pattern(this.folNamePattern)
      ])
    });
  }

  submit(form) {
    this.dialogRef.close(`${this.form.value.folderNameControl}`);
  }
}
