import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';
import { DATAPOD_CATEGORIES } from '../../../consts';

@Component({
  selector: 'semantic-details-dialog',
  templateUrl: './semantic-details-dialog.component.html',
  styleUrls: ['./semantic-details-dialog.component.scss']
})
export class SemanticDetailsDialogComponent implements OnInit {
  form: FormGroup;
  dataPodCategories = DATAPOD_CATEGORIES;

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SemanticDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    const defaultCategory = DATAPOD_CATEGORIES[0].name;
    this.form = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(25)
      ]),
      category: new FormControl(defaultCategory)
    });
  }

  submit() {
    const {name, category} = this.form.value;
    this.dialogRef.close({name, category});
  }
}
