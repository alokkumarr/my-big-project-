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
import * as split from 'lodash/split';
import * as fpGet from 'lodash/fp/get';
import * as dropRight from 'lodash/dropRight';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
// import { correctTimeInterval } from '../../../../../common/time-interval-parser/time-interval-parser';
import { NUMBER_TYPES, DATE_TYPES } from '../../../consts';

import { AlertConfig, AlertDefinition } from '../../../alerts.interface';
import { ALERT_SEVERITY, ALERT_STATUS, DATE_PRESETS } from '../../../consts';
import { SubscriptionLike, of, Observable, combineLatest } from 'rxjs';
import { map, tap } from 'rxjs/operators';

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
  datapods$: Observable<any>;
  selectedDatapod;
  selectedMonitoringEntity;
  selectedEntityName;
  datePresets = dropRight(DATE_PRESETS);
  metricsList$;
  metricsListWithoutMonitoringEntity;
  metricsListWithoutEntity;
  operators$;
  aggregations$;
  notifications$;
  alertSeverity = ALERT_SEVERITY;
  alertStatus = ALERT_STATUS;
  subscriptions: SubscriptionLike[] = [];
  endActionText = 'Add';
  endPayload: AlertConfig;
  showNotificationEmail = false;

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
      notification: [[], [Validators.required]],
      notificationEmail: [''],
      activeInd: [true]
    });

    this.alertMetricFormGroup = this._formBuilder.group({
      datapodId: ['', Validators.required],
      datapodName: [''],
      categoryId: [''],
      monitoringEntity: ['', Validators.required],
      entityName: ['', Validators.required],
      lookbackColumn: ['', Validators.required],
      lookbackPeriod: ['', Validators.required]
    });

    this.alertRuleFormGroup = this._formBuilder.group({
      aggregation: ['', Validators.required],
      operator: ['', Validators.required],
      thresholdValue: [
        '',
        [Validators.required, Validators.pattern('^[0-9]*$')]
      ]
    });

    // const notificationEmail = this.alertDefFormGroup.get('notificationEmail');
    this.alertDefFormGroup
      .get('notification')
      .valueChanges.subscribe(values => {
        console.log('values', values);
        if (values.includes('email')) {
          this.showNotificationEmail = true;
        } else {
          this.showNotificationEmail = false;
        }
      });
  }

  // onLookbackPeriodBlur() {
  //   const control = this.alertMetricFormGroup.get('lookbackPeriod');
  //   const controlValue = control.value;
  //   const correctedValue = correctTimeInterval(controlValue);
  //   control.setValue(correctedValue);
  // }

  onDatapodChanged() {
    this.alertMetricFormGroup.controls.monitoringEntity.setValue('');
  }

  onSelectedMonitoringEntity(selectedItem) {
    this.selectedMonitoringEntity = selectedItem;
  }

  onSelectedEntityName(selectedItem) {
    this.selectedEntityName = selectedItem;
  }

  onDatapodSelected(selectedItem) {
    if (selectedItem) {
      this.alertMetricFormGroup.controls.datapodName.setValue(
        selectedItem.metricName
      );

      this.alertMetricFormGroup.controls.categoryId.setValue(
        selectedItem.categoryId || 'Default'
      );

      this.metricsList$ = this._configureAlertService
        .getDatapod$(selectedItem.id)
        .pipe(
          tap(datapod => (this.selectedDatapod = datapod)),
          map(fpGet('artifacts.[0].columns'))
        );

      const monitoringEntityControl = this.alertMetricFormGroup.get(
        'monitoringEntity'
      );
      combineLatest(this.metricsList$, monitoringEntityControl.valueChanges)
        .pipe(
          map(([columns, monitoringEntity]) =>
            filter(columns, col => col.columnName !== monitoringEntity)
          )
        )
        .subscribe(
          metrics => (this.metricsListWithoutMonitoringEntity = metrics)
        );
      const entityNameControl = this.alertMetricFormGroup.get('entityName');
      combineLatest(this.metricsList$, entityNameControl.valueChanges)
        .pipe(
          map(([columns, entityName]) =>
            filter(
              columns,
              col =>
                col.columnName !== entityName && this.numericMetricFilter(col)
            )
          )
        )
        .subscribe(metrics => (this.metricsListWithoutEntity = metrics));
      monitoringEntityControl.updateValueAndValidity();
      entityNameControl.updateValueAndValidity();
    }
  }

  constructPayload() {
    const {
      datapodId,
      datapodName,
      categoryId,
      lookbackColumn,
      lookbackPeriod
    } = this.alertMetricFormGroup.value;

    const sipQuery = this.generateSipQuery();

    const alertConfigWithoutSipQuery = {
      ...this.alertDefFormGroup.value,
      datapodId,
      datapodName,
      categoryId,
      lookbackColumn,
      lookbackPeriod,
      product: 'SAWD000001'
    };

    const alertConfig: AlertConfig = {
      ...alertConfigWithoutSipQuery,
      sipQuery
    };

    this.endPayload = alertConfigWithoutSipQuery;
    return alertConfig;
  }

  generateSipQuery() {
    const {
      aggregation,
      operator,
      thresholdValue
    } = this.alertRuleFormGroup.value;

    const selectedEntityName = this.selectedEntityName;

    const entityName = {
      dataField: split(selectedEntityName.columnName, '.')[0],
      area: 'x-axis',
      alias: selectedEntityName.alias,
      columnName: selectedEntityName.columnName,
      // name: string, // take out name, and see if it works
      displayName: selectedEntityName.displayName,
      type: selectedEntityName.type
    };

    const selectedMonitoringEntity = this.selectedMonitoringEntity;

    const monitoringEntity = {
      dataField: selectedMonitoringEntity.columnName,
      area: 'y-axis',
      columnName: selectedMonitoringEntity.columnName,
      // name: integer,  // take out name, and see if it works
      displayName: selectedMonitoringEntity.displayName,
      type: selectedMonitoringEntity.type,
      aggregate: aggregation
    };

    const { artifactName } = this.selectedDatapod.artifacts[0];

    const alertFilter = {
      type: selectedMonitoringEntity.type,
      artifactName,
      model: {
        operator,
        value: thresholdValue
      }
    };

    return {
      artifacts: [{ artifactName, fields: [entityName, monitoringEntity] }],
      filters: [alertFilter]
    };
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
