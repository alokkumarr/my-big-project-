import {
  Component,
  OnInit,
  OnDestroy,
  EventEmitter,
  Output,
  Input,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatStepper } from '@angular/material';
import * as fpGet from 'lodash/fp/get';
import * as includes from 'lodash/includes';
import * as cloneDeep from 'lodash/cloneDeep';
import * as split from 'lodash/split';
import * as compact from 'lodash/compact';
import * as omit from 'lodash/omit';
import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as range from 'lodash/range';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpMap from 'lodash/fp/map';

import { ConfigureAlertService } from '../../../services/configure-alert.service';
import { ObserveService } from '../../../../observe/services/observe.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { lessThan } from '../../../../../common/validators';
// import { correctTimeInterval } from '../../../../../common/time-interval-parser/time-interval-parser';
import { NUMBER_TYPES, DATE_TYPES } from '../../../consts';
import { entityNameErrorMessage } from './../../../../../common/validators/field-name-rule.validator';

import {
  AlertConfig,
  AlertDefinition,
  AlertArtifact
} from '../../../alerts.interface';
import { ALERT_SEVERITY, ALERT_STATUS } from '../../../consts';
import { SubscriptionLike, of, Observable, combineLatest } from 'rxjs';
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
  @ViewChild('addAlertStepper') addAlertStepper: MatStepper;
  alertDefFormGroup: FormGroup;
  alertMetricFormGroup: FormGroup;
  alertRuleFormGroup: FormGroup;
  datapods$: Observable<any>;
  attributeValues$: Observable<any>;
  monitoringTypes$: Observable<any>;
  selectedDatapod;
  selectedMetricsColumn;
  selectedAttributeColumn;
  selectedLookbackColumn;
  metricsList$;
  metricsList;
  operators$;
  aggregations$;
  notifications$;
  thresholdValueLabel = 'Threshold value';
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
        this.onSelectedMetricsColumn(alertForm.metricsColumn);
        this.onLookbackColumnSelected(alertForm.lookbackColumn);
        const allSteps = range(3);
        allSteps.forEach(() => this.addAlertStepper.next());
        allSteps.forEach(() => this.addAlertStepper.previous());
      });
      this.endActionText = 'Update';
      this.datapods$ = this._configureAlertService.getListOfDatapods$();
    } else {
      this.datapods$ = this._configureAlertService.getListOfDatapods$();
    }
    this.monitoringTypes$ = this._configureAlertService.getMonitoringTypeList();

    const monitoringTypeValues = this.alertMetricFormGroup.get('monitoringType')
      .valueChanges;

    this.aggregations$ = combineLatest(
      monitoringTypeValues,
      this._configureAlertService.getAggregations()
    ).pipe(
      map(([monitoringType = {}, aggregations]) => [
        monitoringType === 'AGGREGATION_METRICS'
          ? null
          : { id: 'none', name: 'None' },
        ...aggregations
      ]),
      map(compact)
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

  displayErrorMessage(state) {
    return entityNameErrorMessage(state);
  }

  createAlertForm() {
    this.alertDefFormGroup = this._formBuilder.group({
      alertRuleName: ['', [Validators.required,
        Validators.maxLength(30),
        Validators.pattern(/[`~!@#$%^&*()+={}|"':;?/>.<,*:/?[\]\\]/g)]],
      alertRuleDescription: [''],
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
      Validators.pattern(floatingPointRegex)
    ];

    this.alertMetricFormGroup = this._formBuilder.group(
      {
        datapodId: ['', Validators.required],
        datapodName: [''],
        categoryId: [''],
        metricsColumn: [{ value: '', disabled: true }, Validators.required],
        monitoringType: [null, Validators.required],
        aggregationType: ['', Validators.required],
        operator: ['', Validators.required],
        thresholdValue: ['', thresholdValuevalidators],
        otherThresholdValue: ['', otherThresholdValuevalidators]
      },
      { validators: [lessThan('otherThresholdValue', 'thresholdValue')] }
    );

    this.alertRuleFormGroup = this._formBuilder.group({
      lookbackColumn: [{ value: '', disabled: true }, Validators.required],
      lookbackPeriodValue: ['', Validators.required],
      lookbackPeriodType: ['', Validators.required],
      triggerOnLookback: [false],
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

    const aggregationTypeControl = this.alertMetricFormGroup.get(
      'aggregationType'
    );
    this.alertMetricFormGroup
      .get('monitoringType')
      .valueChanges.subscribe(monitoringType => {
        if (monitoringType === 'AGGREGATION_METRICS') {
          // aggregation cannot be none
          const aggregate = aggregationTypeControl.value;
          if (aggregate === 'none') {
            aggregationTypeControl.setValue(null);
          }
        }
      });

    const metricsColumnControl = this.alertMetricFormGroup.get('metricsColumn');
    const lookbackColumnControl = this.alertRuleFormGroup.get('lookbackColumn');
    this.alertMetricFormGroup
      .get('datapodId')
      .statusChanges.subscribe(datapodStatus => {
        if (datapodStatus === 'VALID') {
          metricsColumnControl.enable();
          lookbackColumnControl.enable();
        } else {
          metricsColumnControl.disable();
          lookbackColumnControl.disable();
        }
      });
  }

  // onLookbackPeriodBlur() {
  //   const control = this.alertMetricFormGroup.get('lookbackPeriod');
  //   const controlValue = control.value;
  //   const correctedValue = correctTimeInterval(controlValue);
  //   control.setValue(correctedValue);
  // }

  onSelectedMetricsColumn(metricColumnName) {
    const selectedMetric = find(
      this.metricsList,
      ({ columnName }) => metricColumnName === columnName
    );
    this.selectedMetricsColumn = selectedMetric;
  }

  onDatapodSelected(datapodId) {
    this.alertMetricFormGroup.controls.metricsColumn.setValue('');
    if (datapodId) {
      this.loadMetrics(datapodId);
    }
  }

  onAttributeColumnSelected(selectedAttributeColumn) {
    this.selectedAttributeColumn = selectedAttributeColumn;
  }

  onLookbackColumnSelected(LookbackColumnName) {
    const selectedMetric = find(
      this.metricsList,
      ({ columnName }) => LookbackColumnName === columnName
    );
    this.selectedLookbackColumn = selectedMetric;
  }

  loadMetrics(datapodId) {
    this.metricsList$ = this._configureAlertService.getDatapod$(datapodId).pipe(
      tap(datapod => {
        this.selectedDatapod = datapod;
        this.alertMetricFormGroup.controls.datapodName.setValue(
          datapod.metricName
        );
        this.alertMetricFormGroup.controls.categoryId.setValue(
          datapod.categoryId || 'Default'
        );
      }),
      map(fpGet('artifacts.[0].columns')),
      tap(metricsList => (this.metricsList = metricsList))
    );
    return this.metricsList$.toPromise();
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
      monitoringType,
      aggregationType,
      operator,
      thresholdValue,
      otherThresholdValue
    } = this.alertMetricFormGroup.value;

    const {
      lookbackColumn,
      lookbackPeriodValue,
      lookbackPeriodType,
      triggerOnLookback,
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
      triggerOnLookback,
      attributeName,
      attributeValue,
      monitoringType
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
    this.endPayload = this.transformAlertConfigToShow(
      alertConfigWithoutSipQuery
    );
    return alertConfig;
  }

  transformAlertConfigToShow(alertConfigWithoutSipQuery) {
    const alertConfigToShow = cloneDeep(alertConfigWithoutSipQuery);
    const [trimmedAttributeName] = split(alertConfigToShow.attributeName, '.');
    alertConfigToShow.attributeName = trimmedAttributeName;
    return alertConfigToShow;
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

    // if there is no aggregate, then there should be no alertFilter
    const alertFilter = aggregate
      ? {
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
          isOptional: false,
          isAggregationFilter: true
        }
      : null;

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
