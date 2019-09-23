import {
  Component,
  OnInit,
  OnDestroy,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as fpGet from 'lodash/fp/get';
import * as includes from 'lodash/includes';
import * as split from 'lodash/split';
import * as compact from 'lodash/compact';
import * as omit from 'lodash/omit';
import * as get from 'lodash/get';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpMap from 'lodash/fp/map';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ObserveService } from '../../../../observe/services/observe.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { lessThan } from '../../../../../common/validators/less-than.validator';
// import { correctTimeInterval } from '../../../../../common/time-interval-parser/time-interval-parser';
import { NUMBER_TYPES, DATE_TYPES } from '../../../consts';

import {
  AlertConfig,
  AlertDefinition,
  AlertArtifact
} from '../../../alerts.interface';
import { ALERT_SEVERITY, ALERT_STATUS } from '../../../consts';
import { SubscriptionLike, of, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

const LAST_STEP_INDEX = 3;

const floatingPointRegex = '^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$';

const notificationsOptions = [
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
  attributeValues$: Observable<any>;
  selectedDatapod;
  selectedMetricsColumn;
  selectedAttributeColumn;
  selectedLookbackColumn;
  metricsList$;
  operators$;
  aggregations$;
  notifications$;
  thresholdValueLabel: string;
  alertSeverity = ALERT_SEVERITY;
  alertStatus = ALERT_STATUS;
  subscriptions: SubscriptionLike[] = [];
  endActionText = 'Add';
  endPayload: AlertConfig;
  showNotificationEmail = false;
  showOtherThresholdValue = false;
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
      const alertForm = this.transformAlertToFormObject(
        this.alertDefinition.alertConfig
      );
      const { datapodId } = alertForm;
      // update value of attributeName so the request gets sent for the values
      this.loadMetrics(datapodId).then(() => {
        this.alertRuleFormGroup.get('attributeName').updateValueAndValidity();
        this.alertDefFormGroup.patchValue(alertForm);
        this.alertMetricFormGroup.patchValue(alertForm);
        this.alertRuleFormGroup.patchValue(alertForm);
      });
      this.endActionText = 'Update';
      this.datapods$ = this._configureAlertService.getListOfDatapods$();
    } else {
      this.datapods$ = this._configureAlertService.getListOfDatapods$();
    }
    this.aggregations$ = this._configureAlertService
      .getAggregations()
      .pipe(
        map((aggregations: any[]) => [
          { id: 'none', name: 'None' },
          ...aggregations
        ])
      );
    this.operators$ = this._configureAlertService.getOperators();
    this.notifications$ = of(notificationsOptions);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  transformAlertToFormObject(alert) {
    const {
      notification: notificationsFromBackend,
      lookbackPeriod,
      aggregationType
    } = alert;
    const [stringValue, lookbackPeriodType] = split(lookbackPeriod, '-');
    const lookbackPeriodValue = parseInt(stringValue, 10);

    const notification = fpPipe(fpToPairs, fpMap(([key]) => key))(
      notificationsFromBackend
    );

    const notificationEmails = get(
      notificationsFromBackend,
      'email.recipients',
      []
    );

    return {
      ...omit(alert, [
        'notification',
        'lookbackPeriod',
        'sipQuery',
        'aggregate'
      ]),
      lookbackPeriodType,
      lookbackPeriodValue,
      notification,
      aggregationType: aggregationType || 'none',
      notificationEmails
    };
  }

  generateNotificationsForBackend(selectedNotifications, notificationEmails) {
    const notifications: any = {};

    if (includes(selectedNotifications, 'email')) {
      notifications.email = {
        recipients: notificationEmails
      };
    }
    return notifications;
  }

  createAlertForm() {
    this.alertDefFormGroup = this._formBuilder.group({
      alertRuleName: ['', Validators.required],
      alertRuleDescription: ['', Validators.required],
      alertSeverity: ['', [Validators.required]],
      notification: [[], [Validators.required]],
      notificationEmails: [[]],
      activeInd: [true]
    });

    const thresholdValuevalidators = [
      Validators.required,
      Validators.pattern(floatingPointRegex)
    ];
    const otherThresholdValuevalidators = [
      Validators.required,
      Validators.pattern(floatingPointRegex),
      lessThan('thresholdValue')
    ];
    this.alertMetricFormGroup = this._formBuilder.group({
      datapodId: ['', Validators.required],
      datapodName: [''],
      categoryId: [''],
      metricsColumn: ['', Validators.required],
      aggregationType: ['', Validators.required],
      operator: ['', Validators.required],
      thresholdValue: ['', thresholdValuevalidators],
      otherThresholdValue: ['', otherThresholdValuevalidators]
    });

    this.alertRuleFormGroup = this._formBuilder.group({
      lookbackColumn: ['', Validators.required],
      lookbackPeriodValue: ['', Validators.required],
      lookbackPeriodType: ['', Validators.required],
      attributeName: [''],
      attributeValue: ['']
    });

    this.alertDefFormGroup
      .get('notification')
      .valueChanges.subscribe(values => {
        this.showNotificationEmail = values.includes('email');
      });

    this.alertRuleFormGroup
      .get('attributeName')
      .valueChanges.subscribe(columnName => {
        if (!columnName) {
          return;
        }
        const { artifacts, id, esRepository } = this.selectedDatapod;
        const targetFilter = {
          artifactsName: artifacts[0].artifactName,
          semanticId: id,
          columnName: columnName,
          type: 'string',
          esRepository
        };
        this.attributeValues$ = this._observeService.getModelValues(
          targetFilter
        );
      });
    const otherThresholdValueControl = this.alertMetricFormGroup.get(
      'otherThresholdValue'
    );
    this.alertMetricFormGroup
      .get('operator')
      .valueChanges.subscribe(operator => {
        const isBetweenSelected = operator === 'BTW';
        this.showOtherThresholdValue = isBetweenSelected;
        if (isBetweenSelected) {
          otherThresholdValueControl.setValidators(
            otherThresholdValuevalidators
          );
          otherThresholdValueControl.updateValueAndValidity();
          this.thresholdValueLabel = 'To';
        } else {
          otherThresholdValueControl.clearValidators();
          otherThresholdValueControl.reset();
          this.thresholdValueLabel = 'Threshold value';
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
    this.alertMetricFormGroup.controls.metricsColumn.setValue('');
  }

  onSelectedMetricsColumn(selectedItem) {
    this.selectedMetricsColumn = selectedItem;
  }

  onDatapodSelected(selectedItem) {
    if (selectedItem) {
      this.alertMetricFormGroup.controls.datapodName.setValue(
        selectedItem.metricName
      );

      this.alertMetricFormGroup.controls.categoryId.setValue(
        selectedItem.categoryId || 'Default'
      );

      this.loadMetrics(selectedItem.id);
    }
  }

  onAttributeColumnSelected(selectedAttributeColumn) {
    this.selectedAttributeColumn = selectedAttributeColumn;
  }

  onLookbackColumnSelected(selectedLookbackColumn) {
    this.selectedLookbackColumn = selectedLookbackColumn;
  }

  loadMetrics(datapodId) {
    const metricsList = this._configureAlertService.getDatapod$(datapodId).pipe(
      tap(datapod => (this.selectedDatapod = datapod)),
      map(fpGet('artifacts.[0].columns'))
    );
    this.metricsList$ = metricsList;
    return metricsList.toPromise();
  }

  constructPayload() {
    const {
      alertRuleName,
      alertRuleDescription,
      alertSeverity,
      notification: selectedNotifications,
      notificationEmails,
      activeInd
    } = this.alertDefFormGroup.value;

    const {
      datapodId,
      datapodName,
      categoryId,
      metricsColumn,
      aggregationType,
      operator,
      thresholdValue,
      otherThresholdValue
    } = this.alertMetricFormGroup.value;

    const {
      lookbackColumn,
      lookbackPeriodValue,
      lookbackPeriodType,
      attributeName,
      attributeValue
    } = this.alertRuleFormGroup.value;

    const sipQuery = this.generateSipQuery();

    const notification = this.generateNotificationsForBackend(
      selectedNotifications,
      notificationEmails
    );
    const lookbackPeriod = `${lookbackPeriodValue}-${lookbackPeriodType}`;

    const alertConfigWithoutSipQuery: AlertConfig = {
      alertRuleName,
      alertRuleDescription,
      alertSeverity,
      notification,
      activeInd,
      datapodId,
      datapodName,
      categoryId,
      metricsColumn,
      aggregationType: aggregationType === 'none' ? null : aggregationType,
      operator,
      thresholdValue,
      otherThresholdValue: operator === 'BTW' ? otherThresholdValue : null,
      lookbackColumn,
      lookbackPeriod,
      attributeName,
      attributeValue
    };

    if (this.alertDefinition.action === 'update') {
      const {
        createdBy,
        createdTime,
        product
      } = this.alertDefinition.alertConfig;
      alertConfigWithoutSipQuery.createdBy = createdBy;
      alertConfigWithoutSipQuery.createdTime = createdTime;
      alertConfigWithoutSipQuery.product = product;
    } else {
      alertConfigWithoutSipQuery.product = 'SAWD000001';
    }

    const alertConfig: AlertConfig = {
      ...alertConfigWithoutSipQuery,
      sipQuery
    };
    this.endPayload = alertConfigWithoutSipQuery;
    return alertConfig;
  }

  generateSipQuery() {
    const {
      aggregationType,
      operator,
      thresholdValue,
      otherThresholdValue
    } = this.alertMetricFormGroup.value;

    const {
      lookbackPeriodValue,
      lookbackPeriodType,
      attributeName,
      attributeValue
    } = this.alertRuleFormGroup.value;

    const aggregate = aggregationType === 'none' ? null : aggregationType;

    const selectedMetricsColumn = this.selectedMetricsColumn;

    const metricsColumn: AlertArtifact = {
      dataField: selectedMetricsColumn.columnName,
      alias: selectedMetricsColumn.alias,
      columnName: selectedMetricsColumn.columnName,
      name: selectedMetricsColumn.name,
      displayName: selectedMetricsColumn.displayName,
      type: selectedMetricsColumn.type,
      format: selectedMetricsColumn.format,
      table: selectedMetricsColumn.table,
      visibleIndex: selectedMetricsColumn.visibleIndex,
      groupInterval: selectedMetricsColumn.groupInterval,
      aggregate
    };

    const { id, artifacts } = this.selectedDatapod;
    const { artifactName } = artifacts[0];

    const alertFilter = {
      type: selectedMetricsColumn.type,
      artifactsName: artifactName,
      model: {
        operator,
        value: thresholdValue,
        otherValue: operator === 'BTW' ? otherThresholdValue : null
      },
      isRuntimeFilter: false,
      isGlobalFilter: false,
      tableName: selectedMetricsColumn.tableName,
      columnName: selectedMetricsColumn.columnName,
      isOptional: false
    };

    const selectedLookbackColumn = this.selectedLookbackColumn || {};
    const lookbackFilter = {
      type: 'date',
      artifactsName: artifactName,
      model: {
        presetCal: `${lookbackPeriodValue}-${lookbackPeriodType}`
      },
      isRuntimeFilter: false,
      isGlobalFilter: false,
      tableName: selectedLookbackColumn.tableName,
      columnName: selectedLookbackColumn.columnName,
      isOptional: false
    };

    const selectedAttributeColumn = this.selectedAttributeColumn || {};
    const hasAttributeFilter = attributeName && attributeValue;
    const stringFilter = hasAttributeFilter
      ? {
          type: 'string',
          artifactsName: artifactName,
          model: {
            operator: 'EQ',
            modelValues: [attributeValue]
          },
          isRuntimeFilter: false,
          isGlobalFilter: false,
          tableName: selectedAttributeColumn.tableName,
          columnName: selectedAttributeColumn.columnName,
          isOptional: false
        }
      : null;

    const { indexName, type, storageType } = this.selectedDatapod.esRepository;
    const store = {
      dataStore: `${indexName}/${type}`,
      storageType
    };

    const sipQuery = {
      artifacts: [{ artifactName, fields: [metricsColumn] }],
      booleanCriteria: 'AND',
      filters: compact([alertFilter, lookbackFilter, stringFilter]),
      sorts: [],
      joins: [],
      store,
      semanticId: id
    };

    return sipQuery;
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
