import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

@Component({
  selector: 'createfolder-dialog',
  templateUrl: './semantic-details-dialog.component.html',
  styleUrls: ['./semantic-details-dialog.component.scss']
})
export class SemanticDetailsDialogComponent implements OnInit {
  form: FormGroup;

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SemanticDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.form = new FormGroup({
      nameControl: new FormControl('', [
        Validators.required,
        Validators.maxLength(25)
      ])
    });
  }

  submit(form) {
    this.dialogRef.close(`${this.form.value.nameControl}`);
  }
}
