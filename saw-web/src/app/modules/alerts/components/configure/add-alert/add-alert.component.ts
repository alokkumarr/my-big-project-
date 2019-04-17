import {
  Component,
  OnInit,
  OnDestroy,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

import { AlertConfig, AlertDefinition } from '../../../alerts.interface';
import {
  ALERT_AGGREGATIONS,
  ALERT_OPERATORS,
  ALERT_SEVERITY,
  ALERT_STATUS
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
  alertStatus = ALERT_STATUS;
  subscriptions: SubscriptionLike[] = [];
  endActionText = 'Add';

  constructor(
    private _formBuilder: FormBuilder,
    public _configureAlertService: ConfigureAlertService,
    private _notify: ToastService
  ) {
    this.createAlertForm();
  }

  @Input() alertDefinition: AlertDefinition;
  @Output() onAddAlert = new EventEmitter<any>();

  ngOnInit() {
    if (this.alertDefinition.action === 'update') {
      this.endActionText = 'Update';
      this.alertDefFormGroup.patchValue(this.alertDefinition.alertConfig);
      this.alertMetricFormGroup.patchValue(this.alertDefinition.alertConfig);
      this.alertRuleFormGroup.patchValue(this.alertDefinition.alertConfig);
      this.endActionText = 'Update';
    }
    this.datapods$ = this._configureAlertService.getListOfDatapods$();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  createAlertForm() {
    this.alertDefFormGroup = this._formBuilder.group({
      alertName: ['', [Validators.required, Validators.maxLength(18)]],
      alertDescription: ['', [Validators.required, Validators.maxLength(36)]],
      alertSeverity: ['', [Validators.required]],
      activeInd: [true]
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
  }

  onDatapodSelected(selectedItem) {
    // this.alertMetricFormGroup.controls.monitoringEntity.setValue('');
    if (selectedItem) {
      this.alertMetricFormGroup.controls.datapodName.setValue(
        selectedItem.metricName
      );

      this.metricsList$ = this._configureAlertService.getMetricsInDatapod$(
        selectedItem.id
      );
    }
  }

  constructPayload() {
    const payload: AlertConfig = {
      ...this.alertDefFormGroup.value,
      ...this.alertMetricFormGroup.value,
      ...this.alertRuleFormGroup.value,
      categoryId: '1',
      product: 'SAWD000001'
    };

    return payload;
  }

  createAlert() {
    const payload = this.constructPayload();

    const createSubscriber = this._configureAlertService
      .createAlert(payload)
      .subscribe((data: any) => {
        this._notify.success(data.message);
        this.onAddAlert.emit(true);
      });
    this.subscriptions.push(createSubscriber);
  }

  updateAlert() {
    const payload = this.constructPayload();
    const alertID = this.alertDefinition.alertConfig.alertRulesSysId;
    const updateSubscriber = this._configureAlertService
      .updateAlert(alertID, payload)
      .subscribe((data: any) => {
        this._notify.success(data.message);
        this.onAddAlert.emit(true);
      });
    this.subscriptions.push(updateSubscriber);
  }
}
