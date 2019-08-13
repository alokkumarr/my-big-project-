import {
  Component,
  OnInit,
  OnDestroy,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as filter from 'lodash/filter';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { timeIntervalValidator } from '../../../../../common/validators';
import { NUMBER_TYPES, DATE_TYPES } from '../../../consts';

import { AlertConfig, AlertDefinition } from '../../../alerts.interface';
import { ALERT_SEVERITY, ALERT_STATUS } from '../../../consts';
import { SubscriptionLike, of } from 'rxjs';
import { map } from 'rxjs/operators';

const notifications = [
  {
    value: 'email',
    label: 'email',
    enabled: true
  },
  {
    value: 'slack',
    label: 'slack',
    enabled: false
  },
  {
    value: 'webhook',
    label: 'web hooks',
    enabled: false
  }
];
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
  metricsListWithoutMonitoringEntity$;
  metricsListWithoutEntity$;
  operators$;
  aggregations$;
  notifications$;
  alertSeverity = ALERT_SEVERITY;
  alertStatus = ALERT_STATUS;
  subscriptions: SubscriptionLike[] = [];
  endActionText = 'Add';
  endPayload: AlertConfig;

  constructor(
    private _formBuilder: FormBuilder,
    public _configureAlertService: ConfigureAlertService,
    public _notify: ToastService
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
    this.aggregations$ = this._configureAlertService.getAggregations();
    this.operators$ = this._configureAlertService.getOperators();
    this.notifications$ = of(notifications);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  createAlertForm() {
    this.alertDefFormGroup = this._formBuilder.group({
      alertRuleName: ['', [Validators.required, Validators.maxLength(18)]],
      alertRuleDescription: [
        '',
        [Validators.required, Validators.maxLength(36)]
      ],
      alertSeverity: ['', [Validators.required]],
      notification: ['', [Validators.required]],
      activeInd: [true]
    });

    this.alertMetricFormGroup = this._formBuilder.group({
      datapodId: ['', Validators.required],
      datapodName: [''],
      categoryId: [''],
      monitoringEntity: ['', Validators.required],
      entityName: ['', Validators.required],
      lookbackColumn: ['', Validators.required],
      lookbackPeriod: [
        '',
        [Validators.required, Validators.maxLength(20), timeIntervalValidator]
      ]
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

  onDatapodChanged() {
    this.alertMetricFormGroup.controls.monitoringEntity.setValue('');
  }

  onDatapodSelected(selectedItem) {
    if (selectedItem) {
      this.alertMetricFormGroup.controls.datapodName.setValue(
        selectedItem.metricName
      );

      this.alertMetricFormGroup.controls.categoryId.setValue(
        selectedItem.categoryId || 'Default'
      );

      this.metricsList$ = this._configureAlertService.getMetricsInDatapod$(
        selectedItem.id
      );
      this.metricsListWithoutMonitoringEntity$ = this.metricsList$.pipe(
        map(columns =>
          filter(
            columns,
            col =>
              col.columnName ===
              this.alertMetricFormGroup.get('monitoringEntity')
          )
        )
      );
      this.metricsListWithoutEntity$ = this.metricsList$.pipe(
        map(columns =>
          filter(
            columns,
            col =>
              col.columnName === this.alertMetricFormGroup.get('entityName')
          )
        )
      );
    }
  }

  constructPayload() {
    const partialAlertConfig = {
      ...this.alertDefFormGroup.value,
      ...this.alertMetricFormGroup.value,
      ...this.alertRuleFormGroup.value,
      product: 'SAWD000001'
    };

    const alertConfig: AlertConfig = {
      ...partialAlertConfig,
      sipQuery: { artifacts: [], filters: [] } // TODO: generate sipQuery
    };

    this.endPayload = alertConfig;
    return alertConfig;
  }

  createAlert() {
    const payload = this.constructPayload();
    const createSubscriber = this._configureAlertService
      .createAlert(payload)
      .subscribe((data: any) => {
        this.notifyOnAction(data);
      });
    this.subscriptions.push(createSubscriber);
  }

  updateAlert() {
    const payload = this.constructPayload();
    const alertID = this.alertDefinition.alertConfig.alertRulesSysId;
    const updateSubscriber = this._configureAlertService
      .updateAlert(alertID, payload)
      .subscribe((data: any) => {
        this.notifyOnAction(data);
      });
    this.subscriptions.push(updateSubscriber);
  }

  notifyOnAction(data) {
    this._notify.success(data.message);
    this.onAddAlert.emit(true);
  }

  numericMetricFilter(metric) {
    return NUMBER_TYPES.includes(metric.type);
  }

  dateMetricFilter(metric) {
    return DATE_TYPES.includes(metric.type);
  }
}
