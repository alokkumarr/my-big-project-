import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import { AddAlertService } from '../../../services/add-alert.service';
import { AlertConfig } from '../../../alerts.interface';
import { ALERT_AGGREGATIONS, ALERT_OPERATORS } from '../../../consts';
@Component({
  selector: 'add-alert',
  templateUrl: './add-alert.component.html',
  styleUrls: ['./add-alert.component.scss']
})
export class AddAlertComponent implements OnInit {
  addAlertFormGroup: FormGroup;
  datapods$;
  metricsList$;
  alertAggregations = ALERT_AGGREGATIONS;
  alertOperators = ALERT_OPERATORS;
  datapodName: string;

  constructor(
    private _formBuilder: FormBuilder,
    public _addAlertService: AddAlertService
  ) {}

  ngOnInit() {
    this.addAlertFormGroup = this._formBuilder.group({
      alertFormArray: this._formBuilder.array([
        this._formBuilder.group({
          alertName: ['', [Validators.required, Validators.maxLength(18)]],
          alertDescription: [
            '',
            [Validators.required, Validators.maxLength(36)]
          ],
          alertSeverity: ['', [Validators.required]],
          activeInd: ['true']
        }),
        this._formBuilder.group({
          datapodId: ['', Validators.required],
          metricName: ['', Validators.required]
        }),
        this._formBuilder.group({
          aggregation: ['', Validators.required],
          operator: ['', Validators.required],
          thresholdValue: [
            '',
            [Validators.required, Validators.pattern('^[0-9]*$')]
          ]
        })
      ])
    });
    this.datapods$ = this._addAlertService.getListOfDatapods$();
  }

  // Returns a FormArray with the name 'alertFormArray'.
  get alertFormArray(): AbstractControl | null {
    return this.addAlertFormGroup.get('alertFormArray');
  }

  onDatapodSelected(event) {
    this.alertFormArray
      .get([1])
      .get('metricName')
      .setValue('');
    if (event.selectedItem) {
      this.datapodName = event.selectedItem.metricName;
      this.metricsList$ = this._addAlertService.getMetricsInDatapod$(
        event.selectedItem.id
      );
    }
  }

  createAlert() {}
}
