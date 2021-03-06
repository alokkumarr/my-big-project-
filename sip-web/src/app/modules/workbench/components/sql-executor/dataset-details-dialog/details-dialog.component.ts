import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'details-dialog',
  templateUrl: './details-dialog.component.html',
  styleUrls: ['./details-dialog.component.scss']
})
export class DetailsDialogComponent implements OnInit {
  form: FormGroup;
  public folNamePattern = '[A-Za-z0-9]+';

  constructor(
    public formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<DetailsDialogComponent>
  ) {}

  ngOnInit() {
    this.form = this.formBuilder.group({
      nameControl: [
        '',
        [
          Validators.required,
          Validators.pattern(this.folNamePattern),
          Validators.minLength(3),
          Validators.maxLength(18)
        ]
      ],
      descControl: [
        '',
        [Validators.required, Validators.minLength(5), Validators.maxLength(50)]
      ]
    });
  }

  submit(form) {
    const details = {
      name: form.value.nameControl,
      desc: form.value.descControl
    };
    this.dialogRef.close(details);
  }

  onClose() {
    this.dialogRef.close(false);
  }
}
