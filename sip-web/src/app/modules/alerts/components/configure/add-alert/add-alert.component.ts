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
import * as includes from 'lodash/includes';
import * as compact from 'lodash/compact';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ObserveService } from '../../../../observe/services/observe.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
// import { correctTimeInterval } from '../../../../../common/time-interval-parser/time-interval-parser';
import { NUMBER_TYPES, DATE_TYPES } from '../../../consts';

import {
  AlertConfig,
  AlertDefinition,
  AlertArtifact
} from '../../../alerts.interface';
import { ALERT_SEVERITY, ALERT_STATUS } from '../../../consts';
import { SubscriptionLike, of, Observable, combineLatest } from 'rxjs';
import { map, tap } from 'rxjs/operators';

const LAST_STEP_INDEX = 3;

const notifications = [
  {
    value: 'email',
    label: 'email',
    enabled: true
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
  attributeFilterValues$: Observable<any>;
  selectedDatapod;
  selectedMonitoringEntity;
  selectedEntityName;
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
  lookbackPeriodTypes = ['minute', 'hour', 'day', 'week', 'month'];

  constructor(
    private _formBuilder: FormBuilder,
    public _configureAlertService: ConfigureAlertService,
    public _notify: ToastService,
    public _observeService: ObserveService
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
    this.aggregations$ = this._configureAlertService
      .getAggregations()
      .pipe(
        map((aggregations: any[]) => [
          { id: 'none', name: 'None' },
          ...aggregations
        ])
      );
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
      notificationEmails: [[]],
      activeInd: [true]
    });

    this.alertMetricFormGroup = this._formBuilder.group({
      datapodId: ['', Validators.required],
      datapodName: [''],
      categoryId: [''],
      monitoringEntity: ['', Validators.required],
      aggregation: ['', Validators.required],
      operator: ['', Validators.required],
      thresholdValue: [
        '',
        [Validators.required, Validators.pattern('^[0-9]*$')]
      ]
    });

    this.alertRuleFormGroup = this._formBuilder.group({
      entityName: ['', Validators.required],
      lookbackColumn: ['', Validators.required],
      lookbackPeriodValue: ['', Validators.required],
      lookbackPeriodType: ['', Validators.required],
      attributeFilterColumn: [''],
      attributeFilterValue: ['']
    });

    this.alertDefFormGroup
      .get('notification')
      .valueChanges.subscribe(values => {
        if (values.includes('email')) {
          this.showNotificationEmail = true;
        } else {
          this.showNotificationEmail = false;
        }
      });

    this.alertRuleFormGroup
      .get('attributeFilterColumn')
      .valueChanges.subscribe(column => {
        if (!column) {
          return;
        }
        const { artifacts, id, esRepository } = this.selectedDatapod;
        const targetFilter = {
          artifactsName: artifacts[0].artifactName,
          semanticId: id,
          columnName: column.columnName,
          type: column.type,
          esRepository
        };
        this.attributeFilterValues$ = this._observeService.getModelValues(
          targetFilter
        );
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
      const entityNameControl = this.alertRuleFormGroup.get('entityName');
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
      categoryId
    } = this.alertMetricFormGroup.value;

    const {
      lookbackColumn,
      lookbackPeriodValue,
      lookbackPeriodType,
      attributeFilterColumn,
      attributeFilterValue
    } = this.alertRuleFormGroup.value;

    const sipQueryInfo = {
      lookbackPeriod: {
        value: `${lookbackPeriodValue}-${lookbackPeriodType}`,
        column: lookbackColumn
      },
      attributeFilter: {
        value: attributeFilterValue,
        column: attributeFilterColumn
      }
    };

    const sipQuery = this.generateSipQuery(sipQueryInfo);

    const {
      alertRuleName,
      alertRuleDescription,
      alertSeverity,
      notification: selectedNotifications,
      notificationEmails,
      activeInd
    } = this.alertDefFormGroup.value;

    const notification = [];

    if (includes(selectedNotifications, 'email')) {
      notification.push({
        type: 'email',
        recipients: notificationEmails
      });
    }

    const alertConfigWithoutSipQuery = {
      alertRuleName,
      alertRuleDescription,
      alertSeverity,
      notification,
      activeInd,
      datapodId,
      datapodName,
      categoryId,
      product: 'SAWD000001'
    };
    console.log({ sipQuery });

    const alertConfig: AlertConfig = {
      ...alertConfigWithoutSipQuery,
      sipQuery
    };

    this.endPayload = alertConfigWithoutSipQuery;
    return alertConfig;
  }

  generateSipQuery(sipQueryInfo) {
    const {
      aggregation,
      operator,
      thresholdValue
    } = this.alertMetricFormGroup.value;

    const aggregate = aggregation === 'none' ? null : aggregation;
    const {
      lookbackPeriod
      // attributeFilter
    } = sipQueryInfo;

    const selectedEntityName = this.selectedEntityName;

    const entityName: AlertArtifact = {
      dataField: split(selectedEntityName.columnName, '.')[0],
      area: 'x-axis',
      alias: selectedEntityName.alias,
      columnName: selectedEntityName.columnName,
      // name: '', // take out name, and see if it works
      displayName: selectedEntityName.displayName,
      type: selectedEntityName.type
    };

    const selectedMonitoringEntity = this.selectedMonitoringEntity;

    const monitoringEntity: AlertArtifact = {
      dataField: selectedMonitoringEntity.columnName,
      area: 'y-axis',
      alias: selectedEntityName.alias,
      columnName: selectedMonitoringEntity.columnName,
      // name: '', // take out name, and see if it works
      displayName: selectedMonitoringEntity.displayName,
      type: selectedMonitoringEntity.type,
      aggregate
    };

    const { artifactName } = this.selectedDatapod.artifacts[0];

    const alertFilter = {
      type: selectedMonitoringEntity.type,
      artifactsName: artifactName,
      model: {
        operator,
        value: thresholdValue
      }
    };

    const lookbackPeriodCol = lookbackPeriod.column;
    const lookbackFilter = {
      type: lookbackPeriodCol.type,
      artifactsName: artifactName,
      model: {
        presetCal: lookbackPeriod.value
      }
    };

    // const attributeFilterCol = attributeFilter.column;
    // const hasAttributeFilter = attributeFilter.column && attributeFilter.value;
    // const stringFilter = hasAttributeFilter
    //   ? {
    //       type: attributeFilterCol.type,
    //       artifactsName: artifactName,
    //       model: {
    //         operator: 'EQ',
    //         value: attributeFilter.value
    //       }
    //     }
    //   : null;

    return {
      artifacts: [{ artifactName, fields: [entityName, monitoringEntity] }],
      filters: compact([
        alertFilter,
        lookbackFilter
        // stringFilter
      ])
    };
  }

  onStepperSelectionChange(event) {
    if (event.selectedIndex === LAST_STEP_INDEX) {
      this.constructPayload();
    }
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

  stringMetricFilter(metric) {
    return 'string' === metric.type;
  }
}
