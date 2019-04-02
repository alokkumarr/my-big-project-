import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

@Component({
  selector: 'add-alert',
  templateUrl: './add-alert.component.html',
  styleUrls: ['./add-alert.component.scss']
})
export class AddAlertComponent implements OnInit {
  addAlertFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {}

  ngOnInit() {
    this.addAlertFormGroup = this._formBuilder.group({
      alertFormArray: this._formBuilder.array([
        this._formBuilder.group({
          alertName: ['', Validators.required]
        }),
        this._formBuilder.group({
          datapodName: ['', Validators.required]
        }),
        this._formBuilder.group({
          metricName: ['', Validators.required]
        }),
        this._formBuilder.group({
          operatorName: ['', Validators.required]
        }),
        this._formBuilder.group({
          thresholdValue: ['', Validators.required]
        })
      ])
    });
  }

  // Returns a FormArray with the name 'alertFormArray'.
  get alertFormArray(): AbstractControl | null {
    return this.addAlertFormGroup.get('alertFormArray');
  }

  createAlert() {}
}
