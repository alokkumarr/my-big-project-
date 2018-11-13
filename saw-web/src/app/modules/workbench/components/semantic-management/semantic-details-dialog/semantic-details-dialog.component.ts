import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

const categories = [{
  name: 'default',
  icon: 'category-default'
}, {
  name: 'errors',
  icon: 'category-errors'
}, {
  name: 'orders',
  icon: 'category-orders'
}, {
  name: 'sessions',
  icon: 'category-sessions'
}, {
  name: 'subscribers',
  icon: 'category-subscribers'
}, {
  name: 'usage',
  icon: 'category-usage'
}, {
  name: 'events',
  icon: 'calendar-events'
}, {
  name: 'retention',
  icon: 'calendar-retention'
}, {
  name: 'funnel',
  icon: 'calendar-funnel'
}];

@Component({
  selector: 'semantic-details-dialog',
  templateUrl: './semantic-details-dialog.component.html',
  styleUrls: ['./semantic-details-dialog.component.scss']
})
export class SemanticDetailsDialogComponent implements OnInit {
  form: FormGroup;
  categories = categories;

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SemanticDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    const defaultCategory = categories[0].name;
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
