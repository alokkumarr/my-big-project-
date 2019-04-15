import {
  Component,
  OnInit,
  OnDestroy,
  EventEmitter,
  Output
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

import { AlertConfig } from '../../../alerts.interface';
import {
  ALERT_AGGREGATIONS,
  ALERT_OPERATORS,
  ALERT_SEVERITY
} from '../../../consts';
import { SubscriptionLike } from 'rxjs';
@Component({
  selector: 'add-alert',
  templateUrl: './add-alert.component.html',
  styleUrls: ['./add-alert.component.scss']
})
export class AddAlertComponent implements OnInit, OnDestroy {
  alertDefFormGroup: FormGroup;
  alertMetricFormGroup: FormGroup;
  alertRuleFormGroup: FormGroup;
  datapods$;
  metricsList$;
  alertAggregations = ALERT_AGGREGATIONS;
  alertOperators = ALERT_OPERATORS;
  alertSeverity = ALERT_SEVERITY;
  subscriptions: SubscriptionLike[] = [];

  constructor(
    private _formBuilder: FormBuilder,
    public _configureAlertService: ConfigureAlertService,
    private _notify: ToastService
  ) {}

  @Output() onAddAlert = new EventEmitter<any>();

  ngOnInit() {
    this.alertDefFormGroup = this._formBuilder.group({
      ruleName: ['', [Validators.required, Validators.maxLength(18)]],
      ruleDescriptions: ['', [Validators.required, Validators.maxLength(36)]],
      alertSeverity: ['', [Validators.required]],
      activeInd: ['true']
    });

    this.alertMetricFormGroup = this._formBuilder.group({
      datapodId: ['', Validators.required],
      datapodName: [''],
      monitoringEntity: ['', Validators.required]
    });

    this.alertRuleFormGroup = this._formBuilder.group({
      aggregation: ['', Validators.required],
      operator: ['', Validators.required],
      thresholdValue: [
        '',
        [Validators.required, Validators.pattern('^[0-9]*$')]
      ]
    });

    this.datapods$ = this._configureAlertService.getListOfDatapods$();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onDatapodSelected(selectedItem) {
    this.alertMetricFormGroup.controls.monitoringEntity.setValue('');
    if (selectedItem) {
      this.alertMetricFormGroup.controls.datapodName.setValue(
        selectedItem.metricName
      );

      this.metricsList$ = this._configureAlertService.getMetricsInDatapod$(
        selectedItem.id
      );
    }
  }

  createAlert() {
    const payload: AlertConfig = {
      ...this.alertDefFormGroup.value,
      ...this.alertMetricFormGroup.value,
      ...this.alertRuleFormGroup.value,
      categoryId: '1',
      product: 'SAWD000001'
    };

    const createSubscriber = this._configureAlertService
      .createAlert(payload)
      .subscribe((data: any) => {
        this._notify.success(data.message);
        this.onAddAlert.emit(true);
      });
    this.subscriptions.push(createSubscriber);
  }
}
